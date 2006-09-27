package org.riotfamily.riot.list.ui.render;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import java.io.PrintWriter;

import org.springframework.web.util.HtmlUtils;

/**
 *
 */
public class ObjectRenderer implements CellRenderer {

	private static PropertyEditor DEFAULT_EDITOR = new StringPropertyEditor();
	
	private PropertyEditor propertyEditor;
	
	/**
	 * @param propertyEditor The propertyEditor to use.
	 */
	public void setPropertyEditor(PropertyEditor propertyEditor) {
		this.propertyEditor = propertyEditor;
	}
	
	public void render(RenderContext context, PrintWriter writer) {
		Object object = context.getValue();
		if (object != null) {
			Class type = object.getClass();
			if (propertyEditor == null) {
				propertyEditor = PropertyEditorManager.findEditor(type);
				if (propertyEditor == null) {
					propertyEditor = DEFAULT_EDITOR;
				}
			}
			propertyEditor.setValue(object);
			renderValue(context, writer, propertyEditor.getAsText());
		}
	}

	protected void renderValue(RenderContext context, PrintWriter writer, 
			String value) {
		
		writer.print(HtmlUtils.htmlEscape(value));
	}
	
	private static class StringPropertyEditor extends PropertyEditorSupport {
	}

}
