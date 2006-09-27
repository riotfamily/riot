package org.riotfamily.riot.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.riot.editor.ui.EditorReference;
import org.riotfamily.riot.list.ListConfig;

/**
 * Abstract base class for editors that display a single object.
 *  
 * @see org.riotfamily.riot.editor.FormDefinition
 * @see org.riotfamily.riot.editor.ViewDefinition
 */
public abstract class AbstractDisplayDefinition extends AbstractEditorDefinition 
		implements DisplayDefinition {

	
	private Class beanClass;
	
	private String labelProperty;
	
	private List childEditorDefinitions = new LinkedList();
	
	private boolean nested;
	
	public AbstractDisplayDefinition(EditorRepository repository, 
			String editorType) {
		
		super(repository, editorType);
	}

	public Class getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class beanClass) {
		this.beanClass = beanClass;
	}

	public String getLabelProperty() {
		return labelProperty;
	}

	public void setLabelProperty(String labelProperty) {
		this.labelProperty = labelProperty;
	}
	
	public void setParentEditorDefinition(EditorDefinition editorDef) {
		if (editorDef instanceof ListDefinition) {
			ListDefinition listDef = (ListDefinition) editorDef;
			ListConfig listConfig = listDef.getListConfig();
			if (labelProperty == null) {
				labelProperty = listConfig.getFirstProperty();	
			}
		}
		else if (editorDef instanceof FormChooserDefinition) {
			editorDef = editorDef.getParentEditorDefinition();
		}
		else if (editorDef instanceof DisplayDefinition) {
			nested = true;
		}
		if (beanClass == null && editorDef != null) {
			beanClass = editorDef.getBeanClass();
		}
		super.setParentEditorDefinition(editorDef);
	}
	
	
	public List getChildEditorDefinitions() {
		return childEditorDefinitions;
	}
	
	public List getChildEditorReferences(Object object, 
			MessageResolver messageResolver) {
		
		List refs = new ArrayList();
		Iterator it = childEditorDefinitions.iterator();
		while (it.hasNext()) {
			EditorDefinition editorDef = (EditorDefinition) it.next();
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
	
	public String getLabel(Object object) {
		if (object == null) {
			return "New"; //TODO I18nize this
		}
		if (getLabelProperty() != null) {
			StringBuffer label = new StringBuffer();
			Pattern p = Pattern.compile("(\\w+)(\\W*)");
			Matcher m = p.matcher(labelProperty);
			while (m.find()) {
				String property = m.group(1);
				Object value = PropertyUtils.getProperty(object, property);
				if (value != null) {
					label.append(value);
					label.append(m.group(2));
				}
			}
			if (label.length() > 0) {
				return label.toString();
			}
		}
		return "Untitled"; //TODO I18nize this
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
			MessageResolver messageResolver) {
		
		if (objectId != null) {
			//Editing an existing object
			Object bean = loadBean(objectId);
			return createEditorPath(bean, messageResolver);
		}
		else {
			//Creating a new object - delegate the call to the parent editor
			EditorReference parent = getParentEditorDefinition()
					.createEditorPath(null, parentId, messageResolver);

			EditorReference component = createPathComponent(null, parentId);
			component.setParent(parent);
			return component;
		}
	}

	/**
	 * Returns a PathComponent for the given bean that represents the complete
	 * path to the form.
	 */
	public EditorReference createEditorPath(Object bean, 
			MessageResolver messageResolver) {
		
		EditorReference component = null;
		Object parentBean = null;
		if (nested) {
			String objectId = EditorDefinitionUtils.getObjectId(this, bean);
			component = createReference(objectId, messageResolver);
			parentBean = bean;
		}
		else {
			component = createPathComponent(bean, null);
			parentBean = EditorDefinitionUtils.getParent(this, bean);
		}
		
		//Create ancestors
		EditorReference parent = getParentEditorDefinition()
				.createEditorPath(parentBean, messageResolver);

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
	 */
	public EditorReference createPathComponent(Object bean, String parentId) {
		EditorReference component = new EditorReference();
		component.setEditorType(getEditorType());
		component.setLabel(getLabel(bean));
		component.setBean(bean);
		String objectId = null;
		if (bean != null) {
			 objectId = EditorDefinitionUtils.getObjectId(this, bean);
			 component.setObjectId(objectId);
		}
		component.setEditorUrl(getEditorUrl(objectId, parentId));
		return component;
	}
	
	/**
	 *  
	 */
	public EditorReference createReference(String objectId, 
			MessageResolver messageResolver) {
		
		EditorReference ref = new EditorReference();
		ref.setEditorType(getEditorType());
		String defaultLabel = FormatUtils.camelToTitleCase(getId());
		ref.setLabel(messageResolver.getMessage(
				getMessageKey().toString(), null, defaultLabel));
		
		ref.setObjectId(objectId);
		ref.setEditorUrl(getEditorUrl(objectId, null));
		return ref;
	}
}
