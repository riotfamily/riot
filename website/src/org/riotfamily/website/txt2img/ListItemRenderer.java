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
package org.riotfamily.website.txt2img;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;

import org.riotfamily.common.image.ImageUtils;
import org.springframework.core.io.Resource;

/**
 * TextRenderer that supports bullet icons.
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class ListItemRenderer extends TextRenderer {

	private BufferedImage bulletIcon;
	
	private BufferedImage hoverBulletIcon;
	
	private int bulletTop = 0;
	
	private int bulletLeft = 0;
	
	private int paddingLeft = 0;
	
	public void setBulletIcon(Resource res) throws IOException {
		this.bulletIcon = ImageUtils.read(res);
	}
	
	public void setHoverBulletIcon(Resource res) throws IOException {
		this.hoverBulletIcon = ImageUtils.read(res);
	}
	
	public void setBulletTop(int bulletTop) {
		this.bulletTop = bulletTop;
	}
	
	public void setBulletLeft(int bulletLeft) {
		this.bulletLeft = bulletLeft;
	}
	
	@Override
	public void setPaddingLeft(int paddingLeft) {
		this.paddingLeft = paddingLeft;
		super.setPaddingLeft(paddingLeft);
	}
	
	@Override
	public void afterPropertiesSet() {
		if (bulletIcon != null) {
			setPaddingLeft(paddingLeft + bulletIcon.getWidth());
		}
		else if (hoverBulletIcon != null) {
			setPaddingLeft(paddingLeft + hoverBulletIcon.getWidth());
		}
		super.afterPropertiesSet();
	}
	
	@Override
	public BufferedImage generate(String text, Locale locale, int maxWidth, String color) {
		return generate(text, locale, maxWidth, color, false);
	}
	
	public BufferedImage generate(String text, Locale locale, int maxWidth, String color, boolean hover) {
		BufferedImage image = super.generate(text, locale, maxWidth, color);
		if (bulletIcon != null && !hover) {
			image.getGraphics().drawImage(bulletIcon, bulletLeft, bulletTop, null);
		}
		if (hoverBulletIcon != null && hover) {
			image.getGraphics().drawImage(hoverBulletIcon, bulletLeft, bulletTop, null);
		}
		return image;
	}
}
