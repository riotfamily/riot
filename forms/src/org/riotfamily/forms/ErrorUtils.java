/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ErrorUtils {

	public static final String ERROR_REQUIRED = "required";

	private static Log log = LogFactory.getLog(ErrorUtils.class);

	public static void reject(Editor editor, String errorCode, Object[] args) {
		log.debug("Rejecting value " + editor.getFieldName() + ": " + errorCode);
		editor.getForm().getErrors().rejectValue(
				editor.getFieldName(), errorCode, args, null);
		
		notifyListener(editor);
	}

	public static void reject(Editor editor, String errorCode, Object arg) {
		reject(editor, errorCode, new Object[] {arg});
	}


	public static void reject(Editor editor, String errorCode) {
		log.debug("Rejecting value " + editor.getFieldName() + ": " + errorCode);
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
