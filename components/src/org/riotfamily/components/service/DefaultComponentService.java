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
package org.riotfamily.components.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.cachius.Cache;
import org.riotfamily.common.web.event.ContentChangedEvent;
import org.riotfamily.common.web.file.FileStore;
import org.riotfamily.common.web.file.FileStoreLocator;
import org.riotfamily.common.web.file.FileUtils;
import org.riotfamily.components.cache.ComponentCacheUtils;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.config.component.Component;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.ComponentVersion;
import org.riotfamily.components.model.FileStorageInfo;
import org.riotfamily.components.model.Location;
import org.riotfamily.components.model.VersionContainer;
import org.riotfamily.components.property.PropertyProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class DefaultComponentService implements InitializingBean, ComponentService {

	private static final Log log = LogFactory.getLog(DefaultComponentService.class);

	private ComponentRepository repository;

	private Cache cache;
	
	private ComponentDao dao;

	private ApplicationEventMulticaster eventMulticaster;

	private FileStoreLocator fileStoreLocator;
	
	public DefaultComponentService() {
	}

	public void setRepository(ComponentRepository repository) {
		this.repository = repository;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}
	
	public void setComponentDao(ComponentDao dao) {
		this.dao = dao;
	}

	public void setEventMulticaster(ApplicationEventMulticaster eventMulticaster) {
		this.eventMulticaster = eventMulticaster;
	}
	
	public void setFileStoreLocator(FileStoreLocator fileStoreLocator) {
		this.fileStoreLocator = fileStoreLocator;
	}
	
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(repository, "A ComponentRepository must be set.");
	}

	public void updateComponentVersion(ComponentVersion version) {
		if (version.getId() != null) {
			ComponentCacheUtils.invalidateContainer(
					cache, version.getContainer(), true);

			dao.updateComponentVersion(version);
		}
	}

	public void updateComponentProperties(ComponentVersion version, Map properties) {
		Component component = repository.getComponent(version);
		Iterator it = component.getPropertyProcessors().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			PropertyProcessor pp = (PropertyProcessor) entry.getValue();
			Object object = properties.get(key);
			properties.put(key, pp.convertToString(object));
		}
		version.setProperties(properties);
		
		List listeners = component.getUpdateListeners();
		if (listeners != null) {
			ComponentUpdate update = new ComponentUpdate(dao, version, fileStoreLocator);
			it = listeners.iterator();
			while (it.hasNext()) {
				UpdateListener listener = (UpdateListener) it.next();
				try {
					listener.onUpdate(update);
				}
				catch (Exception e) {
					log.error("Error in UpdateListener", e);
				}
			}
		}
		updateComponentVersion(version);
	}
	
	
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

	public ComponentVersion getOrCreateVersion(Long containerId, boolean live) {
		VersionContainer container = dao.loadVersionContainer(containerId);
		return getOrCreateVersion(container, null, live);
	}
	
	public ComponentVersion getOrCreateVersion(
			VersionContainer container, String type, boolean live) {

		if (live) {
			ComponentVersion liveVersion = container.getLiveVersion();
			if (liveVersion == null) {
				liveVersion = new ComponentVersion(type);
				container.setLiveVersion(liveVersion);
				dao.saveComponentVersion(liveVersion);
				dao.updateVersionContainer(container);
			}
			return liveVersion;
		}
		else {
			ComponentList list = container.getList();
			if (list != null && !list.isDirty()) {
				getOrCreatePreviewContainers(list);
				dao.updateComponentList(list);
			}
			ComponentVersion previewVersion = container.getPreviewVersion();
			if (previewVersion == null) {
				ComponentVersion liveVersion = container.getLiveVersion();
				if (liveVersion != null) {
					previewVersion = copyComponentVersion(liveVersion);
				}
				else {
					previewVersion = new ComponentVersion(type);
					dao.saveComponentVersion(previewVersion);
				}
				container.setPreviewVersion(previewVersion);
				dao.updateVersionContainer(container);
			}
			return previewVersion;
		}
	}

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

		dao.updateComponentList(componentList);
		return container;
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
		dao.saveComponentVersion(version);
		if (live) {
			container.setLiveVersion(version);
		}
		else {
			container.setPreviewVersion(version);
		}
		dao.saveVersionContainer(container);
		return container;
	}

	public void deleteComponentLists(String type, String path) {
		List componentLists = dao.findComponentLists(type, path);
		Iterator it = componentLists.iterator();
		while (it.hasNext()) {
			ComponentList list = (ComponentList) it.next();
			deleteComponentList(list);
		}
	}

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
		dao.deleteComponentList(list);
	}

	/* (non-Javadoc)
	 * @see org.riotfamily.components.service.ComponentService#deleteVersionContainer(org.riotfamily.components.model.VersionContainer)
	 */
	public void deleteVersionContainer(VersionContainer container) {
		Iterator it = container.getChildLists().iterator();
		while (it.hasNext()) {
			ComponentList list = (ComponentList) it.next();
			it.remove();
			deleteComponentList(list);
		}
		deleteComponentVersion(container.getLiveVersion());
		deleteComponentVersion(container.getPreviewVersion());
		dao.deleteVersionContainer(container);
	}

	public void deleteComponentVersion(ComponentVersion version) {
		if (version != null) {
			Iterator it = dao.getFileStorageInfos(version.getType()).iterator();
			while (it.hasNext()) {
				FileStorageInfo fsi = (FileStorageInfo) it.next();
				String path = version.getProperty(fsi.getProperty());
				if (path != null) {
					FileStore store = fileStoreLocator.getFileStore(fsi.getFileStoreId());
					store.delete(path);
				}
			}
			dao.deleteComponentVersion(version);
		}
	}

	/* (non-Javadoc)
	 * @see org.riotfamily.components.service.ComponentService#publishContainer(org.riotfamily.components.model.VersionContainer)
	 */
	public boolean publishContainer(VersionContainer container) {
		boolean published = false;
		Set childLists = container.getChildLists();
		if (childLists != null) {
			Iterator it = childLists.iterator();
			while (it.hasNext()) {
				ComponentList childList = (ComponentList) it.next();
				published |= publishList(childList);
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
			dao.updateVersionContainer(container);
			published = true;
			if (container.getList() == null) {
				ComponentCacheUtils.invalidateContainer(cache, container, false);
			}
		}
		return published;
	}

	public boolean publishList(ComponentList componentList) {
		boolean published = false;
		if (componentList.isDirty()) {
			log.debug("List " + componentList + " is dirty and will be published.");
			published = true;
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
			dao.updateComponentList(componentList);
		}

		Iterator it = componentList.getLiveContainers().iterator();
		while (it.hasNext()) {
			VersionContainer container = (VersionContainer) it.next();
			published |= publishContainer(container);
		}

		if (eventMulticaster != null) {
			String path = repository.getUrl(componentList);
			eventMulticaster.multicastEvent(new ContentChangedEvent(this, path));
		}

		if (published && cache != null) {
			ComponentCacheUtils.invalidateList(cache, componentList);
		}

		return published;
	}

	public boolean discardList(ComponentList componentList) {
		boolean discarded = false;
		List previewList = componentList.getPreviewContainers();
		List liveList = componentList.getLiveContainers();
		if (componentList.isDirty()) {
			discarded = true;
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
			dao.updateComponentList(componentList);
		}
		Iterator it = liveList.iterator();
		while (it.hasNext()) {
			VersionContainer container = (VersionContainer) it.next();
			discarded |= discardContainer(container);
		}
		return discarded;
	}

	public boolean discardContainer(VersionContainer container) {		
		boolean discarded = false;
		Set childLists = container.getChildLists();
		if (childLists != null) {
			Iterator it = childLists.iterator();
			while (it.hasNext()) {
				ComponentList childList = (ComponentList) it.next();
				discarded |= discardList(childList);
			}
		}
		ComponentVersion preview = container.getPreviewVersion();
		if (preview != null) {
			container.setPreviewVersion(null);
			dao.updateVersionContainer(container);
			deleteComponentVersion(preview);
			discarded = true;
		}
		return discarded;
	}

	public void copyComponentLists(String type, String oldPath, String newPath) {
		List lists = dao.findComponentLists(type, oldPath);
		if (lists != null) {
			Iterator it = lists.iterator();
			while (it.hasNext()) {
				ComponentList list = (ComponentList) it.next();
				ComponentList copy = copyComponentList(list, newPath);
				dao.saveComponentList(copy);
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

	/* (non-Javadoc)
	 * @see org.riotfamily.components.service.ComponentService#copyVersionContainer(org.riotfamily.components.model.VersionContainer)
	 */
	public VersionContainer copyVersionContainer(VersionContainer container) {
		VersionContainer copy = new VersionContainer();
		if (container.getLiveVersion() != null) {
			copy.setLiveVersion(copyComponentVersion(
					container.getLiveVersion()));
		}
		if (container.getPreviewVersion() != null) {
			copy.setPreviewVersion(copyComponentVersion(
					container.getPreviewVersion()));
		}
		return copy;
	}
	
	private VersionContainer copyVersionContainer(VersionContainer container, String path) {
		VersionContainer copy = copyVersionContainer(container);
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
		ComponentVersion copy = new ComponentVersion(version);
		Iterator it = dao.getFileStorageInfos(version.getType()).iterator();
		while (it.hasNext()) {
			FileStorageInfo fsi = (FileStorageInfo) it.next();
			String path = copy.getProperty(fsi.getProperty());
			if (path != null) {
				FileStore store = fileStoreLocator.getFileStore(fsi.getFileStoreId());
				try {
					copy.setProperty(fsi.getProperty(), FileUtils.copy(store, path));
				}
				catch (IOException e) {
					log.error("Error copying file.");
					copy.setProperty(fsi.getProperty(), null);
				}
			}
		}
		return copy;
	}
	
}
