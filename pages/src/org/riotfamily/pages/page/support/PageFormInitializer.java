package org.riotfamily.pages.page.support;

import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.bind.Editor;
import org.riotfamily.forms.element.ContainerElement;
import org.riotfamily.pages.page.Page;

/**
 * FormInitializer that removes the pathComponent field if the edited page 
 * is flaged as system page.
 */
public class PageFormInitializer implements FormInitializer {

	public void initForm(Form form) {
		Page page = (Page) form.getBackingObject();
		if (page != null && page.isSystemPage()) {
			Editor e = form.getEditorBinder().getEditor("pathComponent");
			if (e != null) {
				ContainerElement container = (ContainerElement) e.getParent();
				container.removeElement(e);
			}
		}
	}

}
