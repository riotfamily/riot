package org.riotfamily.riot.editor;

import java.util.List;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.riot.editor.ui.EditorReference;



/**
 *
 */
public interface EditorDefinition {

	public String getId();
	
	public Class getBeanClass();
	
	public void setParentEditorDefinition(EditorDefinition parent);	

	public EditorDefinition getParentEditorDefinition();
	
	/**
	 * Returns a PathComponent for the given objectId and parentId that 
	 * represents the complete path to the editor.
	 * 
	 * This method is invoked by the PathController for the current (active)
	 * editor in order to build a breadcrumb navigation.
	 */
	public EditorReference createEditorPath(
			String objectId, String parentId, MessageResolver messageResolver);
	
	/**
	 * Returns a PathComponent for the given bean that represents the complete
	 * path to the editor. 
	 * 
	 * This method is usually invoked by descendant editors to create a
	 * complete path recursivly.
	 */
	public EditorReference createEditorPath(Object bean, 
			MessageResolver messageResolver);
	
	/**
	 * Creates a reference to an editor. The method is used by the
	 * {@link org.riotfamily.riot.editor.ui.EditorGroupController 
	 * EditorGroupController}.
	 */
	public EditorReference createReference(String parentId,
			MessageResolver messageResolver);
	
	
	public void addReference(List refs, DisplayDefinition parentDef, 
			Object parent, MessageResolver messageResolver);
	
	/**
	 * 
	 */
	public String getEditorUrl(String objectId, String parentId);
	
	/**
	 * Editors that are only used in choosers may return <code>true</code>
	 * in order to be excluded from group views.
	 * 
	 * @see org.riotfamily.riot.editor.ui.EditorGroupController 
	 */
	public boolean isHidden();
	
}
