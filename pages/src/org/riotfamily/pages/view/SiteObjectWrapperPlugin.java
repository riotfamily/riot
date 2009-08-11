package org.riotfamily.pages.view;

import org.riotfamily.common.freemarker.FacadeTemplateModel;
import org.riotfamily.common.freemarker.ObjectWrapperPlugin;
import org.riotfamily.common.freemarker.PluginObjectWrapper;
import org.riotfamily.common.servlet.RequestHolder;
import org.riotfamily.pages.model.Site;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class SiteObjectWrapperPlugin implements ObjectWrapperPlugin {

	public boolean supports(Object obj) {
		return obj instanceof Site;
	}

	public TemplateModel wrapSupportedObject(Object obj, 
			PluginObjectWrapper wrapper) 
			throws TemplateModelException {
		
		Site site = (Site) obj;
		SiteFacade facade = new SiteFacade(site, RequestHolder.getRequest());
		return new FacadeTemplateModel(facade, site, wrapper);
	}

}
