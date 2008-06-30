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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.txt2img;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.riotfamily.common.image.ImageUtils;
import org.springframework.core.io.Resource;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ListItemImageGenerator extends ImageGenerator {

	private BufferedImage bulletIcon;
	
	private BufferedImage hoverBulletIcon;
	
	private int bulletTop = 0;
	
	public void setBulletIcon(Resource res) throws IOException {
		this.bulletIcon = ImageUtils.read(res);
	}
	
	public void setHoverBulletIcon(Resource res) throws IOException {
		this.hoverBulletIcon = ImageUtils.read(res);
	}
	
	public void setBulletTop(int bulletTop) {
		this.bulletTop = bulletTop;
	}
	
	@Override
	public void afterPropertiesSet() {
		if (bulletIcon != null) {
			setPaddingLeft(getPaddingLeft() + bulletIcon.getWidth());
		}
		else if (hoverBulletIcon != null) {
			setPaddingLeft(getPaddingLeft() + hoverBulletIcon.getWidth());
		}
		super.afterPropertiesSet();
	}
	
	public BufferedImage generate(String text, int maxWidth, String color, 
			boolean hover) throws IOException {
	
		BufferedImage image = generate(text, maxWidth, color);
		if (bulletIcon != null && !hover) {
			image.getGraphics().drawImage(bulletIcon, 0, bulletTop, null);
		}
		if (hoverBulletIcon != null && hover) {
			image.getGraphics().drawImage(hoverBulletIcon, 0, bulletTop, null);
		}
		return image;
	}
}
