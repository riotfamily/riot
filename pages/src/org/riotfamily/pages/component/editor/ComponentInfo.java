package org.riotfamily.pages.component.editor;

/**
 * Value bean that is send to the client JavaScript via DWR. It provides  
 * information about a component including the rendered HTML code.
 */
public class ComponentInfo {

	private Long id;
	
	private String type;
	
	private String formId;
	
	private String html;

	
	public ComponentInfo() {
	}

	public ComponentInfo(Long id, String type, String formId, String html) {
		this.id = id;
		this.type = type;
		this.formId = formId;
		this.html = html;
	}

	public String getHtml() {
		return this.html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFormId() {
		return this.formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}	

}
