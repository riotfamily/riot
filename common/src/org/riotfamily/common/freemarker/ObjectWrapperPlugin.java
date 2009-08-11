package org.riotfamily.common.freemarker;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public interface ObjectWrapperPlugin {

	public boolean supports(Object obj);

	public TemplateModel wrapSupportedObject(Object obj, 
			PluginObjectWrapper wrapper)
			throws TemplateModelException;

}
