package org.riotfamily.riot.editor.ui;

import org.riotfamily.riot.editor.EditorDefinition;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Interface to be implemented by controllers that act as editors.
 *  
 * @see org.riotfamily.riot.editor.EditorRepository#getEditorUrl(EditorDefinition, String, String)
 */
public interface EditorController extends Controller {

	/**
	 * Returns the EditorDefinition-class that is handled by the controller.
	 */
	public Class getDefinitionClass();
	
	/**
	 * Returns an URL that can be used to request the controller. Typically
	 * implementors will be XmlHandlerMappingAware in order determine 
	 * their own mapping.   
	 */
	public String getUrl(String editorId, String objectId, String parentId);

}
