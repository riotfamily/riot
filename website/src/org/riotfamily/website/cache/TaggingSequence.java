package org.riotfamily.website.cache;

import java.util.Collection;
import java.util.List;

import org.riotfamily.cachius.CachiusContext;
import org.riotfamily.common.util.Generics;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleSequence;

/**
 * SimpleSequence subclass that tags cache items with a list of configured tags
 * whenever the size of the sequence is accessed.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class TaggingSequence extends SimpleSequence {

	private List<String> tags = Generics.newArrayList();
	
	public TaggingSequence(Collection<?> collection, ObjectWrapper wrapper) {
		super(collection, wrapper);
	}
	
	public void addTag(String tag) {
		tags.add(tag);
	}

	@Override
	public int size() {
		for (String tag : tags) {
			CachiusContext.tag(tag);
		}
		return super.size();
	}
	
}
