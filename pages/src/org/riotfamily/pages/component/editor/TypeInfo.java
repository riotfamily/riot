package org.riotfamily.pages.component.editor;


/**
 * Value bean that is send to the client JavaScript via DWR and provides
 * information about a component type.
 */
public class TypeInfo {

	private String description;
	
	private String type;
	
	public TypeInfo(String type, String description) {
		this.description = description;
		this.type = type;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String name) {
		this.description = name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
