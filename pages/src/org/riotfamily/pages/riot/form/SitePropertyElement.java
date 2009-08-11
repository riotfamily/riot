package org.riotfamily.pages.riot.form;

import org.riotfamily.forms.ElementFactory;
import org.riotfamily.pages.model.Site;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class SitePropertyElement extends AbstractLocalizedElement {

	private Site masterSite;
	
	public SitePropertyElement(ElementFactory elementFactory,
			LocalizedEditorBinder binder, Site masterSite) {
		
		super(elementFactory, binder);
		this.masterSite = masterSite;
	}

	protected boolean isLocalized() {
		return masterSite != null;
	}
	
	protected Object getMasterValue(String property) {
		if (masterSite.getProperties() != null) {
			return masterSite.getProperties().get(property);
		}
		return null;
	}
	
}
