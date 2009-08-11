package org.riotfamily.components.render.list;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.model.ComponentList;

public interface RenderStrategy {
	
	public void render(ComponentList componentList, 
			ComponentListConfig config, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception;
	
}
