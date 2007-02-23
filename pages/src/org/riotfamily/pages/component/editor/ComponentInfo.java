/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.component.editor;

import org.riotfamily.pages.component.ComponentRepository;
import org.riotfamily.pages.component.ComponentVersion;

/**
 * Value bean that is send to the JavaScript client via DWR. It provides  
 * information about a component including the rendered HTML code.
 */
public class ComponentInfo {

	private Long id;
	
	private String type;
	
	private String formId;
	
	private String html;

	private String onChangeScript;
	
	public ComponentInfo() {
	}

	public ComponentInfo(ComponentRepository repository, 
			ComponentVersion version, String html) {
		
		this.id = version.getContainer().getId();
		this.type = version.getType();
		this.formId = repository.getFormId(type);
		this.onChangeScript = repository.getComponent(type).getOnChangeScript();
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

	public String getOnChangeScript() {
		return this.onChangeScript;
	}

	public void setOnChangeScript(String onChangeScript) {
		this.onChangeScript = onChangeScript;
	}
	
}
