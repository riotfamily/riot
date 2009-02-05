package org.riotfamily.common.web.ui;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import java.io.PrintWriter;

import org.springframework.web.util.HtmlUtils;

public class StringRenderer implements ObjectRenderer {

	private static PropertyEditor DEFAULT_EDITOR = new StringPropertyEditor();
	
	private PropertyEditor propertyEditor;
	
	/**
	 * @param propertyEditor The propertyEditor to use.
	 */
	public void setPropertyEditor(PropertyEditor propertyEditor) {
		this.propertyEditor = propertyEditor;
	}
	
	public void render(Object obj, RenderContext context, PrintWriter writer) {
		if (obj != null) {
			renderString(convertToString(obj), context, writer);
		}
	}
	
	protected String convertToString(Object obj) {
		if (obj instanceof String) {
			return (String) obj;
		}
		PropertyEditor editor = this.propertyEditor;
		if (editor == null) {
			Class<?> type = obj.getClass();
			editor = PropertyEditorManager.findEditor(type);
			if (editor == null) {
				editor = DEFAULT_EDITOR;
			}
		}
		synchronized (editor) {
			editor.setValue(obj);
			return editor.getAsText();
		}
	}

	protected void renderString(String string, RenderContext context, 
			PrintWriter writer) {
		
		writer.print(HtmlUtils.htmlEscape(string));
	}
	
	private static class StringPropertyEditor extends PropertyEditorSupport {
	}

}
