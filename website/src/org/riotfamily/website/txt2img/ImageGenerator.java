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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   flx
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.txt2img;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.imageio.ImageIO;

import org.riotfamily.common.collection.FlatMap;
import org.riotfamily.common.util.FormatUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 * @author flx
 * @since 6.5
 */
public class ImageGenerator implements InitializingBean {

	private Font font = Font.getFont("Serif");
	
	private float size = 22;
	
	private int paddingTop = 0;
	
	private Integer lineSpacing;
	
	private Color color = Color.BLACK;
	
	private FlatMap attributes = new FlatMap();
	
	/**
	 * Sets the font file to use. The resource must either point to a Type 1
	 * or TrueType font. The type is determined by the extension of the 
	 * resource's filename (the check is case insensitive). If the extension 
	 * is <i>.pfa</i> or <i>.pfb</i> {@link Font#TYPE1_FONT} is used, 
	 * otherwise {@link Font#TRUETYPE_FONT}.
	 */
	public void setFont(Resource res) throws FontFormatException, IOException {
		String ext = FormatUtils.getExtension(res.getFilename()).toLowerCase();
		int format = Font.TRUETYPE_FONT;
		if (ext.equals("pfa") || ext.equals("pfb")) {
			format = Font.TYPE1_FONT;
		}
		this.font = Font.createFont(format, res.getInputStream());
	}
	
	/**
	 * Sets the text foreground color. Examples of supported formats:
	 * <pre>
	 * #fff
	 * #fffff
	 * rgb(255,255,255)
	 * rgb(100%, 100%, 100%)
	 * </pre>
	 */
	public void setColor(String color) {
		this.color = FormatUtils.parseColor(color);
	}

	/**
	 * Sets the font size.
	 */
	public void setSize(float size) {
		this.size = size;
	}

	/**
	 * Sets the interline spacing in pixels. If not set, 
	 * {@link TextLayout#getLeading()} is used.
	 */
	public void setLineSpacing(Integer lineSpacing) {
		this.lineSpacing = lineSpacing;
	}
	
	/**
	 * Sets the padding at the top of the image in pixels. 
	 * The default value is <code>0</code>.
	 */
	public void setPaddingTop(int paddingTop) {
		this.paddingTop = paddingTop;
	}
	
	public void afterPropertiesSet() throws Exception {
		attributes.put(TextAttribute.FONT, font.deriveFont(size));
		attributes.put(TextAttribute.FOREGROUND, color);
	}
	
	public void generate(String text, int width, String color, OutputStream os) 
			throws IOException {
		
		int height = getHeight(text, width);
        BufferedImage image = createImage(width, height);
        drawText(text, width, color, image);
       	ImageIO.write(image, "png", os);
       	image.flush();
	}
	
	protected int getHeight(String text, float width) {
	    return layout(text, width, null, createImage(1, 1), false);
	}
	
	protected void drawText(String text, float width, String color, 
			BufferedImage image) {
		
	    layout(text, width, color, image, true);
	}
	
	protected int layout(String text, float width, String color, 
			BufferedImage image, boolean draw) {
		
		FlatMap attrs = attributes;
		if (draw && color != null) {
			attrs = new FlatMap(attributes);
			attrs.put(TextAttribute.FOREGROUND, FormatUtils.parseColor(color));
		}
		AttributedString as = new AttributedString(text, attrs);
		Graphics2D graphics = createGraphics(image);
        FontRenderContext fc = graphics.getFontRenderContext();
        AttributedCharacterIterator it = as.getIterator();
	    LineBreakMeasurer measurer = new LineBreakMeasurer(it, fc);
	    int y = paddingTop;
	    while (measurer.getPosition() < it.getEndIndex()) {
	    	TextLayout layout;
	    	int nextBreak = text.indexOf('\n', measurer.getPosition() + 1); 
	    	if (nextBreak != -1) {
	    		layout = measurer.nextLayout(width, nextBreak, false);
	    	}
	    	else {
	    		layout = measurer.nextLayout(width);
	    	}
			y += layout.getAscent();
			if (draw) {
				layout.draw(graphics, 0, y);
			}
			y += layout.getDescent();
			if (measurer.getPosition() < it.getEndIndex()) {
				y += lineSpacing != null ? lineSpacing.intValue() : layout.getLeading();
			}
	    }
	    return y;
	}
	
	protected Graphics2D createGraphics(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
        		RenderingHints.VALUE_ANTIALIAS_ON);
        
        return graphics;
	}
	
	protected BufferedImage createImage(int width, int height) {
		return new BufferedImage(width, height,	BufferedImage.TYPE_INT_ARGB);
	}
	
}
