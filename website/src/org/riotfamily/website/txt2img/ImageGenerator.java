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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
import org.riotfamily.common.util.ColorUtils;
import org.riotfamily.common.util.FormatUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ImageGenerator implements InitializingBean {

	private Font font = Font.getFont("Serif");
	
	private float fontSize = 22;
	
	private int paddingTop = 0;
	
	private int paddingRight = 0;
	
	private int paddingBottom = 0;
	
	private int paddingLeft = 0;
	
	private int lineSpacing = 0;
	
	private Integer maxWidth;
	
	private boolean shrinkToFit;
	
	private Color color = Color.BLACK;
	
	private boolean antiAlias = true;
	
	private boolean resample = false;
	
	private int internalFontSize = 120;
	
	private int scale = 1;
	
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
		this.color = ColorUtils.parseColor(color);
	}
	
	/**
	 * Sets the font size.
	 */
	public void setSize(float size) {
		this.fontSize = size;
	}

	/**
	 * Turns anti-aliasing off or on (default).
	 */
	public void setAntiAlias(boolean antiAlias) {
		this.antiAlias = antiAlias;
	}
	
	/**
	 * Sets the interline spacing in pixels. If not set, 
	 * {@link TextLayout#getLeading()} is used.
	 */
	public void setLineSpacing(int lineSpacing) {
		this.lineSpacing = lineSpacing;
	}
	
	/**
	 * Sets the maximum image width.
	 */
	public void setMaxWidth(Integer maxWidth) {
		this.maxWidth = maxWidth;
	}
	
	/**
	 * Sets whether the font-size should be reduced if the text does not
	 * fit within the max-width.
	 */
	public void setShrinkToFit(boolean shrinkToFit) {
		this.shrinkToFit = shrinkToFit;
	}
	
	/**
	 * Sets the padding at the top of the image in pixels. 
	 * The default value is <code>0</code>.
	 */
	public void setPaddingTop(int paddingTop) {
		this.paddingTop = paddingTop;
	}
	
	/**
	 * Sets the padding at the right side of the image in pixels. 
	 * The default value is <code>0</code>.
	 */
	public void setPaddingRight(int paddingRight) {
		this.paddingRight = paddingRight;
	}
	
	/**
	 * Sets the padding at the bottom of the image in pixels. 
	 * The default value is <code>0</code>.
	 */
	public void setPaddingBottom(int paddingBottom) {
		this.paddingBottom = paddingBottom;
	}
	
	/**
	 * Sets the padding at the left side of the image in pixels. 
	 * The default value is <code>0</code>.
	 */
	public void setPaddingLeft(int paddingLeft) {
		this.paddingLeft = paddingLeft;
	}
	
	/**
	 * If set to true, the text will be rendered at a larger size internally
	 * and scaled down to the requested size.
	 */
	public void setResample(boolean resample) {
		this.resample = resample;
	}
	
	/**
	 * Sets the font size at which the text is rendered internally when 
	 * resampling is enabled.
	 */
	public void setInternalFontSize(int internalFontSize) {
		this.internalFontSize = internalFontSize;
	}
	
	public void afterPropertiesSet() {
		if (resample) {
			scale = Math.round(internalFontSize / fontSize);
			fontSize *= scale;
			paddingTop *= scale;
			paddingRight *= scale;
			paddingBottom *= scale;
			paddingLeft *= scale;
			lineSpacing *= scale;
			if (maxWidth != null) {
				maxWidth = new Integer(maxWidth.intValue() * scale);
			}
		}
	}
	
	public void generate(String text, int maxWidth, String color, OutputStream os) 
			throws IOException {
		
        BufferedImage image = generate(text, maxWidth, color) ;
       	ImageIO.write(image, "png", os);
       	image.flush();
	}
	
	public BufferedImage generate(String text, int maxWidth, String color) {
		if (this.maxWidth != null) {
			maxWidth = this.maxWidth.intValue();
		}
		else if (resample && maxWidth < Integer.MAX_VALUE) {
			maxWidth *= scale;
		}
		
		float fontSize = this.fontSize;
		Dimension size;
		
		if (shrinkToFit) {
			size = getSize(text, fontSize, Integer.MAX_VALUE);
			while (size.getWidth() > maxWidth) {
				double delta = fontSize - fontSize * (maxWidth / size.getWidth());
				fontSize -= Math.max(Math.round(delta), 1);
				size = getSize(text, fontSize, Integer.MAX_VALUE);
			}
			size.setSize(maxWidth, size.getHeight());
			maxWidth = Integer.MAX_VALUE;
		}
		else {
			size = getSize(text, fontSize, maxWidth);
		}
		
		BufferedImage image = createImage(size);
		drawText(text, maxWidth, color, fontSize, image);
		if (resample) {
			int w = (int) (size.getWidth() / scale);
			int h = (int) (size.getHeight() / scale);
			Image scaledImage = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
			image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics g = image.getGraphics();
			g.drawImage(scaledImage, 0, 0, w, h, null);
			g.dispose();
		}
		return image;
	}
	
	protected Dimension getSize(String text, float fontSize, float maxWidth) {
	    return layout(text, maxWidth, null, fontSize, 
	    		createImage(new Dimension(1, 1)), false);
	}
	
	protected void drawText(String text, float maxWidth, String color, 
			float fontSize, BufferedImage image) {
		
	    layout(text, maxWidth, color, fontSize, image, true);
	}
	
	protected Dimension layout(String text, float maxWidth, String color, 
			float fontSize, BufferedImage image, boolean draw) {
		
		FlatMap attrs = new FlatMap();
		attrs.put(TextAttribute.FOREGROUND, color != null 
				? ColorUtils.parseColor(color)
				: this.color);
		
		attrs.put(TextAttribute.FONT, font.deriveFont(fontSize));
		
		AttributedString as = new AttributedString(text, attrs);
		Graphics2D graphics = createGraphics(image);
        FontRenderContext fc = graphics.getFontRenderContext();
        AttributedCharacterIterator it = as.getIterator();
	    LineBreakMeasurer measurer = new LineBreakMeasurer(it, fc);
	    int y = paddingTop;
	    int maxX = 0;
	    while (measurer.getPosition() < it.getEndIndex()) {
	    	TextLayout layout;
	    	int nextBreak = text.indexOf('\n', measurer.getPosition() + 1); 
	    	if (nextBreak != -1) {
	    		layout = measurer.nextLayout(maxWidth, nextBreak, false);
	    	}
	    	else {
	    		layout = measurer.nextLayout(maxWidth);
	    	}
			y += layout.getAscent();
			if (draw) {
				layout.draw(graphics, paddingLeft, y);
			}
			y += layout.getDescent();
			maxX = Math.max(maxX, paddingLeft + (int) layout.getVisibleAdvance() + paddingRight);
			y += layout.getLeading();
			if (measurer.getPosition() < it.getEndIndex()) {
				y += lineSpacing;
			}
	    }
	    y += paddingBottom;
	    graphics.dispose();
	    return new Dimension(maxX, y);
	}
	
	protected Graphics2D createGraphics(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias 
        		? RenderingHints.VALUE_ANTIALIAS_ON
        		: RenderingHints.VALUE_ANTIALIAS_OFF);
        
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias 
        		? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        		: RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, antiAlias 
        		? RenderingHints.VALUE_FRACTIONALMETRICS_ON
        		: RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        
        return graphics;
	}
	
	protected BufferedImage createImage(Dimension size) {
		return new BufferedImage((int) size.getWidth(), (int) size.getHeight(),	
				BufferedImage.TYPE_INT_ARGB);
	}
	
}
