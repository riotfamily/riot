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
package org.riotfamily.components.editor;

import java.util.List;
import java.util.Map;

import org.riotfamily.components.meta.ComponentMetaData;

/**
 * Interface used by the Riot Toolbar to edit components. All methods defined
 * by this interface are exposed to the JavaScript client via DWR.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface ComponentEditor {

	public String getText(String contentId, String property);

	public void updateText(String contentId, String property, String text);
	
	public String[] updateTextChunks(String componentId, String property,
			String[] chunks);

	public List<ComponentMetaData> getComponentMetaData(String[] types);
	
	public String insertComponent(String listId, int position, String type);

	public String setType(String componentId, String type);
	
	public String renderComponent(String componentId);

	public void moveComponent(String componentId, String nextComponentId);

	public void deleteComponent(String componentId);
	
	public void markAsDirty(Long containerId);

	public void publish(Long[] containerIds);

	public void discard(Long[] containerIds);

	public void logout();

	public Map<String, Map<String, Object>> getTinyMCEProfiles();

}