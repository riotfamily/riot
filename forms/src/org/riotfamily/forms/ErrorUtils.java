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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ErrorUtils {

	public static final String ERROR_REQUIRED = "required";

	private ErrorUtils() {
	}
	
	private static Logger getLog() {
		return LoggerFactory.getLogger(ErrorUtils.class);
	}
	
	public static void reject(Editor editor, String errorCode, Object[] args) {
		getLog().debug("Rejecting value " + editor.getFieldName() + ": " + errorCode);
		editor.getForm().getErrors().rejectValue(
				editor.getFieldName(), errorCode, args, null);
		
		notifyListener(editor);
	}

	public static void reject(Editor editor, String errorCode, Object arg) {
		reject(editor, errorCode, new Object[] {arg});
	}


	public static void reject(Editor editor, String errorCode) {
		getLog().debug("Rejecting value " + editor.getFieldName() + ": " + errorCode);
		editor.getForm().getErrors().rejectValue(
				editor.getFieldName(), errorCode);
		
		notifyListener(editor);
	}

	public static void rejectRequired(Editor editor) {
		reject(editor, ERROR_REQUIRED);
	}

	public static void removeErrors(Element element) {
		element.getForm().getErrors().removeErrors(element);
		notifyListener(element);
	}

	public static boolean hasErrors(Element element) {
		return element.getForm().getErrors().hasErrors(element);
	}
	
	private static void notifyListener(Element element) {
		FormListener listener = element.getForm().getFormListener();
		if (listener != null) {
			listener.elementValidated(element);
		}
	}

}
