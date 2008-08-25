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

import java.util.List;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.riot.editor.ui.EditorReference;

/**
 * Abstract base class for editors that display a single object.
 *
 * @see org.riotfamily.riot.editor.FormDefinition
 * @see org.riotfamily.riot.editor.ViewDefinition
 */
public abstract class AbstractObjectEditorDefinition
		extends AbstractEditorDefinition
		implements ObjectEditorDefinition {

	private Class<?> beanClass;

	private String labelProperty;

	private List<EditorDefinition> childEditorDefinitions = Generics.newLinkedList();

	public Class<?> getBeanClass() {
		if (beanClass != null) {
			return beanClass;
		}
		EditorDefinition parent = getParentEditorDefinition();
		return parent != null ? parent.getBeanClass() : null;
	}

	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	public void setLabelProperty(String labelProperty) {
		this.labelProperty = labelProperty;
	}
	
	protected String getLabelProperty() {
		return labelProperty;
	}
	
	public String getLabel(Object object, MessageResolver messageResolver) {
		if (object == null) {
			return "New"; //TODO I18nize this
		}
		if (labelProperty != null) {
			return getLabel(object, labelProperty);
		}
		return getParentEditorDefinition().getLabel(object, messageResolver);
	}

	public List<EditorDefinition> getChildEditorDefinitions() {
		return childEditorDefinitions;
	}

	public List<EditorReference> getChildEditorReferences(Object object,
			MessageResolver messageResolver) {

		List<EditorReference> refs = Generics.newArrayList();
		for (EditorDefinition editorDef : childEditorDefinitions) {
			editorDef.addReference(refs, this, object, messageResolver);
		}
		return refs;
	}

	public void addChildEditorDefinition(EditorDefinition editorDef) {
		childEditorDefinitions.add(editorDef);
		editorDef.setParentEditorDefinition(this);
	}

	protected Object loadBean(String objectId) {
		return EditorDefinitionUtils.loadBean(this, objectId);
	}

	/**
	 * Returns a PathComponent for the given objectId and parentId that
	 * represents the complete path to the form.
	 *
	 * @param objectId Id of the object to be edited or <code>null</code> if a
	 *        form for a new object is requested
	 * @param parentId Id of the the parent object or <code>null</code> if
	 *        either an existing object is to be edited or the form belongs to a
	 *        root list
	 */
	public EditorReference createEditorPath(String objectId, String parentId,
			String parentEditorId, MessageResolver messageResolver) {

		if (parentId != null || objectId == null) {
			EditorReference parent = null;
			if (getParentEditorDefinition() != null) {
				parent = getParentEditorDefinition()
						.createEditorPath(null, parentId, parentEditorId, 
						messageResolver);
			}
			Object bean = null;
			if (objectId != null) {
				bean = loadBean(objectId);
			}
			EditorReference component = createPathComponent(bean, parentId, 
					parentEditorId, messageResolver);
			
			component.setParent(parent);
			return component;
		}
		else if (objectId != null) {
			Object bean = loadBean(objectId);
			return createEditorPath(bean, messageResolver);
		}
		return null;
		/*
		if (objectId != null) {
			//Editing an existing object
			Object bean = loadBean(objectId);
			return createEditorPath(bean, messageResolver);
		}
		else {
			//Creating a new object - delegate the call to the parent editor
			EditorReference parent = null;
			if (getParentEditorDefinition() != null) {
				parent = getParentEditorDefinition()
						.createEditorPath(null, parentId, parentEditorId, 
						messageResolver);
			}
			EditorReference component = createPathComponent(null, parentId, 
					parentEditorId, messageResolver);
			
			component.setParent(parent);
			return component;
		}
		*/
	}

	/**
	 * Returns a PathComponent for the given bean that represents the complete
	 * path to the form.
	 */
	public EditorReference createEditorPath(Object bean,
			MessageResolver messageResolver) {

		EditorReference component = null;
		Object parentBean = null;
		if (!(getParentEditorDefinition() instanceof ListDefinition)) {
			String objectId = EditorDefinitionUtils.getObjectId(this, bean);
			component = createReference(objectId, messageResolver);
			parentBean = bean;
		}
		else {
			component = createPathComponent(bean, null, null, messageResolver);
			parentBean = EditorDefinitionUtils.getParent(this, bean);
		}

		//Create ancestors
		EditorReference parent = null;
		if (getParentEditorDefinition() != null) {
			parent = getParentEditorDefinition().createEditorPath(
					parentBean, messageResolver);
		}
		component.setParent(parent);
		
		return component;
	}
	
	/**
	 * Creates a PathComponent for the given bean (or parentId). This method is
	 * not recursive and {@link EditorReference#getParent() getParent()} will
	 * always return <code>null</code>.
	 *
	 * @param bean The bean to be edited or <code>null</code> if a form for a
	 *        new (unsaved) object is requested.
	 * @param parentId Id of the the parent object or <code>null</code> if
	 *        either an existing object is to be edited or the form belongs to a
	 *        root list
	 * @param parentEditorId Id of the parent editor or <code>null</code> if
	 *        no parentId is specified
	 */
	protected EditorReference createPathComponent(Object bean, String parentId,
			String parentEditorId, MessageResolver messageResolver) {
		
		EditorReference component = new EditorReference();
		component.setEditorType(getEditorType());
		component.setEditorId(getId());
		component.setLabel(getLabel(bean, messageResolver));
		component.setBean(bean);
		String objectId = null;
		if (bean != null) {
			 objectId = EditorDefinitionUtils.getObjectId(this, bean);
			 component.setObjectId(objectId);
		}
		component.setEditorUrl(getEditorUrl(objectId, parentId, parentEditorId));
		return component;
	}

	/**
	 *
	 */
	public EditorReference createReference(String objectId,
			MessageResolver messageResolver) {

		EditorReference ref = new EditorReference();
		ref.setEditorType(getEditorType());
		ref.setEditorId(getId());
		ref.setIcon(getIcon());
		
		String defaultLabel = FormatUtils.xmlToTitleCase(getName());
		ref.setLabel(messageResolver.getMessage(
				getMessageKey().toString(), null, defaultLabel));

		ref.setObjectId(objectId);
		ref.setEditorUrl(getEditorUrl(objectId, null, null));
		return ref;
	}
	
	public String getEditorUrl(String objectId, String parentId, 
			String parentEditorId) {
		
		String url = getEditorUrlWithinServlet(objectId, parentId, parentEditorId);
		if (url != null) {
			return getEditorRepository().getRiotServletPrefix() + url;
		}
		return null;
	}
	
	protected final String getEditorUrlWithinServlet(
			String objectId, String parentId) {
		
		return null;
	}
	
	protected abstract String getEditorUrlWithinServlet(
			String objectId, String parentId, String parentEditorId);
	
}
