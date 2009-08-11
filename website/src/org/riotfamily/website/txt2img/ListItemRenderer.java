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
