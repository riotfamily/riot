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
package org.riotfamily.forms;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

import org.riotfamily.forms.element.TemplateElement;


public class TemplateUtils {
	
	public static String getTemplatePath(Object object) {
		return getTemplatePath(object, null);
	}
	
	public static String getTemplatePath(Class<?> clazz) {
		return getTemplatePath(clazz, null);
	}
	
	public static String getTemplatePath(Object object, String suffix) {
		return getTemplatePath(object.getClass(), suffix);
	}
	
	public static String getTemplatePath(Class<?> clazz, String suffix) {
		StringBuffer sb = new StringBuffer();
		sb.append("classpath:/");
		sb.append(clazz.getName().replace('.', '/'));
		if (suffix != null) {
			sb.append(suffix);
		}
		sb.append(".ftl");
		return sb.toString();
	}
	
	public static void render(FormContext context, String template, 
			String modelKey, Object object, PrintWriter writer) {
		
		Map<String, Object> model = Collections.singletonMap(modelKey, object);
		context.getTemplateRenderer().render(template, model, writer);
	}
		
	public static String getInitScript(Element element) {
		return getInitScript(element, element.getClass());
	}
	
	public static String getInitScript(Element element, Class<?> baseClass) {
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
				
		FormContext context = element.getForm().getFormContext();
		String template = getTemplatePath(baseClass, "_init");
		Map<String, ?> model = Collections.singletonMap("element", element);
		context.getTemplateRenderer().render(template, model, writer);
		
		return sw.toString();
	}
	
	public static String getInitScript(TemplateElement element) {
		return getInitScript(element, element.getClass());
	}
	
	public static String getInitScript(TemplateElement element, Class<?> baseClass) {
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
				
		FormContext context = element.getForm().getFormContext();
		String template = getTemplatePath(baseClass, "_init");
		context.getTemplateRenderer().render(template, element.getRenderModel(), writer);
		
		return sw.toString();
	}

}
