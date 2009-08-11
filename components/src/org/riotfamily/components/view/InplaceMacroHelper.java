package org.riotfamily.components.view;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.render.list.ComponentListRenderer;
import org.riotfamily.components.support.EditModeUtils;
import org.riotfamily.core.security.AccessController;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class InplaceMacroHelper {

	private HttpServletRequest request;
	
	private HttpServletResponse response;

	private List<String> toolbarScripts;

	private List<DynamicToolbarScript> dynamicToolbarScripts;
	
	private ComponentListRenderer componentListRenderer;
	
	
	public InplaceMacroHelper(HttpServletRequest request,
			HttpServletResponse response, 
			List<String> toolbarScripts,
			List<DynamicToolbarScript> dynamicToolbarScripts, 
			ComponentListRenderer componentListRenderer) {

		this.request = request;
		this.response = response;
		this.toolbarScripts = toolbarScripts;
		this.dynamicToolbarScripts = dynamicToolbarScripts;
		this.componentListRenderer = componentListRenderer;
	}

	public boolean isEditMode() {
		return EditModeUtils.isEditMode(request);
	}
	
	public boolean isPreviewMode() {
		return EditModeUtils.isPreviewMode(request);
	}
	
	public boolean isLiveMode() {
		return EditModeUtils.isLiveMode(request);
	}
	
	public List<String> getToolbarScripts() {
		return this.toolbarScripts;
	}
	
	public boolean isEditGranted() {
		return AccessController.isGranted("toolbarEdit", request);
	}
	
	public boolean isPublishGranted() {
		return AccessController.isGranted("toolbarPublish", request);
	}

	public String getInitScript() {
		StringBuffer sb = new StringBuffer();
		for (DynamicToolbarScript script : dynamicToolbarScripts) {
			String js = script.generateJavaScript(request);
			if (js != null) {
				sb.append(js).append('\n');
			}
		}
		return sb.toString();
	}
		
	public String renderComponentList(ContentContainer container, 
			String key, Integer minComponents, Integer maxComponents,
			List<String> initalComponentTypes, 
			List<?> validComponentTypes)
			throws Exception {
		
		ComponentListConfig config = new ComponentListConfig(
				minComponents, maxComponents, 
				initalComponentTypes, validComponentTypes);
		
		return componentListRenderer.renderComponentList(container, key, config, 
				request, response);
	}
	
	public String renderNestedComponentList(Component parent, 
			String key, Integer minComponents, Integer maxComponents,
			List<String> initalComponentTypes, 
			List<?> validComponentTypes)
			throws Exception {
		
		ComponentListConfig config = new ComponentListConfig(
				minComponents, maxComponents, 
				initalComponentTypes, validComponentTypes);
		
		return componentListRenderer.renderNestedComponentList(parent, key, 
				config, request, response);
	}
}
