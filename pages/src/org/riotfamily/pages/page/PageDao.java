package org.riotfamily.pages.page;

import java.util.List;


public interface PageDao {

	public List listRootPages();
	
	public List listAliases();
	
	public void deleteAlias(String path);
	
	public void addAlias(String path, PersistentPage page);
	
	public void clearAliases(PersistentPage page);
	
	public PersistentPage loadPage(Long id);
	
	public void savePage(PersistentPage page);
	
	public void updatePage(PersistentPage page);
	
	public void deletePage(PersistentPage page);
}
