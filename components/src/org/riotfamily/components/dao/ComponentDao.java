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
package org.riotfamily.components.dao;

import java.util.List;
import java.util.Map;

import org.riotfamily.components.ComponentList;
import org.riotfamily.components.ComponentVersion;
import org.riotfamily.components.Location;
import org.riotfamily.components.VersionContainer;

/**
 * DAO interface that provides methods to access
 * {@link ComponentList ComponentList}s,
 * {@link ComponentVersion ComponentVersion}s and
 * {@link VersionContainer VersionContainer}s.
 */
public interface ComponentDao {

	/**
	 * Returns the {@link ComponentList} with the given location.
	 */
	public ComponentList findComponentList(Location location);

	/**
	 * Returns the nested {@link ComponentList} for the given parent/slot.
	 */
	public ComponentList findComponentList(VersionContainer parent, String slot);

	/**
	 * Returns all {@link ComponentList ComponentList} with the given type
	 * and path.
	 */
	public List findComponentLists(String type, String path);

	/**
	 * Returns all {@link ComponentList ComponentLists} marked as dirty.
	 */
	public List findDirtyComponentLists();

	/**
	 * Loads the ComponentList specified  by the given id.
	 */
	public ComponentList loadComponentList(Long id);

	/**
	 * Loads the VersionContainer specified  by the given id.
	 */
	public VersionContainer loadVersionContainer(Long id);

	/**
	 * Loads the ComponentVersion specified  by the given id.
	 * @since 6.4
	 */
	public ComponentVersion loadComponentVersion(Long id);

	/**
	 * Saves the given ComponentList.
	 */
	public void saveComponentList(ComponentList list);

	/**
	 * Updates the given ComponentList.
	 */
	public void updateComponentList(ComponentList list);

	/**
	 * Updates the given VersionContainer.
	 */
	public void updateVersionContainer(VersionContainer container);

	/**
	 * Updates the given ComponentVersion.
	 */
	public void updateComponentVersion(ComponentVersion version);

	/**
	 * Deletes all {@link ComponentList ComponentLists} with the given path.
	 * @since 6.4
	 */
	public void deleteComponentLists(String type, String path);

	/**
	 * Deletes the given ComponentList.
	 */
	public void deleteComponentList(ComponentList list);

	/**
	 * Deletes the given ComponentVersion.
	 */
	public void deleteComponentVersion(ComponentVersion version);

	/**
	 * Deletes the given VersionContainer.
	 */
	public void deleteVersionContainer(VersionContainer container);

	/**
	 * Returns a list of {@link VersionContainer containers} that can be
	 * modified without affecting the live list. If the preview list does not
	 * already exist a new list is created and populated with the containers
	 * from the live list. This method does not create any copys since the
	 * containers themself are responisble for managing the different versions.
	 */
	public List getOrCreatePreviewContainers(ComponentList list);

	/**
	 * Returns a ComponentVersion and creates it if it does not already exist.
	 * Whether the live or preview version is returned is controlled by the
	 * <code>live</code> parameter.
	 * @param container The VersionContainer to use
	 * @param type The type assigned to newly created versions
	 * @param live Whether to return the live version
	 */
	public ComponentVersion getOrCreateVersion(
			VersionContainer container, String type, boolean live);

	/**
	 * Inserts a container into a list.
	 */
	public VersionContainer insertContainer(ComponentList componentList,
			String type, Map properties, int position, boolean live);

	/**
	 * Creates copys of all ComponentLists under the given path and sets
	 * their path to the specified <code>newPath</code>.
	 */
	public void copyComponentLists(String type, String oldPath, String newPath);

	/**
	 * Publishes all changes made to the given list.
	 */
	public boolean publishList(ComponentList componentList);

	/**
	 * Published all changes made to the given container.
	 */
	public boolean publishContainer(VersionContainer container);

	/**
	 * Discards all changes made to the given list.
	 */
	public boolean discardList(ComponentList componentList);

	/**
	 * Discards all changes made to the given container.
	 */
	public boolean discardContainer(VersionContainer container);

}
