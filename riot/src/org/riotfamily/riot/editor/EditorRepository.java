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
package org.riotfamily.riot.editor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.collection.TypeDifferenceComparator;
import org.riotfamily.common.i18n.AdvancedMessageCodesResolver;
import org.riotfamily.common.xml.ConfigurationEventListener;
import org.riotfamily.forms.FormRepository;
import org.riotfamily.riot.editor.ui.EditorController;
import org.riotfamily.riot.list.ListRepository;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.util.Assert;

/**
 *
 */
public class EditorRepository extends ApplicationObjectSupport {

	private static Log log = LogFactory.getLog(EditorRepository.class);
	
	private Map editorDefinitions = new HashMap();

	private GroupDefinition rootGroupDefinition;
	
	private Collection editorControllers;
	
	private HashMap editorControllersByDefinitionClass = new HashMap();
	
	private ListRepository listRepository;
	
	private FormRepository formRepository;

	private AdvancedMessageCodesResolver messageCodesResolver;

	
	public GroupDefinition getRootGroupDefinition() {
		return this.rootGroupDefinition;
	}

	public void setRootGroupDefinition(GroupDefinition rootGroupDefinition) {
		this.rootGroupDefinition = rootGroupDefinition;
	}

	protected Map getEditorDefinitions() {
		return this.editorDefinitions;
	}

	public void addEditorDefinition(EditorDefinition editorDefinition) {
		String id = editorDefinition.getId();
		Assert.notNull(id, "Editor must have an id.");
		EditorDefinition existingEditor = getEditorDefinition(id);
		if (existingEditor != null) {
			log.info("Overwriting editor " + id);
			if (existingEditor.getParentEditorDefinition() instanceof GroupDefinition) {
				GroupDefinition oldGroup = (GroupDefinition) existingEditor.getParentEditorDefinition();
				oldGroup.getEditorDefinitions().remove(existingEditor);
			}
		}
		editorDefinitions.put(id, editorDefinition);
	}

	public EditorDefinition getEditorDefinition(String editorId) {
		if (editorId == null) {
			return rootGroupDefinition;
		}
		return (EditorDefinition) editorDefinitions.get(editorId);
	}

	public ListDefinition getListDefinition(String editorId) {
		return (ListDefinition) getEditorDefinition(editorId);
	}

	public FormDefinition getFormDefinition(String editorId) {
		return (FormDefinition) getEditorDefinition(editorId);
	}
	
	public GroupDefinition getGroupDefinition(String editorId) {
		return (GroupDefinition) getEditorDefinition(editorId);
	}

	public ListRepository getListRepository() {
		return listRepository;
	}

	public void setListRepository(ListRepository listRepository) {
		this.listRepository = listRepository;
	}
	
	public FormRepository getFormRepository() {
		return this.formRepository;
	}

	public void setFormRepository(FormRepository formRepository) {
		this.formRepository = formRepository;
	}

	public AdvancedMessageCodesResolver getMessageCodesResolver() {
		return this.messageCodesResolver;
	}

	public void setMessageCodesResolver(AdvancedMessageCodesResolver messageKeyResolver) {
		this.messageCodesResolver = messageKeyResolver;
	}

	/**
	 * Retrieves all {@link EditorController} instances from the 
	 * ApplicationContext.
	 */
	public void initApplicationContext() {
		Map map = getApplicationContext().getBeansOfType(EditorController.class);
		editorControllers = map.values();
	}
	
	/**
	 * Subclasses may overwrite this method to allow the registration of 
	 * {@link ConfigurationEventListener ConfigurationEventListeners}. 
	 * The default implementation does nothing.
	 * @since 6.5
	 */
	public void addListener(ConfigurationEventListener listener) {
	}

	/**
	 * Returns the most specialized {@link EditorController} for the given
	 * {@link EditorDefinition}.
	 */
	public EditorController getEditorController(
			EditorDefinition editorDefinition) {
		
		Class definitionClass = editorDefinition.getClass();
		EditorController controller = (EditorController) 
				editorControllersByDefinitionClass.get(definitionClass);
		
		if (controller == null) {
			EditorControllerComparator comp = 
					new EditorControllerComparator(definitionClass);
			
			TreeSet controllers = new TreeSet(comp);
			controllers.addAll(editorControllers);
			controller = (EditorController) controllers.first();
			editorControllersByDefinitionClass.put(definitionClass, controller);
		}
		
		return controller;
	}
	
	/**
	 * Convenience method to obtain an editor URL. This is a shortcut for
	 * {@link #getEditorController(EditorDefinition)}.{@link EditorController#getUrl(String, String, String)}.
	 */
	public String getEditorUrl(EditorDefinition editorDefinition, 
			String objectId, String parentId) {
		
		EditorController controller = getEditorController(editorDefinition);
		return controller.getUrl(editorDefinition.getId(), objectId, parentId);
	}
	
	/**
	 * Comparator that can be used to find the most specialized EditorController
	 * for a given EditorDefinition class. 
	 */
	protected static class EditorControllerComparator 
			extends TypeDifferenceComparator {
		
		public EditorControllerComparator(Class definitionClass) {
			super(definitionClass);
		}
		
		public int compare(Object o1, Object o2) {
			EditorController ec1 = (EditorController) o1;
			EditorController ec2 = (EditorController) o2;
			return super.compare(ec1.getDefinitionClass(), 
					ec2.getDefinitionClass());
		}
	}
	
}
