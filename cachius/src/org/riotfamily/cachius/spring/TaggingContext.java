package org.riotfamily.cachius.spring;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

/**
 * Provides static methods to tag cache items.
 */
public class TaggingContext {

	private static final String REQUEST_ATTRIBUTE = 
			TaggingContext.class.getName();
	
	private Log log = LogFactory.getLog(TaggingContext.class);
	
	private TaggingContext parent;
		
	private String[] tags;
			
	private TaggingContext(TaggingContext parent) {
		this.parent = parent;
	}
	
	public TaggingContext getParent() {
		return this.parent;
	}

	public void addTag(String tag) {
		if (tags == null) {
			tags = new String[] { tag };
		}
		else {
			tags = StringUtils.addStringToArray(tags, tag);
		}
		if (parent != null) {
			parent.addTag(tag);
		}
		else {
			log.debug("Adding tag: " + tag);
		}
	}
	
	public String[] getTags() {
		return this.tags;
	}

	public static void tag(HttpServletRequest request, String tag) {
		TaggingContext context = getContext(request);
		if (context != null) {
			context.addTag(tag);
		}
	}
	
	public static void openNestedContext(HttpServletRequest request) {
		TaggingContext parent = getContext(request);
		setContext(request, new TaggingContext(parent));
	}
	
	public static String[] popTags(HttpServletRequest request) {
		TaggingContext top = getContext(request);
		if (top != null) {
			setContext(request, top.getParent());
			return top.getTags();
		}
		return null;
	}
	
	private static TaggingContext getContext(HttpServletRequest request) {
		return (TaggingContext) request.getAttribute(REQUEST_ATTRIBUTE);
	}
	
	private static void setContext(HttpServletRequest request, 
			TaggingContext context) {
		
		request.setAttribute(REQUEST_ATTRIBUTE, context);
	}
	
}
