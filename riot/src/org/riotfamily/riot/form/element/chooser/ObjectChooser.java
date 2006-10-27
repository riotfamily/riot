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
	
	private ChooserController chooserController;
	
	private EditorRepository editorRepository;
	
	private DisplayDefinition targetEditorDefinition;
	
	
	public void setTargetEditorId(String targetEditorId) {
		this.targetEditorId = targetEditorId;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
	protected void afterFormSet() {
		if (chooserController == null) {
			Assert.isInstanceOf(ListableBeanFactory.class, beanFactory,
					"Not a ListableBeanFactory");
			
			ListableBeanFactory lbf = (ListableBeanFactory) beanFactory;
			chooserController = (ChooserController) 
					BeanFactoryUtils.beanOfTypeIncludingAncestors(
					lbf, ChooserController.class);
			
			Assert.notNull(chooserController, 
					"No ChooserListController found in BeanFactory");
		}
		
		log.debug("Looking up editor: " + targetEditorId);
		editorRepository = chooserController.getEditorRepository();
		EditorDefinition editor = editorRepository.getEditorDefinition(
				targetEditorId);
		
		Assert.notNull(editor, "No such EditorDefinition: " + targetEditorId);
		targetEditorDefinition = getDisplayDefinition(editor);
	}

	private DisplayDefinition getDisplayDefinition(EditorDefinition def) {
		if (def instanceof DisplayDefinition) {
			return (DisplayDefinition) def;
		}
		else if (def instanceof ListDefinition) {
			ListDefinition listDef = (ListDefinition) def;
			return listDef.getDisplayDefinition();
		}
		else {
			throw new IllegalArgumentException(
					"Neither a List- nor FormDefinition: " + def);
		}
	}
	
	protected Object loadBean(String objectId) {
		return EditorDefinitionUtils.loadBean(targetEditorDefinition, objectId);
	}
	
	protected String getDisplayName(Object object) {
		return targetEditorDefinition.getLabel(object);
	}

	protected String getChooserUrl() {
		return chooserController.getUrl(targetEditorId);
	}
	
}
