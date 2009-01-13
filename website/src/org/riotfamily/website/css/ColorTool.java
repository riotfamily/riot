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
package org.riotfamily.website.css;

import java.awt.Color;

import org.riotfamily.common.util.ColorUtils;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ColorTool {

	/**
	 * Returns a shade of the given color with a brightness adjusted by the 
	 * specified percentage. The color may specified in any format supported
	 * by {@link ColorUtils#parseColor(String)}, including named colors.
	 */
	public String brightness(String color, int percentage) {
		Color c = ColorUtils.parseColor(color);
		Assert.notNull(c, "Could not parse the given color: " + color);
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
		Assert.notNull(c, "Could not parse the given color: " + color);
		c = ColorUtils.saturation(c, (float) percentage / 100);
		return ColorUtils.toHex(c);
	}
	
}
