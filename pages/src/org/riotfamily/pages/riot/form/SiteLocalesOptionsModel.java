package org.riotfamily.pages.riot.form;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.riotfamily.common.collection.ToStringComparator;
import org.riotfamily.forms.options.OptionsModel;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Site;

public class SiteLocalesOptionsModel implements OptionsModel {

	private PageDao dao;
	
	public SiteLocalesOptionsModel(PageDao dao) {
		this.dao = dao;
	}

	public Collection getOptionValues() {
		Set locales = new TreeSet(new ToStringComparator());
		Iterator it = dao.listSites().iterator();
		while (it.hasNext()) {
			Site site = (Site) it.next();
			locales.add(site.getLocale());
		}
		return locales;
	}

}
