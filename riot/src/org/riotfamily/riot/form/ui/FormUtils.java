package org.riotfamily.riot.form.ui;

import org.riotfamily.forms.Form;

public class FormUtils {

	private static final String OBJECT_ID_ATTRIBUTE = "objectId";
	
	private static final String PARENT_ID_ATTRIBUTE = "parentId";
	
	public static void setObjectId(Form form, String objectId) {
		form.setAttribute(OBJECT_ID_ATTRIBUTE, objectId);
	}
	
	public static String getObjectId(Form form) {
		return (String) form.getAttribute(OBJECT_ID_ATTRIBUTE);
	}
	
	public static void setParentId(Form form, String objectId) {
		form.setAttribute(PARENT_ID_ATTRIBUTE, objectId);
	}
	
	public static String getParentId(Form form) {
		return (String) form.getAttribute(PARENT_ID_ATTRIBUTE);
	}
	
}
