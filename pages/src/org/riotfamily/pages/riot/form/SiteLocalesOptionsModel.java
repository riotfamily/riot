package org.riotfamily.pages.riot.form;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.riotfamily.common.collection.ToStringComparator;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.options.OptionsModel;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Site;

public class SiteLocalesOptionsModel implements OptionsModel {

	private PageDao dao;
	
	public SiteLocalesOptionsModel(PageDao dao) {
		this.dao = dao;
	}

	public Collection<?> getOptionValues(Element element) {
		Set<Locale> locales = new TreeSet<Locale>(new ToStringComparator());
		for (Site site : dao.listSites()) {
			locales.add(site.getLocale());
		}
		return locales;
	}

}
