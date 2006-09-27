package org.riotfamily.pages.page;

/**
 * Alias for a page. Aliases are created whenever a page (or one of it's 
 * ancestors) is renamed or moved.
 */
public class PageAlias {

	private String path;
	
	private PersistentPage page;
	
	public PageAlias() {
	}
	
	public PageAlias(String path, PersistentPage page) {
		this.path = path;
		this.page = page;
	}

	public PersistentPage getPage() {
		return this.page;
	}

	public void setPage(PersistentPage page) {
		this.page = page;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}


}
