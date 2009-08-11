package org.riotfamily.website.css;

import java.awt.Color;

import org.riotfamily.common.util.ColorUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public final class ColorTool {

	/**
	 * Returns a shade of the given color with a brightness adjusted by the 
	 * specified percentage. The color may specified in any format supported
	 * by {@link ColorUtils#parseColor(String)}, including named colors.
	 */
	public String brightness(String color, int percentage) {
		Color c = ColorUtils.parseColor(color);
		c = ColorUtils.brightness(c, (float) percentage / 100);
		return ColorUtils.toHex(c);
	}
	
	/**
	 * Returns a shade of the given color with a saturation adjusted by the 
	 * specified percentage. The color may specified in any format supported
	 * by {@link ColorUtils#parseColor(String)}, including named colors.
	 */
	public String saturation(String color, int percentage) {
		Color c = ColorUtils.parseColor(color);
		c = ColorUtils.saturation(c, (float) percentage / 100);
		return ColorUtils.toHex(c);
	}
	
}
