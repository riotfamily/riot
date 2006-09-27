package org.riotfamily.forms.error;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.bind.Editor;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;

public class FormErrors extends AbstractBindingResultSupport {	
	
	private static final String GENERAL_FORM_ERROR_MESSAGE_KEY = "error.form.hasErrors";
	
	private static final String GENERAL_FORM_ERROR_DEFAULT_MESSAGE = "Form has an error";
	
	private Form form;
	
	private String generalFormErrorMessageKey = GENERAL_FORM_ERROR_MESSAGE_KEY;
	
	private String generalFormErrorDefaultMessage = GENERAL_FORM_ERROR_DEFAULT_MESSAGE;

	public FormErrors(Form form) {
		super(form.getId());		
		this.form = form;
		setMessageCodesResolver(form.getFormContext().getMessageResolver()
				.getMessageCodesResolver());		
	}	
	
	public void setGeneralFormErrorDefaultMessage(
			String generalFormErrorDefaultMessage) {
		this.generalFormErrorDefaultMessage = generalFormErrorDefaultMessage;
	}
	
	public void setGeneralFormErrorMessageKey(String generalFormErrorMessageKey) {
		this.generalFormErrorMessageKey = generalFormErrorMessageKey;
	}

	public Object getTarget() {
		return form.getBackingObject();
	}	
	
	public void renderErrors(Element element) {		
		List errors = getErrors(element);
		if (errors != null) {
			PrintWriter writer = element.getForm().getFormContext().getWriter();
			DocumentWriter tag = new DocumentWriter(writer);
			tag.start(Html.UL)
					.attribute(Html.COMMON_ID, element.getId() + "-error")
					.attribute(Html.COMMON_CLASS, "errors");		
			Iterator it = errors.iterator();
			while (it.hasNext()) {
				tag.start(Html.LI)
						.body((String) it.next())
						.end();
			}			
			tag.end();	
		}
	}	
	
	public List getErrors(Element element) {
		if (element instanceof Editor) {
			ArrayList messages = new ArrayList();
			Editor editor = (Editor) element;
			List fieldErrors = getFieldErrors(editor.getFieldName());
			for (Iterator it = fieldErrors.iterator(); it.hasNext();) {
				FieldError error = (FieldError) it.next();
				String message = form.getFormContext().getMessageResolver().getMessage(error);
				if (!StringUtils.hasLength(message)) {
					message = StringUtils.arrayToCommaDelimitedString(error.getCodes());
				}
				messages.add(message);
			}
			return messages;
		}
		return null;
	}
	
	public void removeErrors(Element element) {
		if (element instanceof Editor) {			
			Editor editor = (Editor) element;
			removeErrors(getFieldErrors(editor.getFieldName()));			
		}		
	}
	
	public String getGeneralFormError() {
		return getGeneralFormError(null);
	}
	
	public String getGeneralFormError(Object[] args) {
		return form.getFormContext().getMessageResolver().getMessage(
					generalFormErrorMessageKey, args, 
					generalFormErrorDefaultMessage);
	}
	
	public boolean hasErrors(Element element) {
		if (!(element instanceof Editor)) {
			return false;
		}
		Editor editor = (Editor) element;
		return hasFieldErrors(editor.getFieldName());
	}
	
	public PropertyEditorRegistry getPropertyEditorRegistry() {
		return form.getEditorBinder();
	}
	
}
