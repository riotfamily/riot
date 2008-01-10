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

import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ComponentListLocation;
import org.riotfamily.components.model.ContentContainer;

/**
 * DAO interface that provides methods to access
 * {@link ComponentList ComponentList}s,
 * {@link Content ComponentVersion}s and
 * {@link Component VersionContainer}s.
 */
public interface ComponentDao {

	/**
	 * Returns the {@link ComponentList} with the given location.
	 */
	public ComponentList findComponentList(ComponentListLocation location);

	/**
	 * Returns the nested {@link ComponentList} for the given parent/slot.
	 */
	public ComponentList findComponentList(Component parent, String slot);

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
	 * Copies all ComponentLists of the specified type from the oldPath to the newPath.
	 */
	public void copyComponentLists(String type, String oldPath, String newPath);
	
	/**
	 * Loads the ComponentList specified  by the given id.
	 */
	public ComponentList loadComponentList(Long id);

	/**
	 * Loads the Component specified  by the given id.
	 */
	public Component loadComponent(Long id);
	
	/**
	 * Loads the VersionContainer specified  by the given id.
	 */
	public ContentContainer loadVersionContainer(Long id);

	/**
	 * Loads the ComponentVersion specified  by the given id.
	 * @since 6.4
	 */
	public Content loadComponentVersion(Long id);

	/**
	 * Saves the given ComponentList.
	 */
	public void saveComponentList(ComponentList list);
	
	/**
	 * Saves the given VersionContainer.
	 */
	public void saveVersionContainer(ContentContainer container);

	/**
	 * Saves the given ComponentVersion.
	 */
	public void saveComponentVersion(Content version);
	
	/**
	 * Updates the given ComponentList.
	 */
	public void updateComponentList(ComponentList list);

	/**
	 * Updates the given VersionContainer.
	 */
	public void updateVersionContainer(ContentContainer container);

	/**
	 * Updates the given ComponentVersion.
	 */
	public void saveOrUpdateComponentVersion(Content version);
	
	/**
	 * Deletes the given ComponentList.
	 */
	public void deleteComponentList(ComponentList list);

	/**
	 * Deletes all ComponentLists with the given type and path.
	 */
	public void deleteComponentLists(String type, String path);
	
	/**
	 * Deletes the given ComponentVersion.
	 */
	public void deleteComponentVersion(Content version);

	/**
	 * Deletes the given VersionContainer.
	 */
	public void deleteVersionContainer(ContentContainer container);
	
}
