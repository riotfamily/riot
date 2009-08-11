package org.riotfamily.core.screen.form;

import org.riotfamily.core.screen.Notification;
import org.riotfamily.forms.Form;

public class FormNotification extends Notification {

	private String resourcePath;
	
	public FormNotification(Form form) {
		super(form.getFormContext().getMessageResolver());
		this.resourcePath = form.getFormContext().getContextPath() 
				+ form.getFormContext().getResourcePath();
	}
	
	@Override
	public Notification setIcon(String icon) {
		super.setIcon(resourcePath + "style/images/icons/" + icon + ".png");
		return this;
	}
	
}
