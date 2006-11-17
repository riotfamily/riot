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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.riotfamily.pages.component.Component;
import org.riotfamily.pages.component.ComponentList;
import org.riotfamily.pages.component.ComponentRepository;
import org.riotfamily.pages.component.ComponentVersion;
import org.riotfamily.pages.component.VersionContainer;
import org.riotfamily.pages.component.property.PropertyProcessor;

/**
 * Abstract base class for {@link Component implementations that delegates
 * the various CRUD methods to generic load, save, update and delete methods.
 * 
 * @author Felix Gnass <fgnass@neteye.de>
 */
public abstract class AbstractComponentDao implements ComponentDao {

	private ComponentRepository repository;
	
	public AbstractComponentDao(ComponentRepository repository) {
		this.repository = repository;
	}

	/**
	 * Loads the ComponentList specified  by the given id.
	 */
	public ComponentList loadComponentList(Long id) {
		return (ComponentList) loadObject(ComponentList.class, id);
	}

	/**
	 * Loads the VersionContainer specified  by the given id.
	 */
	public VersionContainer loadVersionContainer(Long id) {
		return (VersionContainer) loadObject(VersionContainer.class, id);
	}
	
	/**
	 * Saves the given ComponentList.
	 */
	public void saveComponentList(ComponentList list) {
		saveObject(list);
	}

	/**
	 * Saves the given VersionContainer.
	 */
	public void saveVersionContainer(VersionContainer container) {
		saveObject(container);
	}

	/**
	 * Updates the given ComponentList.
	 */
	public void updateComponentList(ComponentList list) {
		if (list.getId() != null) {
			updateObject(list);
		}
	}

	/**
	 * Updates the given VersionContainer.
	 */
	public void updateVersionContainer(VersionContainer container) {
		if (container.getId() != null) {
			updateObject(container);
		}
	}
	
	/**
	 * Updates the given ComponentVersion.
	 */
	public void updateComponentVersion(ComponentVersion version) {
		if (version.getId() != null) {
			updateObject(version);
		}
	}
	
	/**
	 * Returns a list of {@link VersionContainer containers} that can be 
	 * modified without affecting the live list. If the preview list does not
	 * already exist a new list is created and populated with the containers
	 * from the live list. This method does not create any copys since the 
	 * containers themself are responisble for managing the different versions. 
	 */
	public List getOrCreatePreviewContainers(ComponentList list) {
		List previewContainers = list.getPreviewList(); 
		if (!list.isDirty()) {
			if (previewContainers == null) {
				previewContainers = new ArrayList();
			}
			else {
				previewContainers.clear();
			}
			List liveContainers = list.getLiveList();
			if (liveContainers != null) {
				previewContainers.addAll(liveContainers);
			}
			list.setPreviewList(previewContainers);
			list.setDirty(true);
		}
		return previewContainers;
	}
	
	/**
	 * Returns the most recent version within the given container. This can 
	 * either be the preview version or the live version (in case the container
	 * does not contain a preview version). This method will never return 
	 * <code>null</code> since containers must not be empty.
	 */
	public ComponentVersion getLatestVersion(VersionContainer container) {
		ComponentVersion version = container.getPreviewVersion();
		if (version == null) {
			version = container.getLiveVersion();
		}
		return version;
	}
	
	/**
	 * Returns the preview version from the given container. If there is only
	 * a live version, a new preview is created automatically.
	 *  
	 * @param container The container to use
	 * @param type The type to use when creating a new version. If set to
	 * 		<code>null</code>, the type of the live version is used. 
	 * 
	 */
	public ComponentVersion getOrCreatePreviewVersion(VersionContainer container, String type) {
		ComponentVersion preview = container.getPreviewVersion();
		if (preview == null) {
			ComponentVersion live = container.getLiveVersion();
			if (type == null) {
				type = live.getType();
			}
			preview = cloneComponentVersion(live);
			container.setPreviewVersion(preview);
			updateVersionContainer(container);
		}
		return preview;
	}
		
