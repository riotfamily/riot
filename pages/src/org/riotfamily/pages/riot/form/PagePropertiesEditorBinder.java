package org.riotfamily.pages.riot.form;

import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.riot.form.ContentContainerEditorBinder;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageProperties;

public class PagePropertiesEditorBinder extends ContentContainerEditorBinder {

	private Page page;
	
	public PagePropertiesEditorBinder(Page page) {
		this.page = page;
	}

	protected ContentContainer createContainer() {
		return new PageProperties(page);
	}

}
