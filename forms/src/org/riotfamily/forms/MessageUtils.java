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




public class MessageUtils {
		
	public static String getMessage(Element element, String key) {
		return getMessage(element, key, null, key);
	}
	
	public static String getMessage(Element element, String key, 
			Object[] args, String defaultMessage) {
		
		FormContext context = element.getForm().getFormContext();
		return context.getMessageResolver().getMessage(
				key, args, defaultMessage);
	}
	
	public static String getLabel(Element element, EditorBinding binding) {
		FormContext context = element.getForm().getFormContext();
		return context.getMessageResolver().getPropertyLabel(
				element.getForm().getId(), binding.getBeanClass(), 
				binding.getProperty());
	}
	
	public static String getHint(Element element, EditorBinding binding) {
		FormContext context = element.getForm().getFormContext();
		return context.getMessageResolver().getPropertyHint(
				element.getForm().getId(), binding.getBeanClass(), 
				binding.getProperty());
	}
	
	public static String getHint(Form form, Class<?> beanClass) {
		FormContext context = form.getFormContext();
		return context.getMessageResolver().getPropertyHint(
				form.getId(), beanClass, null);
	}
}
