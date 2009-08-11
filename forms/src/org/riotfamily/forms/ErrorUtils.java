package org.riotfamily.forms;

import org.riotfamily.common.util.RiotLog;


public class ErrorUtils {

	public static final String ERROR_REQUIRED = "required";

	private ErrorUtils() {
	}
	
	private static RiotLog getLog() {
		return RiotLog.get(ErrorUtils.class);
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
