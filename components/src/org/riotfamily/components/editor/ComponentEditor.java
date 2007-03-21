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

import org.riotfamily.components.context.RequestContextExpiredException;

/**
 * Interface that is exposed to the JavaScript client via DWR. 
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface ComponentEditor {

	public static final String INSTANT_PUBLISH_ATTRIBUTE =
			ComponentEditor.class.getName() + ".instantPublish";
	
	public static final String POSTION_CLASS_PREFIX = "component-";

	public static final String LAST_COMPONENT_CLASS = "last-component";

	public String getText(Long containerId, String property);

	public String updateText(String controllerId, Long containerId,
			String property, String text)
			throws RequestContextExpiredException;

	public ComponentInfo[] updateTextChunks(String controllerId, 
			Long containerId, String property, String[] chunks) 
			throws RequestContextExpiredException;

	public List getValidTypes(String controllerId);

	public ComponentInfo insertComponent(String controllerId, Long listId,
			int position, String type, Map properties)
			throws RequestContextExpiredException;

	public ComponentInfo setType(String controllerId, Long containerId,
			String type) throws RequestContextExpiredException;

	public ComponentInfo getComponent(String controllerId, Long containerId)
			throws RequestContextExpiredException;

	public String getLiveListHtml(String controllerId, Long listId);

	public String getPreviewListHtml(String controllerId, Long listId);

	public void moveComponent(Long containerId, Long nextContainerId);

	public void deleteComponent(Long containerId);

	public List getDirtyListIds(Long[] listIds);

	public void publishLists(Long[] listIds);

	public void discardLists(Long[] listIds);

	public void discardList(Long listId);

	public String discardListAndGetPreviewHtml(String controllerId,
			Long listId);

	public void publishList(Long listId);

	public void keepAlive();
	
	public boolean isInstantPublishMode() throws RequestContextExpiredException;

	public void logout();
	
	public Map getEditorConfigs();

}