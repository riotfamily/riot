package org.riotfamily.components.render.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.common.util.RiotLog;
import org.riotfamily.components.meta.ComponentMetaDataProvider;
import org.riotfamily.components.model.Component;
import org.riotfamily.forms.factory.FormRepository;

public class EditModeComponentDecorator implements ComponentRenderer {

	private RiotLog log = RiotLog.get(this);
	
	private FormRepository formRepository;
	
	private ComponentRenderer renderer;
	
	private ComponentMetaDataProvider metaDataProvider;
	
	public EditModeComponentDecorator(ComponentRenderer renderer, 
			ComponentMetaDataProvider metaDataProvider,
			FormRepository formRepository) {
		
		this.renderer = renderer;
		this.metaDataProvider = metaDataProvider;
		this.formRepository = formRepository;
	}

	public void render(Component component, int position, int listSize,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {

		String type = component.getType();
		
		String className = "riot-content riot-component " +
				"riot-component-" + type;
		
		String formId = metaDataProvider.getMetaData(type).getForm();
		if (formId != null) {
			if (!formRepository.containsForm(formId)) {
				log.error("The configured component form [%s] does not exist", formId);
				formId = null;
			}
		}
		else if (formRepository.containsForm(type)) {
			formId = type;
		}
		
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