	/**
	 * Creates a new container, containing a version of the given type.
	 * 
	 * @param type The type of the version to create
	 * @param properties Properties of the version to create
	 * @param live Whether to create a preview or live version
	 * @return The newly created container
	 */
	public VersionContainer createVersionContainer(
			String type, Map properties, boolean live) {
		
		VersionContainer container = new VersionContainer();
		ComponentVersion version = new ComponentVersion(type);
		version.setProperties(properties);
		if (live) {
			container.setLiveVersion(version);
		}
		else {
			container.setPreviewVersion(version);
		}
		version.setContainer(container);
		saveVersionContainer(container);
		return container;
	}
	
	/**
	 * Deletes the given VersionContainer.
	 */
	public void deleteVersionContainer(VersionContainer container) {
		deleteComponentVersion(container.getLiveVersion());
		deleteComponentVersion(container.getPreviewVersion());
		deleteObject(container);
	}
	
	/**
	 * Deletes the given ComponentVersion.
	 */
	public void deleteComponentVersion(ComponentVersion version) {
		if (version != null) {
			Component component = repository.getComponent(version);
			Iterator it = component.getPropertyProcessors().iterator();
			while (it.hasNext()) {
				PropertyProcessor pp = (PropertyProcessor) it.next();
				pp.delete(version.getProperties());
			}
			deleteObject(version);
		}
	}
	
	/**
	 * Publishes the given VersionContainer.
	 * @return <code>true</code> if there was anything to publish
	 */
	public boolean publishContainer(VersionContainer container) {
		ComponentVersion preview = container.getPreviewVersion();
		if (preview != null) {
			ComponentVersion liveVersion = container.getLiveVersion();
			if (liveVersion != null) {
				deleteComponentVersion(liveVersion);
			}
			container.setLiveVersion(preview);
			container.setPreviewVersion(null);
			updateVersionContainer(container);
			return true;
		}
		return false;
	}
	
	/**
	 * Publishes the given list.
	 * @return <code>true</code> if there was anything to publish
	 */
	public boolean publishList(ComponentList componentList) {
		boolean result = false;
		if (componentList.isDirty()) {
			result = true;
			List previewList = componentList.getPreviewList();
			List liveList = componentList.getLiveList();
			if (liveList == null) {
				liveList = new ArrayList();
			}
			else {
				Iterator it = liveList.iterator();
				while (it.hasNext()) {
					VersionContainer container = (VersionContainer) it.next();
					if (!previewList.contains(container)) {
						deleteVersionContainer(container);
					}
				}
				liveList.clear();
			}
			liveList.addAll(previewList);
			previewList.clear();
			componentList.setDirty(false);
			updateComponentList(componentList);
		}
		
		Iterator it = componentList.getLiveList().iterator();
		while (it.hasNext()) {
			VersionContainer container = (VersionContainer) it.next();
			result |= publishContainer(container);
		}
		return result;
	}
	
	/**
	 * Discards all changes made to the given list.
	 */
	public void discardList(ComponentList componentList) {
		List previewList = componentList.getPreviewList();
		List liveList = componentList.getLiveList();
		if (componentList.isDirty()) {
			componentList.setPreviewList(null);
			componentList.setDirty(false);
			Iterator it = previewList.iterator();
			while (it.hasNext()) {
				VersionContainer container = (VersionContainer) it.next();
				if (liveList == null || !liveList.contains(container)) {
					deleteVersionContainer(container);
				}
			}
			updateComponentList(componentList);	
		}
		Iterator it = liveList.iterator();
		while (it.hasNext()) {
			VersionContainer container = (VersionContainer) it.next();
			ComponentVersion preview = container.getPreviewVersion();
			if (preview != null) {
				container.setPreviewVersion(null);
				updateVersionContainer(container);
				deleteComponentVersion(preview);
			}
		}
	}
	
