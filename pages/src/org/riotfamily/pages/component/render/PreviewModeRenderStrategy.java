package org.riotfamily.pages.component.render;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.pages.component.ComponentList;
import org.riotfamily.pages.component.ComponentListConfiguration;
import org.riotfamily.pages.component.ComponentVersion;
import org.riotfamily.pages.component.VersionContainer;

public class PreviewModeRenderStrategy extends AbstractRenderStrategy {

	public static final String EDIT_MODE_ATTRIBUTE = "riotComponentEditMode";
	
	public PreviewModeRenderStrategy(ComponentListConfiguration config,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		super(config, request, response);
	}
	
	/**
	 * Overrides the default implementation to return the preview components 
	 * in case the list is marked as dirty.
	 */
	protected List getComponentsToRender(ComponentList list) {
		if (list.isDirty()) { 
			return list.getPreviewList();
		}
		else {
			return list.getLiveList();
		}
	}
	
	/**
	 * Overrides the default implementation to return the component's preview
	 * version. In case no preview version exists, the live version is returned.
	 */
	protected ComponentVersion getVersionToRender(VersionContainer container) {
		ComponentVersion version = container.getPreviewVersion(); 
		if (version == null) {
			version = container.getLiveVersion();
		}
		return version;
	}
	
}
