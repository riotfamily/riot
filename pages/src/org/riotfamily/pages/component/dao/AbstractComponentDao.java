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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.pages.component.ComponentList;
import org.riotfamily.pages.component.ComponentRepository;
import org.riotfamily.pages.component.ComponentVersion;
import org.riotfamily.pages.component.VersionContainer;

/**
 * Abstract base class for {@link ComponentDao} implementations that delegates
 * the various CRUD methods to generic load, save, update and delete methods.
 */
public abstract class AbstractComponentDao implements ComponentDao {

	private Log log = LogFactory.getLog(AbstractComponentDao.class);
	
	private ComponentRepository componentRepository;
	
	public AbstractComponentDao(ComponentRepository componentRepository) {
		this.componentRepository = componentRepository;
	}

	public ComponentList loadComponentList(Long id) {
		log.debug("Loading ComponentList " + id);
		return (ComponentList) loadObject(ComponentList.class, id);
	}

	public VersionContainer loadVersionContainer(Long id) {
		log.debug("Loading VersionContainer " + id);
		return (VersionContainer) loadObject(VersionContainer.class, id);
	}

	public void saveComponentList(ComponentList list) {
		log.debug("Saving ComponentList");
		saveObject(list);
	}

	public void saveVersionContainer(VersionContainer container) {
		log.debug("Saving ComponentData");
		saveObject(container);
	}

	public void updateComponentList(ComponentList list) {
		if (list.getId() != null) {
			log.debug("Updating ComponentList " + list.getId());
			updateObject(list);
		}
	}

	public void updateVersionContainer(VersionContainer container) {
		if (container.getId() != null) {
			log.debug("Updating VersionContainer " + container.getId());
			updateObject(container);
		}
	}
	
	public void deleteVersionContainer(VersionContainer container) {
		log.debug("Deleting VersionContainer " + container.getId());
		deleteObject(container);
	}

	public void updateComponentVersion(ComponentVersion version) {
		if (version.getId() != null) {
			log.debug("Updating ComponentVersion " + version.getId());
			updateObject(version);
		}
	}
	
	public void deleteComponentVersion(ComponentVersion version) {
		log.debug("Deleting ComponentVersion " + version.getId());
		deleteObject(version);
	}

	public void copyComponentLists(String oldPath, String newPath) {
		List lists = findComponentLists(oldPath);
		if (lists != null) {
			Iterator it = lists.iterator();
			while (it.hasNext()) {
				ComponentList list = (ComponentList) it.next();
				ComponentList copy = list.copy(newPath, componentRepository);
				saveComponentList(copy);		
			}
		}
	}
	
	protected abstract Object loadObject(Class clazz, Long id);
	
	protected abstract void saveObject(Object object);
	
	protected abstract void updateObject(Object object);
	
	protected abstract void deleteObject(Object object);

}
