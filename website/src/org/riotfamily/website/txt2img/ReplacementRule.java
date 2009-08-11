package org.riotfamily.website.txt2img;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import org.riotfamily.common.image.ImageUtils;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ReplacementRule extends ListItemRenderer {

	private String selector;
	
	public String getSelector() {
		return this.selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public void generate(String text, Locale locale, int maxWidth, String color, boolean hover, 
			OutputStream os) throws IOException {
		
		BufferedImage image = generate(text, locale, maxWidth, color, hover);
		ImageUtils.write(image, "png", os);
		image.flush();
	}

}
