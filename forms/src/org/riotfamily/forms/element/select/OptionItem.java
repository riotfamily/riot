package org.riotfamily.forms.element.select;

/**
 *
 */
public class OptionItem {

	private Object object;
	
	private Object value;

	private String label;
	
	private String styleClass;

	private SelectElement parent;
	
	public OptionItem(Object object, Object value, String label, String styleClass, SelectElement parent) {
		this.object = object;
		this.value = value;
		this.label = label;
		this.styleClass = styleClass;
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

	public Object getValue() {
		return value;
	}
	
	public Object getObject() {
		return this.object;
	}
	
	public String getId() {
		return parent.getId() + '-' + getIndex();
	}
	
	public String getStyleClass() {
		return styleClass;
	}

	public void render() {
		parent.renderOption(this);
	}

}