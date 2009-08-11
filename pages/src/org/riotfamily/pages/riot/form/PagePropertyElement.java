package org.riotfamily.pages.riot.form;

import org.riotfamily.forms.ElementFactory;
import org.riotfamily.pages.model.Page;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PagePropertyElement extends AbstractLocalizedElement {

	private Page masterPage;
	
	public PagePropertyElement(ElementFactory elementFactory,
			LocalizedEditorBinder binder, Page masterPage) {
		
		super(elementFactory, binder);
		this.masterPage = masterPage;
	}

	protected boolean isLocalized() {
		return masterPage != null;
	}
	
	protected Object getMasterValue(String property) {
		return masterPage.getPageProperties().getPreviewVersion().get(property);
	}
	
}
