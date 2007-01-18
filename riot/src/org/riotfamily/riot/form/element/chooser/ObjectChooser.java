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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.form.element.chooser;

import org.riotfamily.forms.element.support.AbstractChooser;
import org.riotfamily.riot.editor.DisplayDefinition;
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
	
	private DisplayDefinition targetDisplayDefinition;
	
	
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
		EditorDefinition editor = editorRepository.getEditorDefinition(
				targetEditorId);
		
		Assert.notNull(editor, "No such EditorDefinition: " + targetEditorId);

		if (editor instanceof DisplayDefinition) {
			targetDisplayDefinition = (DisplayDefinition) editor;
			targetListDefinition = EditorDefinitionUtils.getParentListDefinition(editor); 
		}
		else if (editor instanceof ListDefinition) {
			targetListDefinition = (ListDefinition) editor;
			targetDisplayDefinition = targetListDefinition.getDisplayDefinition();
		}
		else {
			throw new IllegalArgumentException(
					"Neither a List- nor DisplayDefinition: " + editor);
		}
		
	}

	protected Object loadBean(String objectId) {
		return EditorDefinitionUtils.loadBean(targetListDefinition, objectId);
	}
	
	protected String getDisplayName(Object object) {
		return object != null ? targetDisplayDefinition.getLabel(object) : null;
	}

	protected String getChooserUrl() {
		return targetListDefinition.getEditorUrl(null, null) 
				+ "?choose=" + targetDisplayDefinition.getId();
	}
	
}
