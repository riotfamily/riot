package org.riotfamily.forms.element.dom;

import org.riotfamily.forms.bind.EditorBinder;
import org.riotfamily.forms.bind.XmlElementWrapper;
import org.riotfamily.forms.element.core.NestedForm;

public class XmlElement extends NestedForm {

	private String name;
	
	public void setName(String name) {
		this.name = name;
		setEditorBinder(new EditorBinder(new XmlElementWrapper(name)));
	}
	
	public void setBeanClass(Class beanClass) {
	}

	protected void afterBindingSet() {
		if (name == null) {
			setName(getEditorBinding().getProperty());
		}
		super.afterBindingSet();
	}
	
}
