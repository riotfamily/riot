package org.riotfamily.components.render.component;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.riotfamily.common.freemarker.RiotFreeMarkerView;
import org.riotfamily.common.util.RiotLog;
import org.riotfamily.components.meta.ComponentMetaData;
import org.riotfamily.components.meta.ComponentMetaDataProvider;
import org.riotfamily.components.meta.FreeMarkerMetaDataExtractor;
import org.springframework.web.servlet.View;

import freemarker.template.Template;

public class ViewComponentMetaDataProvider implements ComponentMetaDataProvider {

	private RiotLog log = RiotLog.get(this);
	
	private ViewComponentRenderer renderer;
	
	public ViewComponentMetaDataProvider(ViewComponentRenderer renderer) {
		this.renderer = renderer;
	}

	public ComponentMetaData getMetaData(String type) {
		View view = renderer.getView(type);
		Map<String, String> data = null;
		if (view instanceof RiotFreeMarkerView) {
			try {
				Template template = ((RiotFreeMarkerView) view).getTemplate(Locale.getDefault());
				data = FreeMarkerMetaDataExtractor.extractMetaData(template);
			} 
			catch (IOException e) {
				log.warn("Failed to extract component meta data", e);
			}
		}
		return new ComponentMetaData(type, data);
	}
}
