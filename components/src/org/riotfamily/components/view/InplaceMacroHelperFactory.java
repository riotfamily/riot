package org.riotfamily.components.view;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.view.MacroHelperFactory;
import org.riotfamily.components.render.list.ComponentListRenderer;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class InplaceMacroHelperFactory implements MacroHelperFactory {

	private List<String> toolbarScripts = Collections.emptyList();
	
	private List<DynamicToolbarScript> dynamicToolbarScripts = Collections.emptyList();

	private ComponentListRenderer componentListRenderer;

	public InplaceMacroHelperFactory(
			ComponentListRenderer componentListRenderer) {
		
		this.componentListRenderer = componentListRenderer;
	}
	
	public void setToolbarScripts(List<String> toolbarScripts) {
		this.toolbarScripts = toolbarScripts;
	}
	
	public void setDynamicToolbarScripts(List<DynamicToolbarScript> dynamicToolbarScripts) {
		this.dynamicToolbarScripts = dynamicToolbarScripts;
	}

	public Object createMacroHelper(HttpServletRequest request,
			HttpServletResponse response, Map<String, ?> model) {

		
		return new InplaceMacroHelper(request, response, toolbarScripts, 
				dynamicToolbarScripts, componentListRenderer);
	}
}
