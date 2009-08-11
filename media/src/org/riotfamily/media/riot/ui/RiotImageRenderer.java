package org.riotfamily.media.riot.ui;

import java.io.PrintWriter;

import org.riotfamily.common.ui.ObjectRenderer;
import org.riotfamily.common.ui.RenderContext;
import org.riotfamily.media.model.RiotImage;

public class RiotImageRenderer implements ObjectRenderer {

	public void render(Object obj, RenderContext context, PrintWriter writer) {
		RiotImage image = (RiotImage) obj;
		if (image != null) {
			writer.format("<img src=\"%s\" width=\"%s\" height=\"%s\" />",
					context.getContextPath() + image.getUri(), 
					image.getWidth(), image.getHeight());
		}
	}

}
