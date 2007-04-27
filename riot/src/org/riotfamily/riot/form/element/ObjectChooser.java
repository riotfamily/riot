/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.form.element;

import org.riotfamily.forms.element.support.AbstractChooser;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.ListDefinition;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.util.Assert;

public class ObjectChooser extends AbstractChooser 
		implements BeanFactoryAware {

	private String targetEditorId;
		
	private BeanFactory beanFactory;
	
	private EditorRepository editorRepository;
	
	private ListDefinition targetListDefinition;
	
	private EditorDefinition targetEditorDefinition;
	
	
	public void setTargetEditorId(String targetEditorId) {
		this.targetEditorId = targetEditorId;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
	protected void afterFormSet() {
		if (editorRepository == null) {
			Assert.isInstanceOf(ListableBeanFactory.class, beanFactory,
					"Not a ListableBeanFactory");
			
			ListableBeanFactory lbf = (ListableBeanFactory) beanFactory;
			editorRepository = (EditorRepository) 
					BeanFactoryUtils.beanOfTypeIncludingAncestors(
					lbf, EditorRepository.class);
			
			Assert.notNull(editorRepository, 
					"No EditorRepository found in BeanFactory");
		}
		
		log.debug("Looking up editor: " + targetEditorId);
		targetEditorDefinition = editorRepository.getEditorDefinition(
				targetEditorId);
		
		Assert.notNull(targetEditorDefinition, "No such EditorDefinition: "
					+ targetEditorId);

		if (targetEditorDefinition instanceof ListDefinition) {
			targetListDefinition = (ListDefinition) targetEditorDefinition;
			targetEditorDefinition = targetListDefinition.getDisplayDefinition();
		}
		else {
			targetListDefinition = EditorDefinitionUtils
					.getParentListDefinition(targetEditorDefinition);
		}
	}

	protected Object loadBean(String objectId) {
		return EditorDefinitionUtils.loadBean(targetListDefinition, objectId);
	}
	
	protected String getDisplayName(Object object) {
		return object != null ? targetEditorDefinition.getLabel(object) : null;
	}

	protected String getChooserUrl() {
		EditorDefinition rootList =
			EditorDefinitionUtils.getRootListDefinition(targetListDefinition);
		if (rootList == null) {
			rootList = targetListDefinition;
		}
		
		return rootList.getEditorUrl(null, null) 
				+ "?choose=" + targetEditorDefinition.getId();
	}
	
}
