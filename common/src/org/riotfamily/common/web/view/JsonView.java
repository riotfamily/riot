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
	
	private static final String DEFAULT_CONTENT_TYPE = 
			"text/plain; charset=UTF-8";
	
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