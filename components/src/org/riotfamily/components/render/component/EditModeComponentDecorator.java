package org.riotfamily.components.render.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.components.model.Component;
import org.riotfamily.forms.factory.FormRepository;

public class EditModeComponentDecorator implements ComponentRenderer {

	private FormRepository formRepository;
	
	private ComponentRenderer renderer;
	
	public EditModeComponentDecorator(ComponentRenderer renderer, 
			FormRepository formRepository) {
		
		this.renderer = renderer;
		this.formRepository = formRepository;
	}

	public void render(Component component, int position, int listSize,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {

		String type = component.getType();
		
		String className = "riot-content riot-component " +
				"riot-component-" + type;
		
		String formId = formRepository.containsForm(type) ? type : null;
		if (formId != null) {
			className += " riot-form";
		}
		
		TagWriter wrapper = new TagWriter(response.getWriter());
		wrapper.start("div")
				.attribute("class", className)
				.attribute("riot:contentId", component.getId().toString())
				.attribute("riot:componentType", type)
				.attribute("riot:form", formId)
				.body();

		renderer.render(component, position, listSize, request, response);
		
		wrapper.end();
	}
}
