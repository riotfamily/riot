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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.riotfamily.components.context.RequestContextExpiredException;

/**
 * Interface used by the Riot Toolbar to edit components. All methods defined
 * by this interface are exposed to the JavaScript client via DWR.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface ComponentEditor {

	public String getText(Long contentId, String property);

	public void updateText(Long contentId, String property, String text);
	
	public String[] updateTextChunks(Long componentId, String property,
			String[] chunks) throws RequestContextExpiredException;

	public String generateToken();
	
	public void invalidateToken(String token);
	
	public String updateImage(Long contentId, String property, Long imageId);
	
	public String cropImage(Long contentId, String property, Long imageId,
			int width, int height, int x, int y, int scaledWidth)
			throws IOException;
			
	public void discardImage(Long imageId);
			
	public List<TypeInfo> getValidTypes(Long listId);

	public String insertComponent(Long listId, int position, String type,
			Map<String, String> properties)
			throws RequestContextExpiredException;

	public String setType(Long componentId, String type)
			throws RequestContextExpiredException;
	
	public String renderComponent(Long componentId)
			throws RequestContextExpiredException;

	public void moveComponent(Long componentId, Long nextComponentId);

	public void deleteComponent(Long componentId);

	public void publish(Long[] listIds, Long[] containerIds);

	public void discard(Long[] listIds, Long[] containerIds);

	public void keepAlive();

	public void logout();

	public Map<String, Map<String, Object>> getTinyMCEProfiles();

}