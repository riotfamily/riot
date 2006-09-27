package org.riotfamily.forms.element.support.select;

import org.riotfamily.forms.element.SelectElement;


/**
 *
 */
public class Option {

	private Object object;

	private String label;

	private SelectElement parent;
	
	public Option(Object object, String label, SelectElement parent) {
		this.object = object;
		this.label = label;
		this.parent = parent;
	}

	public SelectElement getParent() {
		return parent;
	}
	
	public boolean isSelected() {
		return parent.isSelected(this);
	}

	public int getIndex() {
		return parent.getOptionIndex(this);
	}

	public String getLabel() {
		return label;
	}

	public Object getObject() {
		return object;
	}
	
	public String getId() {
		return parent.getId() + '-' + getIndex();
	}

	public void render() {
		parent.renderOption(this);
	}

}