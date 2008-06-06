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
package org.riotfamily.common.image;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 *
 */
public class ImageMagickCropper implements ImageCropper {

	private ImageMagick imageMagick;
	
	public ImageMagickCropper(ImageMagick imageMagick) {
		this.imageMagick = imageMagick;
	}

	public void cropImage(File source, File dest, int width, int height,
			int x, int y, int scaledWidth) throws IOException {
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(source.getAbsolutePath());
		args.add("-resize");
		args.add(scaledWidth + "x>");
		args.add("-crop");
		args.add(width + "x" + height + "+" + x + "+" + y);
		if (imageMagick.getMajorVersion() >= 6) {
			args.add("+repage");
		}
		else {
			args.add("-page");
			args.add("+0+0");
		}
		args.add("-quality");
		args.add("100");
		
		args.add(dest.getAbsolutePath());
		imageMagick.invoke(args);
	}
	
}
