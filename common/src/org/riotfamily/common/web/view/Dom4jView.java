package org.riotfamily.common.web.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.io.XMLWriter;
import org.springframework.web.servlet.view.AbstractView;

public class Dom4jView extends AbstractView {

	private Document document;

	public Dom4jView(Document document) {
		setContentType("text/xml");
		this.document = document;
	}
	
	protected void renderMergedOutputModel(Map model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		XMLWriter xmlWriter = new XMLWriter(response.getWriter());
		xmlWriter.write(document);
	}

}
