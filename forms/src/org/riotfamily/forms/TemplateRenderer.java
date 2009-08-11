package org.riotfamily.forms;

import java.io.PrintWriter;
import java.util.Map;

import freemarker.template.Configuration;

public class TemplateRenderer {

	private Configuration configuration;
	
	public TemplateRenderer(Configuration configuration) {
		this.configuration = configuration;
	}

	public void render(String templateName, Map<String, ?> model, PrintWriter writer) {
		try {
			configuration.getTemplate(templateName).process(model, writer);
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
