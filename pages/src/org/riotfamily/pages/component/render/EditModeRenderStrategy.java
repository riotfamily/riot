package org.riotfamily.pages.component.render;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.pages.component.ComponentList;
import org.riotfamily.pages.component.ComponentListConfiguration;
import org.riotfamily.pages.component.ComponentVersion;
import org.riotfamily.pages.component.VersionContainer;
import org.riotfamily.pages.component.dao.ComponentDao;

public class EditModeRenderStrategy extends PreviewModeRenderStrategy {
	
	private boolean renderOuterDiv = true;
	
	public EditModeRenderStrategy(ComponentListConfiguration config,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		super(config, request, response);
	}
	
	public void setRenderOuterDiv(boolean renderOuterDiv) {
		this.renderOuterDiv = renderOuterDiv;
	}

	/**
	 * Overrides the default implementation to render a DIV tag around the 
	 * actual list. The DIV has attributes that are required for the 
	 * Riot-Toolbar JavaScript.
	 */
	protected void renderComponentList(ComponentList list) throws IOException {
		request.setAttribute(EDIT_MODE_ATTRIBUTE, Boolean.TRUE);
		if (renderOuterDiv) {
			out.print("<div riot:listId=\"");
			out.print(list.getId());
			out.print("\" riot:controllerId=\"");
			out.print(config.getBeanName());
			out.print('"');
			if (config.getMaxComponents() != null) {
				out.print(" riot:maxComponents=\"");
				out.print(config.getMaxComponents());
				out.print('"');
			}
			out.print(" class=\"riot-components\">");
			super.renderComponentList(list);
			out.print("</div>");
		}
		else {
			super.renderComponentList(list);
		}
	}
	
	/**
	 * Overrides the default implementation to create a new list if no existing
	 * list is found.
	 * 
	 * @see #createNewList() 
	 */
	protected ComponentList getComponentList(String path, String key) {
		ComponentList list = super.getComponentList(path, key);
		if (list == null) {
			list = createNewList(path, key);
		}
		return list;
	}
	
	/**
	 * Creates a new ComponentList with an initial component set as defined by
	 * the controller.
	 */
	protected ComponentList createNewList(String path, String key) {
		ComponentList list = new ComponentList();
		ComponentDao componentDao = config.getComponentDao();
		list.setPath(path);
		list.setKey(key);
		componentDao.saveComponentList(list);
		String[] initialTypes = config.getInitialComponentTypes();
		if (initialTypes != null) {
			List containers = new ArrayList();
			for (int i = 0; i < initialTypes.length; i++) {
				VersionContainer container = new VersionContainer();
				ComponentVersion live = new ComponentVersion(initialTypes[i]);
				live.setContainer(container);
				container.setLiveVersion(live);
				componentDao.saveVersionContainer(container);
				containers.add(container);
			}
			list.setLiveList(containers);
			componentDao.updateComponentList(list);
		}
		return list;
	}
	
	/**
	 * Overrides the default implementation to render a DIV tag around the 
	 * actual component. The DIV has attributes that are required for the 
	 * Riot-Toolbar JavaScript.
	 * @throws IOException 
	 */
	protected void renderContainer(VersionContainer container, 
			String positionClassName) throws IOException {
		
		ComponentVersion version = getVersionToRender(container);
		out.print("<div riot:containerId=\"");
		out.print(container.getId());
		out.print("\" riot:componentType=\"");
		out.print(version.getType());
		out.print('"');
		
		String type = version.getType(); 
		String formId = config.getRepository().getFormId(type);
		if (formId != null) {
			out.print(" riot:formId=\"");
			out.print(formId);
			out.print('"');
		}
		
		out.print(" class=\"riot-component riot-component-");
		out.print(version.getType());
		out.print("\">");
		renderComponentVersion(version, positionClassName);
		out.print("</div>");
	}

}
