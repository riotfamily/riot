package org.riotfamily.website.cache;

import java.util.List;

import org.riotfamily.cachius.CachiusContext;
import org.riotfamily.common.util.Generics;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * StringModel subclass that tags cache items with a list of configured tags
 * whenever a property is read.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class TaggingStringModel extends StringModel {

	private List<String> tags = Generics.newArrayList();
	
	public TaggingStringModel(Object object, BeansWrapper wrapper) {
		super(object, wrapper);
	}
	
	public void addTag(String tag) {
		tags.add(tag);
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException {
		for (String tag : tags) {
			CachiusContext.tag(tag);
		}
		return super.get(key);
	}
	
}
