package org.riotfamily.pages.page.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.pages.component.dao.ComponentDao;
import org.riotfamily.pages.page.PageDao;
import org.riotfamily.pages.page.PersistentPage;
import org.riotfamily.pages.setup.Plumber;
import org.riotfamily.pages.setup.WebsiteConfig;
import org.riotfamily.pages.setup.WebsiteConfigAware;
import org.riotfamily.riot.dao.CopyAndPasteEnabledDao;
import org.riotfamily.riot.dao.CutAndPasteEnabledDao;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.ParentChildDao;
import org.riotfamily.riot.dao.SwappableItemDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.ApplicationEventMulticaster;

public class RiotPageDao implements ParentChildDao, SwappableItemDao, 
		CutAndPasteEnabledDao, CopyAndPasteEnabledDao, 
		ApplicationContextAware, WebsiteConfigAware {

	private static Log log = LogFactory.getLog(RiotPageDao.class);
	
	private PageDao dao;
	
	private Class entityClass = PersistentPage.class;
	
	private ApplicationEventMulticaster eventMulticaster;
	
	private ComponentDao componentDao;

	
	public RiotPageDao(PageDao dao, ApplicationEventMulticaster eventMulticaster) {
		this.dao = dao;
		this.eventMulticaster = eventMulticaster;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		Plumber.register(applicationContext, this);
	}
	
	public void setWebsiteConfig(WebsiteConfig config) {
		componentDao = config.getComponentDao();
	}
	

	public Class getEntityClass() {
		return entityClass;
	}
	
	public void setEntityClass(Class itemClass) {
		this.entityClass = itemClass;
	}

	public String getObjectId(Object entity) {
		return ((PersistentPage) entity).getId().toString();
	}
	
	public Collection list(Object parent, ListParams params) {
		if (parent != null) {
			PersistentPage parentPage = (PersistentPage) parent;
			return parentPage.getPersistentChildPages();
		}
		else {
			return dao.listRootPages();
		}
	}

	public int getListSize(Object parent, ListParams params) {
		return -1;
	}

	public Object getParent(Object entity) {
		PersistentPage page = (PersistentPage) entity;
		return page.getParent();
	}
	
	public Object load(String objectId) {
		return dao.loadPage(new Long(objectId));
	}

	public void save(Object entity, Object parent) {
		PersistentPage page = (PersistentPage) entity;
		if (parent != null) {
			PersistentPage parentPage = (PersistentPage) parent;
			parentPage.addChildPage(page);
			dao.savePage(page);
			dao.updatePage(parentPage);
		}
		else {
			page.initPosition(dao.listRootPages());
			dao.savePage(page);
		}
		dao.deleteAlias(page.getPath());
		firePageMappingEvent();
	}

	public void update(Object entity) {
		PersistentPage page = (PersistentPage) entity;
		String oldPath = page.getPath();
		page.updatePath();
		dao.updatePage(page);
		updateAliases(page, oldPath);
		firePageMappingEvent();
	}

	public void delete(Object entity, Object parent) {
		PersistentPage page = (PersistentPage) entity;
		if (parent != null) {
			PersistentPage parentPage = (PersistentPage) parent;
			parentPage.getPersistentChildPages().remove(page);
			dao.updatePage(parentPage);
		}
		dao.deletePage(page);
		dao.clearAliases(page);
		Collection childPages = page.getPersistentChildPages();
		if (childPages != null) {
			Iterator it = childPages.iterator();
			while (it.hasNext()) {
				PersistentPage child = (PersistentPage) it.next();
				dao.clearAliases(child);
			}
		}
		firePageMappingEvent();
	}
	
	public void addChild(Object item, Object parent) {
		PersistentPage page = (PersistentPage) item;
		String oldPath = page.getPath();
		
		PersistentPage parentPage = (PersistentPage) parent;
		changeNameIfExists(parentPage, page);
		if (parent != null) {
			parentPage.addChildPage(page);
			dao.updatePage(parentPage);
		}
		else {
			page.setParent(null);
			page.updatePath();
			page.initPosition(dao.listRootPages());
			dao.updatePage(page);
		}
		updateAliases(page, oldPath);
		firePageMappingEvent();
	}

	public void addCopy(Object item, Object parent) {
		PersistentPage page = (PersistentPage) item;
		PersistentPage parentPage = (PersistentPage) parent;
		PersistentPage copy = page.copy();
		changeNameIfExists(parentPage, copy);
		save(copy, parent);
		componentDao.copyComponentLists(page.getPath(), copy.getPath());
	}
	
	private void changeNameIfExists(PersistentPage parent, 
			PersistentPage page) {
		
		Collection siblings = parent != null 
				? parent.getChildPages()
				: dao.listRootPages();
		
		int i = 1;
		String name = page.getPathComponent();
		while (PageUtils.getPage(siblings, name) != null) {
			name = name + "-" + i++;
		}
		page.setPathComponent(name);
	}
	
	public void removeChild(Object item, Object parent) {
		PersistentPage page = (PersistentPage) item;
		if (parent != null) {
			PersistentPage parentPage = (PersistentPage) parent;
			parentPage.getPersistentChildPages().remove(page);
			dao.updatePage(parentPage);
		}
	}

	public void swapEntity(Object entity, Object parent, 
			ListParams params, int swapWith) {

		PersistentPage page = (PersistentPage) entity;
		PersistentPage parentPage = (PersistentPage) parent;
		
		List siblings = null;
		if (parentPage != null) {
			siblings = new ArrayList(parentPage.getPersistentChildPages());
		}
		else {
			siblings = dao.listRootPages();
		}
		
		PersistentPage target = (PersistentPage) siblings.get(swapWith);
		
		int newPos = target.getPosition();
		int oldPos = page.getPosition();
		
		target.setPosition(oldPos);
		page.setPosition(newPos);
		
		dao.updatePage(page);
		dao.updatePage(target);
		
		if (parentPage != null) {
			Collection c = parentPage.getPersistentChildPages();
			c.remove(page);
			c.remove(target);
			c.add(page);
			c.add(target);
		}
		firePageMappingEvent();
	}
	
	protected void updateAliases(PersistentPage page, String oldPath) {
		log.debug("Old path: " + oldPath);
		String newPath = page.getPath();
		log.debug("New path: " + newPath);
		if (!newPath.equals(oldPath)) {
			dao.deleteAlias(newPath);
			dao.addAlias(oldPath, page);
			if (componentDao != null) {
				componentDao.updatePaths(oldPath, newPath);
			}
			Collection childPages = page.getPersistentChildPages();
			if (childPages != null) {
				Iterator it = childPages.iterator();
				while (it.hasNext()) {
					PersistentPage child = (PersistentPage) it.next();
					oldPath = child.getPath();
					child.updatePath();
					updateAliases(child, oldPath);
				}
			}
		}
	}
	
	protected void firePageMappingEvent() {
		if (eventMulticaster != null) {
			eventMulticaster.multicastEvent(PageMappingEvent.MAPPINGS_MODIFIED);
		}
	}


}
