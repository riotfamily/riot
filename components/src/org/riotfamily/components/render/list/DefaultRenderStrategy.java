package org.riotfamily.components.render.list;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.RiotLog;
import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.render.component.ComponentRenderer;

public class DefaultRenderStrategy implements RenderStrategy {
	
	protected RiotLog log = RiotLog.get(getClass());
	
	private ComponentRenderer renderer;
	
	public DefaultRenderStrategy(ComponentRenderer renderer) {
		this.renderer = renderer;
	}
		
	public void render(ComponentList list,
			ComponentListConfig config,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		if (list == null || list.isEmpty()) {
			onEmptyComponentList(config, request, response);
			return;
		}
		for (Component component : list) {
			renderComponent(component, config, request, response);
		}
	}
	
	protected void onEmptyComponentList(ComponentListConfig config, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {

	}
	
	protected void renderComponent(Component component, 
			ComponentListConfig config, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		renderer.render(component, request, response);
	}

}
