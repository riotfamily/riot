package org.riotfamily.forms;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.riotfamily.common.ui.RenderContext;
import org.springframework.beans.PropertyEditorRegistrar;

public interface FormContext extends RenderContext {
		
	public Locale getLocale();
	
	public String getResourcePath();

	public TemplateRenderer getTemplateRenderer();

	public PrintWriter getWriter();

	public void setWriter(PrintWriter writer);
	
	public String getFormUrl();
	
	public String getContentUrl(ContentElement el);
	
	public String getUploadUrl(String uploadId);

	public Collection<PropertyEditorRegistrar> getPropertyEditorRegistrars();

	public List<OptionsModelAdapter> getOptionsModelAdapters();
	
}
