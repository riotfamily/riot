/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.view;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
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
	
	private Object jsonModel;
	
	private boolean useMergedOutputModel = true;
	
	public JsonView() {
		setContentType(DEFAULT_CONTENT_TYPE);
	}
	
	public JsonView(Object obj) {
		this();
		jsonModel = obj;
		useMergedOutputModel = false;
	}
	
	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}
	
	public JsonView sendAsHeader() {
		sendAsHeader = true;
		return this;
	}

	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		response.setContentType(getContentType());
		response.setCharacterEncoding(characterEncoding);
		
		if (useMergedOutputModel) {
			jsonModel = model;
		}
		String jsonString = null;
		if (jsonModel instanceof Collection<?> || jsonModel.getClass().isArray()) {
			jsonString = JSONArray.fromObject(jsonModel).toString();
		}
		else {
			jsonString = JSONObject.fromObject(jsonModel).toString();
		}
		if (sendAsHeader) {
			response.setHeader(headerName, jsonString);
		}
		else {
			PrintWriter out = response.getWriter();
			out.write('(');
			out.write(jsonString);
			out.write(')');
		}
	}
}