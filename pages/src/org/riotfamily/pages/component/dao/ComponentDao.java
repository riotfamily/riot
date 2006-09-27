package org.riotfamily.pages.component.dao;

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

}
