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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.service;

import java.util.List;
import java.util.Map;

import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.ComponentVersion;
import org.riotfamily.components.model.VersionContainer;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public interface ComponentService {

	/**
	 * Updates the given ComponentVersion.
	 */
	public abstract void updateComponentVersion(ComponentVersion version);

	/**
	 * Updates the properties of the given ComponentVersion.
	 */
	public abstract void updateComponentProperties(ComponentVersion version, Map properties);

	/**
	 * Returns a list of {@link VersionContainer containers} that can be
	 * modified without affecting the live list. If the preview list does not
	 * already exist a new list is created and populated with the containers
	 * from the live list. This method does not create any copies since the
	 * containers themselves are responsible for managing the different versions.
	 */
	public abstract List getOrCreatePreviewContainers(ComponentList list);

	public abstract ComponentVersion getOrCreateVersion(Long containerId, boolean live);

	/**
	 * Returns the preview version from the given container. If there is only
	 * a live version, a new preview is created automatically.
	 *
	 * @param container The container to use
	 * @param type The type to use when creating an initial preview version.
	 */
	public abstract ComponentVersion getOrCreateVersion(VersionContainer container, String type, boolean live);

	/**
	 * Inserts a container into a list.
	 */
	public abstract VersionContainer insertContainer(ComponentList componentList, String type, Map properties,
			int position, boolean live);

	/**
	 * Delete all ComponentLists for the given path
	 * @since 6.4
	 */
	public abstract void deleteComponentLists(String type, String path);

	/**
	 * Deletes the given ComponentList.
	 */
	public abstract void deleteComponentList(ComponentList list);

	/**
	 * Deletes the given VersionContainer.
	 */
	public abstract void deleteVersionContainer(VersionContainer container);

	/**
	 * Deletes the given ComponentVersion.
	 */
	public abstract void deleteComponentVersion(ComponentVersion version);

	/**
	 * Publishes the given VersionContainer.
	 * @return <code>true</code> if there was anything to publish.
	 */
	public abstract boolean publishContainer(VersionContainer container);

	/**
	 * Publishes the given list.
	 * @return <code>true</code> if there was anything to publish
	 */
	public abstract boolean publishList(ComponentList componentList);

	/**
	 * Discards all changes made to the given list.
	 */
	public abstract boolean discardList(ComponentList componentList);

	/**
	 * Discards all changes made to the given VersionContainer.
	 * @return <code>true</code> if there was anything to discard.
	 */
	public abstract boolean discardContainer(VersionContainer container);

	/**
	 * Creates copies of all ComponentLists under the given path and sets
	 * their path to the specified <code>newPath</code>.
	 */
	public abstract void copyComponentLists(String type, String oldPath, String newPath);

	public abstract VersionContainer copyVersionContainer(VersionContainer container);

}