	/**
	 * Creates copys of all ComponentLists under the given path and sets 
	 * their path to the specified <code>newPath</code>.
	 */
	public void copyComponentLists(String oldPath, String newPath) {
		List lists = findComponentLists(oldPath);
		List nestedLists = null;
		Map copiedLists = null;
		if (lists != null) {
			copiedLists = new HashMap();
			Iterator it = lists.iterator();
			while (it.hasNext()) {
				ComponentList list = (ComponentList) it.next();
				if (list.getKey().indexOf('$') != -1) {					
					if (nestedLists == null) {
						nestedLists = new ArrayList();
					}
					nestedLists.add(list);
				}
				else {
					ComponentList copy = cloneComponentList(list);
					copy.setPath(newPath);
					saveComponentList(copy);					
					copiedLists.put(copy.getKey(), copy);
				}						
			}
		}
		if (nestedLists != null) {
			copyNestedLists(nestedLists, copiedLists, newPath);
		}
	}	

	private void copyNestedLists(List nestedLists, Map copiedLists, 
					String newPath) {

		//TODO Nested list could again have nested lists
		Iterator it = nestedLists.iterator();
		while (it.hasNext()) {
			ComponentList list = (ComponentList) it.next();				
			String parentId = list.getKey().
					substring(list.getKey().indexOf('$') + 1);				
			VersionContainer parent = loadVersionContainer(Long.valueOf(parentId));
			if (parent != null) {
				ComponentList parentList = parent.getList();				
				ComponentList copiedList = (ComponentList)copiedLists.get(
								parentList.getKey());				
				VersionContainer newParent = null;
				if (parentList.getLiveList() != null 
						&& parentList.getLiveList().contains(parent)) {						
					newParent = (VersionContainer)copiedList.getLiveList().
							get(parentList.getLiveList().indexOf(parent));
				}
				else {
					newParent = (VersionContainer)copiedList.
						getPreviewList().get(parentList.getPreviewList().
								indexOf(parent));					
				}				
				ComponentList copy = cloneComponentList(list);
				copy.setPath(newPath);
				String newKey = copy.getKey().
						substring(0, copy.getKey().indexOf('$') + 1) 
						+ newParent.getId();				
				copy.setKey(newKey);
				saveComponentList(copy);
			}
		}
	}
	
	private ComponentList cloneComponentList(ComponentList list) {
		ComponentList copy = new ComponentList();
		copy.setKey(list.getKey());
		copy.setDirty(list.isDirty());
		copy.setLiveList(copyContainers(list.getLiveList()));;
		copy.setPreviewList(copyContainers(list.getPreviewList()));
		return copy;
	}
	
	private List copyContainers(List source) {
		if (source == null) {
			return null;
		}
		List dest = new ArrayList(source.size());
		Iterator it = source.iterator();
		while (it.hasNext()) {
			VersionContainer vc = (VersionContainer) it.next();
			VersionContainer copy = copyVersionContainer(vc);
			dest.add(copy);
		}
		return dest;
	}
	
	private VersionContainer copyVersionContainer(VersionContainer container) {
		VersionContainer copy = new VersionContainer();
		if (container.getLiveVersion() != null) {
			copy.setLiveVersion(cloneComponentVersion(
					container.getLiveVersion()));
		}
		if (container.getPreviewVersion() != null) {
			copy.setPreviewVersion(cloneComponentVersion(
					container.getPreviewVersion()));
		}
		return copy;
	}
	
	private ComponentVersion cloneComponentVersion(ComponentVersion version) {
		Component component = repository.getComponent(version);
		ComponentVersion copy = new ComponentVersion(version);
		if (component.getPropertyProcessors() != null) {
			Iterator it = component.getPropertyProcessors().iterator();
			while (it.hasNext()) {
				PropertyProcessor pp = (PropertyProcessor) it.next();
				pp.copy(version.getProperties(), copy.getProperties());
			}
		}
		return copy;
	}
	
	protected abstract Object loadObject(Class clazz, Long id);
	
	protected abstract void saveObject(Object object);
	
	protected abstract void updateObject(Object object);
	
	protected abstract void deleteObject(Object object);

}
