package org.riotfamily.pages.component.property;

import java.util.Map;

public interface PropertyProcessor {

	/**
	 * Replaces strings in the map by objects suitable for rendering or
	 * editing. The method is invoked before a component is rendered or edited
	 * using a form.
	 */
	public void resolveStrings(Map map);
	
	/**
	 * Replaces objects in the map by their string representation. The method
	 * is invoked after a component model has been edited using a form and
	 * before it is persisted. 
	 */
	public void convertToStrings(Map map);
	
	/**
	 * Copies strings from one map to another. The method is invoked when a
	 * copy of a component model needs to be created. Implementors can use this
	 * hook to clone referenced objects. See {@link FileStoreProperyProcessor}
	 * for an example.
	 */
	public void copy(Map source, Map dest);
	
	/**
	 * Deletes orphaned resources. The method is invoked when a component model
	 * is deleted. Implementors can use this hook to delete referenced objects
	 * or resources. See {@link FileStoreProperyProcessor}
	 * for an example.
	 */
	public void delete(Map map);
	
}
