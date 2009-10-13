/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.ui;

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
			renderString(convertToString(obj, context), context, writer);
		}
	}
	
	protected String convertToString(Object obj, RenderContext context) {
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
