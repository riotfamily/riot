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

import org.riotfamily.pages.component.VersionContainer;
import org.riotfamily.pages.component.ComponentList;
import org.riotfamily.pages.component.ComponentVersion;

/**
 * DAO interface that provides CRUD methods for 
 * {@link ComponentList ComponentList}s, 
 * {@link ComponentVersion ComponentVersion}s and 
 * {@link VersionContainer VersionContainer}s.
 */
public interface ComponentDao {

	public List findComponentLists(String path);
	
	public ComponentList findComponentList(String path, String key);
	
	public ComponentList loadComponentList(Long id);
	
	public VersionContainer loadVersionContainer(Long id);
	
	
	public void saveComponentList(ComponentList list);
	
	public void saveVersionContainer(VersionContainer container);
	
	
	public void updateComponentList(ComponentList list);
	
	public void updateVersionContainer(VersionContainer container);

	public void updateComponentVersion(ComponentVersion version);
	
	
	public void deleteVersionContainer(VersionContainer container);
		
	public void deleteComponentVersion(ComponentVersion version);
	
	public void updatePaths(String oldPath, String newPath);
	
	public void copyComponentLists(String oldPath, String newPath);

}
