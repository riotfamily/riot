package org.riotfamily.components.render.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.model.Component;

public class EditModeComponentRenderer {

	private ComponentRepository repository;
	
	public EditModeComponentRenderer(ComponentRepository repository) {
		this.repository = repository;
	}

	public void renderComponent(ComponentRenderer renderer, 
			Component component, int position, int listSize,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {

		String type = component.getType();
		String formUrl = repository.getFormUrl(type, 
				component.getList().getContainer().getId() , component.getId());
		
		String className = "riot-list-component riot-component " +
				"riot-component-" + type;
		
		if (formUrl != null) {
			className += " riot-form";
		}
		
		TagWriter wrapper = new TagWriter(response.getWriter());
		wrapper.start(Html.DIV)
				.attribute(Html.COMMON_CLASS, className)
				.attribute("riot:componentId", component.getId().toString())
				.attribute("riot:componentType", type)
				.attribute("riot:form", formUrl)
				.body();

		renderer.render(component, true, position, listSize, request, response);
		
		wrapper.end();
	}
}
