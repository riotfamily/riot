package org.riotfamily.pages.mapping;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.riotfamily.common.collection.TypedList;
import org.riotfamily.components.model.ContentContainerOwner;
import org.riotfamily.pages.config.VirtualPageType;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.riotfamily.pages.model.VirtualPage;

public abstract class VirtualPageResolver<T extends ContentContainerOwner> 
		implements ChildPageResolver {

	public Page resolvePage(VirtualPageType type, Page parent, String pathComponent) {
		T entity = load(parent, pathComponent);
		if (entity == null) {
			return null;
		}
		return new VirtualPage(type, parent, entity, pathComponent, getTitle(entity));
	}
	
	@SuppressWarnings("unchecked")
	public Page getPage(VirtualPageType type, Site site, Object object) {
		T entity = (T) object;
		Page parent = type.getParent().getPage(site, getParent(entity));
		return new VirtualPage(type, parent, entity, getPathComponent(entity),
				getTitle(entity));
	}
	
	protected Object getParent(T object) {
		return null;
	}
	
	public Collection<Page> listChildren(VirtualPageType type, Page parent) {
		Collection<T> children = getChildren(parent);
		if (children == null) {
			return null;
		}
		List<Page> pages = TypedList.newInstance(children);
		for (T entity : children) {
			pages.add(new VirtualPage(type, parent, entity,
					getPathComponent(entity), getTitle(entity)));
		}
		
		return pages;
	}
	
	@SuppressWarnings("unchecked")
	public Date getLastPublished(VirtualPageType type, Page parent, Object object) {
		T entity = (T) object;
		return getLastPublished(entity);
	}
	
	protected Date getLastPublished(T entity) {
		return entity.getContentContainer().getLastPublished();
	}
	
	protected abstract T load(Page parent, String pathComponent);

	protected abstract String getPathComponent(T entity);
	
	protected abstract String getTitle(T entity);
	
	protected abstract Collection<T> getChildren(Page parent);
}
