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
package org.riotfamily.components.property;

import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.riotfamily.cachius.TaggingContext;



public interface PropertyProcessor {

	/**
	 * Converts the given Object into a String representation suitable for
	 * storing it in a flat key-value table.
	 */
	public String convertToString(Object object);
	
	/**
	 * Converts a String created by the {@link #convertToString(Object)} method
	 * back into an Object of the original type.
	 */
	public Object resolveString(String s);
	
	/**
	 * Converts the given instance into an object suitable for JSON 
	 * serialization. Implementors must return an object that can be passed to
	 * {@link JSONObject#fromObject(Object)}.  
	 */
	public Object toJSON(Object object);
	
	/**
	 * Converts an Object returned by {@link JSONObject#get(Object)} or
	 * {@link JSONArray#get(int)} back to its original type.
	 */
	public Object fromJSON(Object object);
	
	/**
	 * Creates a copy of the given String. This is useful for PropertyProcessors
	 * that store a reference to an external resource. This hook allows 
	 * implementors to create a copy of the referenced resource and return the 
	 * new reference. 
	 */
	public String copy(String s);
	
	/**
	 * Invoked when a ComponentVersion is deleted. This hook is useful for 
	 * PropertyProcessors that store a reference rather than a value, as it 
	 * allows implementors to delete the associated resource.
	 */
	public void delete(String s);
	
	/**
	 * Implementors may return a String that is used to {@link 
	 * TaggingContext#tag(javax.servlet.http.HttpServletRequest, String) tag}
	 * views displaying this property. This is useful when the stored value is
	 * a reference to an external resource, as allows cache items to be 
	 * invalidated when the associated resource is modified. 
	 */
	public String getCacheTag(String s);
	
	/**
	 * This method is invoked after {@link #convertToString(Object)} and allows
	 * implementors to store additional values in the model. Make sure not to 
	 * add any non-String values.
	 */
	public void onUpdate(Object object, Map map);
	
}
