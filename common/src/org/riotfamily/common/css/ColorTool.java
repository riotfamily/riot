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
package org.riotfamily.common.css;

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
