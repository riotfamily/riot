package org.riotfamily.forms.controller;

import org.riotfamily.forms.Form;
import org.riotfamily.forms.element.core.Button;
import org.riotfamily.forms.event.ClickEvent;
import org.riotfamily.forms.event.ClickListener;
import org.springframework.web.servlet.mvc.Controller;

public class ButtonFactory {

	private String labelKey;
	
	private String label;
	
	private String cssClass;
	
	private FormSubmissionHandler formSubmissionHandler;
		
	
	public ButtonFactory(FormSubmissionHandler formSubmissionHandler) {
		this.formSubmissionHandler = formSubmissionHandler;
	}

	public ButtonFactory(Controller controller, String handlerMethodName) {
		this.formSubmissionHandler = new NamedMethodHandler(
				controller,	handlerMethodName);
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public Button createButton() {
		Button button = new Button();
		button.setSubmit(true);
		button.setTabIndex(1);
		if (label != null) {
			button.setLabel(label);
		}
		else {
			button.setLabelKey(labelKey);
		}
		if (cssClass != null) {
			button.setCssClass(cssClass);
		}
		button.addClickListener(new ClickListener() {
			public void clicked(ClickEvent event) {
				Form form = event.getSource().getForm();
				form.setAttribute(AbstractFormController.FORM_SUBMISSION_HANDLER, 
						formSubmissionHandler);
			}
		});
		
		return button;
	}

}
