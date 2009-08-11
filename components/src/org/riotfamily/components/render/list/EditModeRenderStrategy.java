package org.riotfamily.components.render.list;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.meta.ComponentMetaDataProvider;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.render.component.ComponentRenderer;
import org.riotfamily.components.render.component.EditModeComponentRenderer;
import org.riotfamily.forms.factory.FormRepository;

public class EditModeRenderStrategy extends DefaultRenderStrategy {

	private EditModeComponentRenderer editModeRenderer;
	
	public EditModeRenderStrategy(ComponentRenderer renderer,
			ComponentMetaDataProvider metaDataProvider,
			FormRepository formRepository, ComponentListRenderer listRenderer) {
		
		super(renderer);
		editModeRenderer = new EditModeComponentRenderer(
				renderer, metaDataProvider, formRepository);
		
		listRenderer.setEditModeRenderStrategy(this);
	}
	
	/**
	 * Overrides the default implementation to render a DIV tag around the
	 * actual list. The DIV has attributes that are required for the
	 * Riot toolbar JavaScript.
	 */
	@Override
	public void render(ComponentList list, 
			ComponentListConfig config,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		DocumentWriter doc = new DocumentWriter(response.getWriter());
		
		doc.start("div")
			.attribute("class", "riot-component-list")
			.attribute("riot:listId", list.getId());
		
		doc.body();
		super.render(list, config, request, response);
		doc.end();
		
		doc.start("script").body("riotComponentListConfig" + list.getId() 
				+ " = " + config.toJSON() + ";", false);
		
		doc.end();
	}

	/**
	 * Overrides the default implementation to render a DIV tag around the
	 * actual component. The DIV has attributes that are required for the
	 * Riot toolbar JavaScript.
	 * @throws IOException
	 */
	@Override
	protected void renderComponent(Component component, 
			ComponentListConfig config, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		editModeRenderer.render(component, request, response);
	}

}
