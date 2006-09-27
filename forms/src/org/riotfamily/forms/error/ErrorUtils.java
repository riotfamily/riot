package org.riotfamily.forms.error;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.forms.bind.Editor;

public class ErrorUtils {

	public static final String ERROR_REQUIRED = "required";
	
	private static Log log = LogFactory.getLog(ErrorUtils.class);
	
	public static void reject(Editor editor, String errorCode, Object[] args) {
		log.debug("Rejecting value " + editor.getFieldName() + ": " + errorCode);
		editor.getForm().getErrors().rejectValue(
				editor.getFieldName(), errorCode, args, null);
	}
	
	public static void reject(Editor editor, String errorCode, Object arg) {
		reject(editor, errorCode, new Object[] {arg});
	}
	
	
	public static void reject(Editor editor, String errorCode) {
		log.debug("Rejecting value " + editor.getFieldName() + ": " + errorCode);
		editor.getForm().getErrors().rejectValue(
				editor.getFieldName(), errorCode);
	}
	
	public static void rejectRequired(Editor editor) {
		reject(editor, ERROR_REQUIRED);
	}
	
	public static void removeErrors(Editor editor) {
		editor.getForm().getErrors().removeErrors(editor);
	}

}
