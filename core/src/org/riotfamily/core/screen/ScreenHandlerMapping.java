package org.riotfamily.core.screen;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.mapping.ReverseHandlerMapping;
import org.riotfamily.common.servlet.ServletUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

public class ScreenHandlerMapping extends AbstractHandlerMapping
		implements ReverseHandlerMapping {
	
	private ScreenRepository repository;
	
	private String servletPrefix = "";
	
	public ScreenHandlerMapping(ScreenRepository repository) {
		this.repository = repository;
	}
	
	public void setServletPrefix(String servletPrefix) {
		this.servletPrefix = servletPrefix;
	}

	@Override
	protected Object getHandlerInternal(HttpServletRequest request)
			throws Exception {
		
		String path = ServletUtils.getPathWithoutServletMapping(request);
		String[] s = StringUtils.tokenizeToStringArray(path, "/");
		
		if (!StringUtils.hasLength(path) || path.equals("/") 
				|| "screen".equals(stringAt(s, 0))) {
			
			String screenId = stringAt(s, 1);
			String objectId = stringAt(s, 2);
			String parentId = null;
			boolean parentIsNode = false;
			if ("-".equals(objectId)) {
				objectId = null;
				parentId = stringAt(s, 3);
				String parentScreenId = stringAt(s, 4);
				parentIsNode = screenId.equals(parentScreenId);
			}
			
			RiotScreen screen = repository.getScreen(screenId);
			ScreenContext context = new ScreenContext(
					screen, request, objectId, parentId, parentIsNode);
			
			context.expose();
			return screen;
		}
		
		return null;
	}
	
	private static String stringAt(String[] a, int i) {
		if (a != null && a.length > i) {
			return a[i];
		}
		return null;
	}

	public String getUrlForHandler(String handlerName, Object attributes) {
		if (attributes instanceof ScreenContext) {
			ScreenContext context = (ScreenContext) attributes;
			StringBuilder path = new StringBuilder("/screen/");
			path.append(handlerName).append('/');
			if (context.getObjectId() != null) {
				path.append(context.getObjectId());
			}
			else if (context.getParentId() != null) {
				path.append("-/").append(context.getParentId());
				if (context.isNestedTreeItem()) {
					path.append('/').append(handlerName);	
				}
			}
			return servletPrefix + path.toString();
		}
		return null;
	}
}
