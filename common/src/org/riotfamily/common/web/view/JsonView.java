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
package org.riotfamily.common.web.view;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.web.servlet.view.AbstractView;

/**
 * A View that renders the model as JSON object.
 */
public class JsonView extends AbstractView {
	
	private static final String DEFAULT_CONTENT_TYPE = "application/json";
	
	private static final String JSON_HEADER = "X-JSON";

	private String characterEncoding = "UTF-8";

	private boolean sendAsHeader;
	
	private String headerName = JSON_HEADER;
	
	public JsonView() {
		this(false);
	}
	
	public JsonView(boolean sendAsHeader) {
		this.sendAsHeader = sendAsHeader;
		setContentType(DEFAULT_CONTENT_TYPE);
	}
	
	public void setSendAsHeader(boolean sendAsHeader) {
		this.sendAsHeader = sendAsHeader;
	}
	
	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	protected void renderMergedOutputModel(Map model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		response.setContentType(getContentType());
		response.setCharacterEncoding(characterEncoding);
		JSONObject jsonObject = JSONObject.fromObject(model);
		if (sendAsHeader) {
			response.setHeader(headerName, jsonObject.toString());
		}
		else {
			PrintWriter out = response.getWriter();
			out.write('(');
			out.write(jsonObject.toString());
			out.write(')');
		}
	}
}