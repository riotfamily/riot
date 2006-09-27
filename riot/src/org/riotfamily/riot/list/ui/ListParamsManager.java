package org.riotfamily.riot.list.ui;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.support.ListParamsImpl;
import org.springframework.web.util.WebUtils;

/**
 * Provides ListParams for the given request.
 */
public class ListParamsManager {

	private static final String SESSION_KEY = ListParamsManager.class.getName();
	
	public MutableListParams getListParams(ListConfig listConfig, 
			HttpServletRequest request) {
		
		Map map = getParamsMap(request);
		MutableListParams params = (MutableListParams) map.get(listConfig.getId());
		if (params == null) {
			params = new ListParamsImpl();
			map.put(listConfig.getId(), params);
		}
		return params;
	}

	protected Map getParamsMap(HttpServletRequest request) {
		return (Map) WebUtils.getOrCreateSessionAttribute(
				request.getSession(), SESSION_KEY, HashMap.class); 
	}

}
