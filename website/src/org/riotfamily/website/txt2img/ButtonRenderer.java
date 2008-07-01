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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Class to render button graphics. 
 */
public class ButtonRenderer extends ListItemRenderer implements BeanNameAware {

	private File bgFile;
	
	private BufferedImage bg;
		
	private String hoverColor;
	
	private String id;
	
	private int buttonHeight;
	
	private final long creationTime = System.currentTimeMillis();
	
	public void setBg(Resource res) throws IOException {
		bgFile = res.getFile();
		bg = ImageIO.read(bgFile);
	}
	
	public void setHoverColor(String hoverColor) {
		this.hoverColor = hoverColor;
	}
	
	public void setBeanName(String name) {
		this.id = name;
	}
	
	public String getId() {
		return id;
	}
	
	@Override
	public void afterPropertiesSet() {
		Assert.notNull(bg, "The background image must be specified");
		buttonHeight = bg.getHeight();
		if (hoverColor != null) {
			buttonHeight /= 2;
		}
		super.afterPropertiesSet();
	}
	
	public long getLastModified() {
		return Math.max(creationTime, bgFile.lastModified());
	}
		
	public BufferedImage generate(String label) throws Exception {
		
		BufferedImage labelImage = generate(label, Integer.MAX_VALUE, null, false);
		int textImageWidth = labelImage.getWidth();
		
		int width = textImageWidth + bg.getWidth();
		
		BufferedImage image = new BufferedImage(width, bg.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) image.getGraphics();

		int c = bg.getWidth() / 2;
		int y2 = bg.getHeight(); 
		g.drawImage(bg, 0, 0, c, y2, 0, 0, c, y2, null);
		g.drawImage(bg, c, 0, width-c, y2, c+1, 0, c, y2, null);
		g.drawImage(bg, width-c, 0, width, y2, c, 0, bg.getWidth(), y2, null);

		int top = buttonHeight / 2 - labelImage.getHeight() / 2;
		g.drawImage(labelImage, c, top, null);
		if (hoverColor != null) {
			BufferedImage hoverLabelImage = generate(label, Integer.MAX_VALUE, hoverColor, true);
			g.drawImage(hoverLabelImage, c, top + buttonHeight, null);
		}
		return image;
	}
	
	public String getRules() {
		StringBuilder sb = new StringBuilder();
		sb.append('.').append(id)
				.append("{border:none;background-repeat:none;text-indent:-999em;")
				.append("height:").append(buttonHeight).append("px;}\n")
				.append('.').append(id).append(":hover{")
				.append("background-position:0 -").append(buttonHeight)
				.append("px}\n");
		
		return sb.toString();
	}
	
}
