package org.riotfamily.riot.list.ui.render;

import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThumbnailRenderer extends ObjectRenderer {

	Log log = LogFactory.getLog(ThumbnailRenderer.class);
	
	private String thumbnailControllerPath;
	
	public void setThumbnailControllerPath(String thumbnailControllerPath) {
		this.thumbnailControllerPath = thumbnailControllerPath;
	}
	
	protected void renderValue(RenderContext context, PrintWriter writer, 
			String value) {		
		writer.print("<img src=\"");
		writer.print(context.getContextPath());
		writer.print(context.getRequest().getServletPath());
		writer.print(thumbnailControllerPath);
		writer.print("?sourceFile=");
		writer.print(value);
		writer.print("\" />"); 
		
	}
}
