package org.riotfamily.common.freemarker;

import java.util.Collection;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PluginObjectWrapper extends DefaultObjectWrapper {

	private Collection<ObjectWrapperPlugin> plugins;
	
	public PluginObjectWrapper(Collection<ObjectWrapperPlugin> plugins) {
		this.plugins = plugins;
	}
	
	public TemplateModel wrap(Object obj) throws TemplateModelException {
		if (obj == null) {
			return null;
		}
		if (obj instanceof TemplateModel) {
			return (TemplateModel) obj;
		}
		for (ObjectWrapperPlugin plugin : plugins) {
			if (plugin.supports(obj)) {
				return plugin.wrapSupportedObject(obj, this);
			}
		}
		return wrapUnsupportedObject(obj);
	}
	
	public TemplateModel wrapUnsupportedObject(Object obj) 
			throws TemplateModelException {
		
		return super.wrap(obj);
	}
	
}
