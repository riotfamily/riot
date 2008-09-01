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
package org.riotfamily.common.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class ColorUtils {

	private static final Pattern RGB_PATTERN = Pattern.compile(
			"rgb\\(\\s?(\\d{1,3})%?\\s?,\\s?(\\d{1,3})%?\\s?," +
			"\\s?(\\d{1,3})(%?)\\s?\\)");

	private static HashMap<String, Color> namedColors = new HashMap<String, Color>();

	static {
		namedColors.put("aliceblue", new Color(0xf0, 0xf8, 0xff));
		namedColors.put("antiquewhite", new Color(0xfa, 0xeb, 0xd7));
		namedColors.put("aqua", new Color(0x00, 0xff, 0xff));
		namedColors.put("aquamarine", new Color(0x7f, 0xff, 0xd4));
		namedColors.put("azure", new Color(0xf0, 0xff, 0xff));
		namedColors.put("beige", new Color(0xf5, 0xf5, 0xdc));
		namedColors.put("bisque", new Color(0xff, 0xe4, 0xc4));
		namedColors.put("black", new Color(0x00, 0x00, 0x00));
		namedColors.put("blanchedalmond", new Color(0xff, 0xeb, 0xcd));
		namedColors.put("blue", new Color(0x00, 0x00, 0xff));
		namedColors.put("blueviolet", new Color(0x8a, 0x2b, 0xe2));
		namedColors.put("brown", new Color(0xa5, 0x2a, 0x2a));
		namedColors.put("burlywood", new Color(0xde, 0xb8, 0x87));
		namedColors.put("cadetblue", new Color(0x5f, 0x9e, 0xa0));
		namedColors.put("chartreuse", new Color(0x7f, 0xff, 0x00));
		namedColors.put("chocolate", new Color(0xd2, 0x69, 0x1e));
		namedColors.put("coral", new Color(0xff, 0x7f, 0x50));
		namedColors.put("cornflowerblue", new Color(0x64, 0x95, 0xed));
		namedColors.put("cornsilk", new Color(0xff, 0xf8, 0xdc));
		namedColors.put("crimson", new Color(0xdc, 0x14, 0x3c));
		namedColors.put("cyan", new Color(0x00, 0xff, 0xff));
		namedColors.put("darkblue", new Color(0x00, 0x00, 0x8b));
		namedColors.put("darkcyan", new Color(0x00, 0x8b, 0x8b));
		namedColors.put("darkgoldenrod", new Color(0xb8, 0x86, 0x0b));
		namedColors.put("darkgray", new Color(0xa9, 0xa9, 0xa9));
		namedColors.put("darkgrey", new Color(0xa9, 0xa9, 0xa9));
		namedColors.put("darkgreen", new Color(0x00, 0x64, 0x00));
		namedColors.put("darkkhaki", new Color(0xbd, 0xb7, 0x6b));
		namedColors.put("darkmagenta", new Color(0x8b, 0x00, 0x8b));
		namedColors.put("darkolivegreen", new Color(0x55, 0x6b, 0x2f));
		namedColors.put("darkorange", new Color(0xff, 0x8c, 0x00));
		namedColors.put("darkorchid", new Color(0x99, 0x32, 0xcc));
		namedColors.put("darkred", new Color(0x8b, 0x00, 0x00));
		namedColors.put("darksalmon", new Color(0xe9, 0x96, 0x7a));
		namedColors.put("darkseagreen", new Color(0x8f, 0xbc, 0x8f));
		namedColors.put("darkslateblue", new Color(0x48, 0x3d, 0x8b));
		namedColors.put("darkslategray", new Color(0x2f, 0x4f, 0x4f));
		namedColors.put("darkslategrey", new Color(0x2f, 0x4f, 0x4f));
		namedColors.put("darkturquoise", new Color(0x00, 0xce, 0xd1));
		namedColors.put("darkviolet", new Color(0x94, 0x00, 0xd3));
		namedColors.put("deeppink", new Color(0xff, 0x14, 0x93));
		namedColors.put("deepskyblue", new Color(0x00, 0xbf, 0xff));
		namedColors.put("dimgray", new Color(0x69, 0x69, 0x69));
		namedColors.put("dimgrey", new Color(0x69, 0x69, 0x69));
		namedColors.put("dodgerblue", new Color(0x1e, 0x90, 0xff));
		namedColors.put("firebrick", new Color(0xb2, 0x22, 0x22));
		namedColors.put("floralwhite", new Color(0xff, 0xfa, 0xf0));
		namedColors.put("forestgreen", new Color(0x22, 0x8b, 0x22));
		namedColors.put("fuchsia", new Color(0xff, 0x00, 0xff));
		namedColors.put("gainsboro", new Color(0xdc, 0xdc, 0xdc));
		namedColors.put("ghostwhite", new Color(0xf8, 0xf8, 0xff));
		namedColors.put("gold", new Color(0xff, 0xd7, 0x00));
		namedColors.put("goldenrod", new Color(0xda, 0xa5, 0x20));
		namedColors.put("gray", new Color(0x80, 0x80, 0x80));
		namedColors.put("grey", new Color(0x80, 0x80, 0x80));
		namedColors.put("green", new Color(0x00, 0x80, 0x00));
		namedColors.put("greenyellow", new Color(0xad, 0xff, 0x2f));
		namedColors.put("honeydew", new Color(0xf0, 0xff, 0xf0));
		namedColors.put("hotpink", new Color(0xff, 0x69, 0xb4));
		namedColors.put("indianred", new Color(0xcd, 0x5c, 0x5c));
		namedColors.put("indigo", new Color(0x4b, 0x00, 0x82));
		namedColors.put("ivory", new Color(0xff, 0xff, 0xf0));
		namedColors.put("khaki", new Color(0xf0, 0xe6, 0x8c));
		namedColors.put("lavender", new Color(0xe6, 0xe6, 0xfa));
		namedColors.put("lavenderblush", new Color(0xff, 0xf0, 0xf5));
		namedColors.put("lawngreen", new Color(0x7c, 0xfc, 0x00));
		namedColors.put("lemonchiffon", new Color(0xff, 0xfa, 0xcd));
		namedColors.put("lightblue", new Color(0xad, 0xd8, 0xe6));
		namedColors.put("lightcoral", new Color(0xf0, 0x80, 0x80));
		namedColors.put("lightcyan", new Color(0xe0, 0xff, 0xff));
		namedColors.put("lightgoldenrodyellow", new Color(0xfa, 0xfa, 0xd2));
		namedColors.put("lightgray", new Color(0xd3, 0xd3, 0xd3));
		namedColors.put("lightgrey", new Color(0xd3, 0xd3, 0xd3));
		namedColors.put("lightgreen", new Color(0x90, 0xee, 0x90));
		namedColors.put("lightpink", new Color(0xff, 0xb6, 0xc1));
		namedColors.put("lightsalmon", new Color(0xff, 0xa0, 0x7a));
		namedColors.put("lightseagreen", new Color(0x20, 0xb2, 0xaa));
		namedColors.put("lightskyblue", new Color(0x87, 0xce, 0xfa));
		namedColors.put("lightslategray", new Color(0x77, 0x88, 0x99));
		namedColors.put("lightslategrey", new Color(0x77, 0x88, 0x99));
		namedColors.put("lightsteelblue", new Color(0xb0, 0xc4, 0xde));
		namedColors.put("lightyellow", new Color(0xff, 0xff, 0xe0));
		namedColors.put("lime", new Color(0x00, 0xff, 0x00));
		namedColors.put("limegreen", new Color(0x32, 0xcd, 0x32));
		namedColors.put("linen", new Color(0xfa, 0xf0, 0xe6));
		namedColors.put("magenta", new Color(0xff, 0x00, 0xff));
		namedColors.put("maroon", new Color(0x80, 0x00, 0x00));
		namedColors.put("mediumaquamarine", new Color(0x66, 0xcd, 0xaa));
		namedColors.put("mediumblue", new Color(0x00, 0x00, 0xcd));
		namedColors.put("mediumorchid", new Color(0xba, 0x55, 0xd3));
		namedColors.put("mediumpurple", new Color(0x93, 0x70, 0xd8));
		namedColors.put("mediumseagreen", new Color(0x3c, 0xb3, 0x71));
		namedColors.put("mediumslateblue", new Color(0x7b, 0x68, 0xee));
		namedColors.put("mediumspringgreen", new Color(0x00, 0xfa, 0x9a));
		namedColors.put("mediumturquoise", new Color(0x48, 0xd1, 0xcc));
		namedColors.put("mediumvioletred", new Color(0xc7, 0x15, 0x85));
		namedColors.put("midnightblue", new Color(0x19, 0x19, 0x70));
		namedColors.put("mintcream", new Color(0xf5, 0xff, 0xfa));
		namedColors.put("mistyrose", new Color(0xff, 0xe4, 0xe1));
		namedColors.put("moccasin", new Color(0xff, 0xe4, 0xb5));
		namedColors.put("navajowhite", new Color(0xff, 0xde, 0xad));
		namedColors.put("navy", new Color(0x00, 0x00, 0x80));
		namedColors.put("oldlace", new Color(0xfd, 0xf5, 0xe6));
		namedColors.put("olive", new Color(0x80, 0x80, 0x00));
		namedColors.put("olivedrab", new Color(0x6b, 0x8e, 0x23));
		namedColors.put("orange", new Color(0xff, 0xa5, 0x00));
		namedColors.put("orangered", new Color(0xff, 0x45, 0x00));
		namedColors.put("orchid", new Color(0xda, 0x70, 0xd6));
		namedColors.put("palegoldenrod", new Color(0xee, 0xe8, 0xaa));
		namedColors.put("palegreen", new Color(0x98, 0xfb, 0x98));
		namedColors.put("paleturquoise", new Color(0xaf, 0xee, 0xee));
		namedColors.put("palevioletred", new Color(0xd8, 0x70, 0x93));
		namedColors.put("papayawhip", new Color(0xff, 0xef, 0xd5));
		namedColors.put("peachpuff", new Color(0xff, 0xda, 0xb9));
		namedColors.put("peru", new Color(0xcd, 0x85, 0x3f));
		namedColors.put("pink", new Color(0xff, 0xc0, 0xcb));
		namedColors.put("plum", new Color(0xdd, 0xa0, 0xdd));
		namedColors.put("powderblue", new Color(0xb0, 0xe0, 0xe6));
		namedColors.put("purple", new Color(0x80, 0x00, 0x80));
		namedColors.put("red", new Color(0xff, 0x00, 0x00));
		namedColors.put("rosybrown", new Color(0xbc, 0x8f, 0x8f));
		namedColors.put("royalblue", new Color(0x41, 0x69, 0xe1));
		namedColors.put("saddlebrown", new Color(0x8b, 0x45, 0x13));
		namedColors.put("salmon", new Color(0xfa, 0x80, 0x72));
		namedColors.put("sandybrown", new Color(0xf4, 0xa4, 0x60));
		namedColors.put("seagreen", new Color(0x2e, 0x8b, 0x57));
		namedColors.put("seashell", new Color(0xff, 0xf5, 0xee));
		namedColors.put("sienna", new Color(0xa0, 0x52, 0x2d));
		namedColors.put("silver", new Color(0xc0, 0xc0, 0xc0));
		namedColors.put("skyblue", new Color(0x87, 0xce, 0xeb));
		namedColors.put("slateblue", new Color(0x6a, 0x5a, 0xcd));
		namedColors.put("slategray", new Color(0x70, 0x80, 0x90));
		namedColors.put("slategrey", new Color(0x70, 0x80, 0x90));
		namedColors.put("snow", new Color(0xff, 0xfa, 0xfa));
		namedColors.put("springgreen", new Color(0x00, 0xff, 0x7f));
		namedColors.put("steelblue", new Color(0x46, 0x82, 0xb4));
		namedColors.put("tan", new Color(0xd2, 0xb4, 0x8c));
		namedColors.put("teal", new Color(0x00, 0x80, 0x80));
		namedColors.put("thistle", new Color(0xd8, 0xbf, 0xd8));
		namedColors.put("tomato", new Color(0xff, 0x63, 0x47));
		namedColors.put("turquoise", new Color(0x40, 0xe0, 0xd0));
		namedColors.put("violet", new Color(0xee, 0x82, 0xee));
		namedColors.put("wheat", new Color(0xf5, 0xde, 0xb3));
		namedColors.put("white", new Color(0xff, 0xff, 0xff));
		namedColors.put("whitesmoke", new Color(0xf5, 0xf5, 0xf5));
		namedColors.put("yellow", new Color(0xff, 0xff, 0x00));
		namedColors.put("yellowgreen", new Color(0x9a, 0xcd, 0x32));
	}

	/**
	 * Creates a Color instance from a String. Supported formats are:
	 * <pre>
	 * #fff
	 * #ffffff
	 * rgb(255,155,155)
	 * rgb(100%,100%,100%)
	 * </pre>
	 * You may also use one of the color names listed at
	 * {@linkplain http://www.w3schools.com/css/css_colornames.asp}
	 */
	public static Color parseColor(String s) throws IllegalArgumentException {
		int r, g, b;
		if (s.startsWith("#")) {
			if (s.length() == 4) {
				r = Integer.parseInt(s.substring(1, 2) + s.charAt(1), 16);
				g = Integer.parseInt(s.substring(2, 3) + s.charAt(2), 16);
				b = Integer.parseInt(s.substring(3) + s.charAt(3), 16);
				return new Color(r, g, b);
			}
			if (s.length() == 7) {
				r = Integer.parseInt(s.substring(1, 3), 16);
				g = Integer.parseInt(s.substring(3, 5), 16);
				b = Integer.parseInt(s.substring(5), 16);
				return new Color(r, g, b);
			}
		}
		else {
			Matcher m = RGB_PATTERN.matcher(s);
			if (m.matches()) {
				r = Integer.parseInt(m.group(1));
				g = Integer.parseInt(m.group(2));
				b = Integer.parseInt(m.group(3));
				if ("%".equals(m.group(4))) {
					r = 255 * (r / 100);
					g = 255 * (g / 100);
					b = 255 * (b / 100);
				}
				return new Color(r, g, b);
			}
			else {
				Color color = (Color) namedColors.get(s.toLowerCase());
				if (color != null) {
					return color;
				}
			}
		}
		throw new IllegalArgumentException("Invalid color format: " + s);
	}
	
	/**
	 * Adjusts the brightness of the given color.
	 * @param c The color to adjust
	 * @param scale The factor (a value from -1 to 1) 
	 */
	public static Color brightness(Color c, float scale) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		if (r == 0 && g == 0 && b == 0 && scale > 0) {
			r = g = b = Math.min(255, (int) (255 * scale));
		}
		else {
			r = Math.max(0, (int) (r * scale));
			g = Math.max(0, (int) (g * scale));
			b = Math.max(0, (int) (b * scale));
			if (r > 255 || g > 255 || b > 255) {
				return c;
			}
		}
		return new Color(r, g, b);
	}
	
	/**
	 * Adjusts the saturation of the given color.
	 * @param c The color to adjust
	 * @param scale The factor (a value from -1 to 1) 
	 */
	public static Color saturation(Color c, float scale) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		float[] hsb = Color.RGBtoHSB(r, g, b, null);
		float s = hsb[1] * scale;
		if (s < 0) { 
			s = 0; 
		};
		if (s > 1) { 
			s = 1; 
		};
		return Color.getHSBColor(hsb[0], s, hsb[2]);
	}

	public static String toHex(Color color) {
		String r = Integer.toHexString(color.getRed());
		String g = Integer.toHexString(color.getGreen());
		String b = Integer.toHexString(color.getBlue());
		if (r.length() == 1) {
		  r = "0" + r;
		}
		if (g.length() == 1) {
		  g = "0" + g;
		}
		if (b.length() == 1) {
		  b = "0" + b;
		}
		return ("#" + r + g + b).toUpperCase();
	}
}

	