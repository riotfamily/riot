package org.riotfamily.pages.riot.form;

import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.pages.model.Site;

/**
 * FormInitializer that imports form fields defined in content-forms.xml.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class SiteFormInitializer implements FormInitializer {

	private FormRepository repository;

	public SiteFormInitializer(FormRepository repository) {
		this.repository = repository;
	}

	public void initForm(Form form) {
		Site site = (Site) form.getBackingObject();
		SitePropertiesEditor spe = new SitePropertiesEditor(repository, site.getMasterSite());
		form.addElement(spe, "properties");
	}

}
