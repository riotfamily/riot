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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.event.ContentChangedEvent;
import org.riotfamily.components.Component;
import org.riotfamily.components.ComponentList;
import org.riotfamily.components.ComponentRepository;
import org.riotfamily.components.ComponentVersion;
import org.riotfamily.components.Location;
import org.riotfamily.components.PropertyProcessor;
import org.riotfamily.components.VersionContainer;
import org.riotfamily.riot.security.AccessController;
import org.springframework.context.event.ApplicationEventMulticaster;

/**
 * Abstract base class for {@link Component} implementations that delegates
 * the various CRUD methods to generic load, save, update and delete methods.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public abstract class AbstractComponentDao implements ComponentDao {

	private static final Log log = LogFactory.getLog(AbstractComponentDao.class);
	
	private ComponentRepository repository;

	private ApplicationEventMulticaster eventMulticaster;
	
	public AbstractComponentDao(ComponentRepository repository) {
		this.repository = repository;
	}

	public void setEventMulticaster(ApplicationEventMulticaster eventMulticaster) {
		this.eventMulticaster = eventMulticaster;
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
	 * Loads the ComponentVersion specified  by the given id.
	 * @since 6.4
	 */
	public ComponentVersion loadComponentVersion(Long id) {
		return (ComponentVersion) loadObject(ComponentVersion.class, id);
	}
	
	/**
	 * Saves the given ComponentList.
	 */
	public void saveComponentList(ComponentList list) {
		list.setLastModified(new Date());
		list.setLastModifiedBy(AccessController.getPrincipalForCurrentThread());
		saveObject(list);
	}

	/**
	 * Updates the given ComponentList.
	 */
	public void updateComponentList(ComponentList list) {
		if (list.getId() != null) {
			list.setLastModified(new Date());
			list.setLastModifiedBy(AccessController.getPrincipalForCurrentThread());
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
	 * from the live list. This method does not create any copies since the 
	 * containers themselves are responsible for managing the different versions. 
	 */
	public List getOrCreatePreviewContainers(ComponentList list) {
		List previewContainers = list.getPreviewContainers(); 
		if (!list.isDirty()) {
			if (previewContainers == null) {
				previewContainers = new ArrayList();
			}
			else {
				previewContainers.clear();
			}
			List liveContainers = list.getLiveContainers();
			if (liveContainers != null) {
				previewContainers.addAll(liveContainers);
			}
			list.setPreviewContainers(previewContainers);
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
	 * @param type The type to use when creating an initial preview version. 
	 */
	public ComponentVersion getOrCreatePreviewVersion(
			VersionContainer container, String type) {
		
		ComponentList list = container.getList();
		if (list != null && !list.isDirty()) {
			getOrCreatePreviewContainers(list);
			updateComponentList(list);
		}
		ComponentVersion preview = container.getPreviewVersion();
		if (preview == null) {
			ComponentVersion live = container.getLiveVersion();
			if (live != null) {
				preview = copyComponentVersion(live);
			}
			else {
				preview = new ComponentVersion(type);
			}
			container.setPreviewVersion(preview);
			updateVersionContainer(container);
		}
		return preview;
	}
	
	/**
	 * Inserts a container into a list.
	 */
	public VersionContainer insertContainer(ComponentList componentList, 
			String type, Map properties, int position, boolean live) {
		 
		List containers = live 
				? componentList.getLiveContainers() 
				: getOrCreatePreviewContainers(componentList);

		VersionContainer container = createVersionContainer(type, properties, live);
		container.setList(componentList);
		
		if (position >= 0) {
			containers.add(position, container);
		}
		else {
			containers.add(container);
		}
		
		if (!live) {
			componentList.setDirty(true);
		}
		
		updateComponentList(componentList);
		return container;
	}

	public ComponentVersion getComponentVersionForContainer(Long containerId, 
			boolean live) {
		
		VersionContainer container = loadVersionContainer(containerId);
		if (live) {
			return container.getLiveVersion();
		}
		return getOrCreatePreviewVersion(container, null);
	}
	
	/**
	 * Creates a new container, containing a version of the given type.
	 * 
	 * @param type The type of the version to create
	 * @param properties Properties of the version to create
	 * @param live Whether to create a preview or live version
	 * @return The newly created container
	 */
	private VersionContainer createVersionContainer(
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
		return container;
	}
	
	/**
	 * Delete all ComponentLists for the given path
	 * @since 6.4
	 */
	public void deleteComponentLists(String type, String path) {
		List componentLists = findComponentLists(type, path);
		Iterator it = componentLists.iterator();
		while (it.hasNext()) {
			ComponentList list = (ComponentList) it.next();
			deleteComponentList(list);
		}		
	}
	
	/**
	 * Deletes the given ComponentList.
	 */
	public void deleteComponentList(ComponentList list) {
		List previewList = list.getPreviewContainers();
		List liveList = list.getLiveContainers();
		if (liveList != null) {
			Iterator it = liveList.listIterator();
			while (it.hasNext()) {
				VersionContainer component = (VersionContainer) it.next();
				if (previewList == null || !previewList.contains(component)) {
					deleteVersionContainer(component);
				}
				it.remove();
			}
		}
		if (previewList != null) {
			Iterator it = previewList.listIterator();
			while (it.hasNext()) {
				deleteVersionContainer((VersionContainer) it.next());
				it.remove();
			}
		}
		deleteObject(list);
	}
	
	/**
	 * Deletes the given VersionContainer.
	 */
	public void deleteVersionContainer(VersionContainer container) {		
		Iterator it = container.getChildLists().iterator();
		while (it.hasNext()) {
			deleteComponentList((ComponentList) it.next());	
		}
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
	 * @return <code>true</code> if there was anything to publish.
	 */
	public boolean publishContainer(VersionContainer container) {
		boolean result = false;
		Set childLists = container.getChildLists();
		if (childLists != null) {
			Iterator it = childLists.iterator();
			while (it.hasNext()) {
				ComponentList childList = (ComponentList) it.next();
				result |= publishList(childList);
			}
		}
		ComponentVersion preview = container.getPreviewVersion();
		if (preview != null) {
			ComponentVersion liveVersion = container.getLiveVersion();
			if (liveVersion != null) {
				deleteComponentVersion(liveVersion);
			}
			container.setLiveVersion(preview);
			container.setPreviewVersion(null);
			updateVersionContainer(container);
			result = true;
		}
		return result;
	}
	
	/**
	 * Publishes the given list.
	 * @return <code>true</code> if there was anything to publish
	 */
	public boolean publishList(ComponentList componentList) {
		boolean result = false;
		if (componentList.isDirty()) {
			log.debug("List " + componentList + " is dirty and will be published.");
			result = true;
			List previewList = componentList.getPreviewContainers();
			List liveList = componentList.getLiveContainers();
			if (liveList == null) {
				liveList = new ArrayList();
			}
			else {
				Iterator it = liveList.iterator();
				while (it.hasNext()) {
					VersionContainer container = (VersionContainer) it.next();
					if (!previewList.contains(container)) {
						deleteVersionContainer(container);
						it.remove();
					}
				}
				liveList.clear();
			}
			liveList.addAll(previewList);
			previewList.clear();
			componentList.setDirty(false);
			updateComponentList(componentList);
		}
		
		Iterator it = componentList.getLiveContainers().iterator();
		while (it.hasNext()) {
			VersionContainer container = (VersionContainer) it.next();
			result |= publishContainer(container);
		}
		
		if (eventMulticaster != null) {
			String path = repository.getUrl(componentList);
			eventMulticaster.multicastEvent(new ContentChangedEvent(this, path));
		}
		
		return result;
	}
	
	/**
	 * Discards all changes made to the given list.
	 */
	public void discardList(ComponentList componentList) {
		List previewList = componentList.getPreviewContainers();
		List liveList = componentList.getLiveContainers();
		if (componentList.isDirty()) {
			componentList.setPreviewContainers(null);
			componentList.setDirty(false);
			Iterator it = previewList.iterator();
			while (it.hasNext()) {
				VersionContainer container = (VersionContainer) it.next();
				if (liveList == null || !liveList.contains(container)) {
					deleteVersionContainer(container);
					it.remove();
				}
			}
			updateComponentList(componentList);	
		}
		Iterator it = liveList.iterator();
		while (it.hasNext()) {
			VersionContainer container = (VersionContainer) it.next();
			discardContainer(container);
		}
	}
	
	/**
	 * Discards all changes made to the given VersionContainer.
	 * @return <code>true</code> if there was anything to discard.
	 */
	public boolean discardContainer(VersionContainer container) {
		ComponentVersion preview = container.getPreviewVersion();
		if (preview != null) {
			container.setPreviewVersion(null);
			updateVersionContainer(container);
			deleteComponentVersion(preview);
			return true;
		}
		return false;
	}
	
	/**
	 * Creates copies of all ComponentLists under the given path and sets 
	 * their path to the specified <code>newPath</code>.
	 */
	public void copyComponentLists(String type, String oldPath, String newPath) {
		List lists = findComponentLists(type, oldPath);
		if (lists != null) {
			Iterator it = lists.iterator();
			while (it.hasNext()) {
				ComponentList list = (ComponentList) it.next();
				ComponentList copy = copyComponentList(list, newPath);
				saveComponentList(copy);					
			}
		}
	}	

	private ComponentList copyComponentList(ComponentList list, String path) {
		ComponentList copy = new ComponentList();
		Location location = new Location(list.getLocation());
		location.setPath(path);
		copy.setLocation(location);
		copy.setDirty(list.isDirty());
		copy.setLiveContainers(copyContainers(list.getLiveContainers(), path));
		copy.setPreviewContainers(copyContainers(list.getPreviewContainers(), path));
		return copy;
	}
	
	private List copyContainers(List source, String path) {
		if (source == null) {
			return null;
		}
		List dest = new ArrayList(source.size());
		Iterator it = source.iterator();
		while (it.hasNext()) {
			VersionContainer container = (VersionContainer) it.next();
			dest.add(copyVersionContainer(container, path));
		}
		return dest;
	}
	
	private VersionContainer copyVersionContainer(VersionContainer container, String path) {
		VersionContainer copy = new VersionContainer();
		if (container.getLiveVersion() != null) {
			copy.setLiveVersion(copyComponentVersion(
					container.getLiveVersion()));
		}
		if (container.getPreviewVersion() != null) {
			copy.setPreviewVersion(copyComponentVersion(
					container.getPreviewVersion()));
		}
		Set childLists = container.getChildLists();
		if (childLists != null) {
			HashSet clonedLists = new HashSet(); 
			Iterator it = childLists.iterator();
			while (it.hasNext()) {
				ComponentList list = (ComponentList) it.next();
				ComponentList clonedList = copyComponentList(list, path);
				clonedList.setParent(copy);
				clonedLists.add(clonedList);
			}
			copy.setChildLists(clonedLists);
		}
		return copy;
	}
	
	private ComponentVersion copyComponentVersion(ComponentVersion version) {
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
