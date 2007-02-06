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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.component.dao;

import java.util.List;
import java.util.Map;

import org.riotfamily.pages.component.ComponentList;
import org.riotfamily.pages.component.ComponentVersion;
import org.riotfamily.pages.component.VersionContainer;

/**
 * DAO interface that provides methods to access 
 * {@link ComponentList ComponentList}s, 
 * {@link ComponentVersion ComponentVersion}s and 
 * {@link VersionContainer VersionContainer}s.
 */
public interface ComponentDao {

	/**
	 * Returns all {@link ComponentList ComponentLists} with the given path.
	 */
	public List findComponentLists(String path);
	
	/**
	 * Returns all {@link ComponentList ComponentLists} marked as dirty.
	 */
	public List findDirtyComponentLists();
	
	/**
	 * Returns all {@link ComponentList ComponentLists} with the given path
	 * and key.
	 */
	public ComponentList findComponentList(String path, String key);
	
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
	 * Returns a ComponentVersion for the given container id.
	 * @param containerId Id of the container to load
	 * @param live Whether to return the live (or preview) version
	 * @since 6.4
	 */
	public ComponentVersion getComponentVersionForContainer(
			Long containerId, boolean live);
	
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
	public void deleteComponentLists(String path);
	
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
	 * Updates all ComponentLists under <code>oldPath</code> and changes their
	 * path to <code>newPath</code>. 
	 */
	public void updatePaths(String oldPath, String newPath);
	
	/**
	 * Returns a list of {@link VersionContainer containers} that can be 
	 * modified without affecting the live list. If the preview list does not
	 * already exist a new list is created and populated with the containers
	 * from the live list. This method does not create any copys since the 
	 * containers themself are responisble for managing the different versions. 
	 */
	public List getOrCreatePreviewContainers(ComponentList list);
	
	/**
	 * Returns the most recent version within the given container. This can 
	 * either be the preview version or the live version (in case the container
	 * does not contain a preview version). This method will never return 
	 * <code>null</code> since containers must not be empty.
	 */
	public ComponentVersion getLatestVersion(VersionContainer container);
	
	/**
	 * Returns the preview version from the given container. If there is only
	 * a live version, a new preview is created.
	 *  
	 * @param container The container to use
	 * @param type The type to use when creating a new version. If set to
	 * 		<code>null</code>, the type of the live version is used. 
	 * 
	 */
	public ComponentVersion getOrCreatePreviewVersion(
			VersionContainer container, String type);
	
	/**
	 * Inserts a container into a list.
	 */
	public VersionContainer insertContainer(ComponentList componentList, 
			String type, Map properties, int position, boolean live);
	
	/**
	 * Creates copys of all ComponentLists under the given path and sets 
	 * their path to the specified <code>newPath</code>.
	 */
	public void copyComponentLists(String oldPath, String newPath);
	
	/**
	 * Publishes all changes made to the given list.
	 */
	public boolean publishList(ComponentList componentList);

	/**
	 * Discards all changes made to the given list.
	 */
	public void discardList(ComponentList componentList);
	
}
