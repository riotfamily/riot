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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.thumbnail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.riotfamily.common.util.CommandUtils;

/**
 * Thumbnailer that uses ImageMagick.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class ImageMagickThumbnailer implements Thumbnailer {

	private String convertCommand = "convert";
		
	private boolean crop;
	
	private String backgroundColor;
	
	private String format = "JPG";
	
	
	public void setConvertCommand(String convertCommand) {
		this.convertCommand = convertCommand;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
	public void setCrop(boolean crop) {
		this.crop = crop;
	}
	
	/**
	 * If a background color is specified, the image will be exactly resized to 
	 * <code>maxWidth x maxHeight</code>, unused space will be filled with the
	 * given color. If <code>crop</code> is enabled, the value is ignored.
	 * <p>
	 * Colors are represented in ImageMagick in the same form used by SVG:
	 * <pre>
  	 * name                 (color name like red, blue, white, etc.)
  	 * #RGB                 (R,G,B are hex numbers, 4 bits each)
  	 * #RRGGBB              (8 bits each)
  	 * #RRRGGGBBB           (12 bits each)
  	 * #RRRRGGGGBBBB        (16 bits each)
  	 * #RGBA                (4 bits each)
  	 * #RRGGBBOO            (8 bits each)
  	 * #RRRGGGBBBOOO        (12 bits each)
  	 * #RRRRGGGGBBBBOOOO    (16 bits each)
  	 * rgb(r,g,b)           0-255 for each of rgb
  	 * rgba(r,g,b,a)        0-255 for each of rgb and 0-1 for alpha
  	 * cmyk(c,m,y,k)        0-255 for each of cmyk
  	 * cmyka(c,m,y,k,a)     0-255 for each of cmyk and 0-1 for alpha
  	 * </pre>
  	 * </p>
  	 * For a transparent background specify an alpha value, 
  	 * like in <code>#ffff</code>.
	 */
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void renderThumbnail(File source, File dest, int width, int height)
			throws IOException {
		
		ArrayList cmd = new ArrayList();
		cmd.add(convertCommand);
		cmd.add(source.getAbsolutePath());
		cmd.add("-resize");
		if (crop) {
			cmd.add("x" + height * 2);
			cmd.add("-resize");
			cmd.add(width * 2 + "x<");
			cmd.add("-resize");
			cmd.add("50%");
			cmd.add("-crop");
			cmd.add(width + "x" + height + "+0+0");
			cmd.add("+repage");
		}
		else {
			cmd.add(width + "x" + height + ">");
			if (backgroundColor != null) {
				cmd.add("-size");
				cmd.add(width + "x" + height);
				cmd.add("xc:" + backgroundColor);
				cmd.add("+swap");
				cmd.add("-gravity");
				cmd.add("center");
				cmd.add("-composite");
			}
		}
		cmd.add("-colorspace");
		cmd.add("RGB");
		cmd.add(format.toUpperCase() + ':' + dest.getAbsolutePath());
		CommandUtils.exec(cmd);
	}

}
