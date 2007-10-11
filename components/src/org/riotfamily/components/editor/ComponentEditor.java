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
 * Interface used by the Riot Toolbar to edit components. All methods defined
 * by this interface are exposed to the JavaScript client via DWR.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface ComponentEditor {

	public static final String POSTION_CLASS_PREFIX = "component-";

	public static final String LAST_COMPONENT_CLASS = "last-component";

	public String getText(Long containerId, String property);

	public void updateText(Long containerId, String property, String text);

	public void updateTextChunks(Long containerId, String property,
			String[] chunks);

	public List getValidTypes(String controllerId);

	public Long insertComponent(Long listId, int position, String type,
			Map properties);

	public void setType(Long containerId, String type);

	public List getLiveListHtml(ListRef[] ref)
			throws RequestContextExpiredException;

	public String getPreviewListHtml(String controllerId, Long listId)
			throws RequestContextExpiredException;

	public void moveComponent(Long containerId, Long nextContainerId);

	public void deleteComponent(Long containerId);

	public void publish(Long listId, Long[] containerIds);

	public void discard(Long listId, Long[] containerIds);

	public void keepAlive();

	public void logout();

	public Map getEditorConfigs();

}