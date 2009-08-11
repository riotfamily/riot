package org.riotfamily.pages.view;

import org.riotfamily.common.freemarker.FacadeTemplateModel;
import org.riotfamily.common.freemarker.ObjectWrapperPlugin;
import org.riotfamily.common.freemarker.PluginObjectWrapper;
import org.riotfamily.common.servlet.RequestHolder;
import org.riotfamily.pages.model.Page;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageObjectWrapperPlugin implements ObjectWrapperPlugin {

	public boolean supports(Object obj) {
		return obj instanceof Page;
	}

	public TemplateModel wrapSupportedObject(Object obj, 
			PluginObjectWrapper wrapper) 
			throws TemplateModelException {
		
		Page page = (Page) obj;
		PageFacade facade = new PageFacade(page, RequestHolder.getRequest());
		return new FacadeTemplateModel(facade, page, wrapper);
	}

}
