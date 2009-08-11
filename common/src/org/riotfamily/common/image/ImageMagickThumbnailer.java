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
package org.riotfamily.common.image;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Thumbnailer that uses ImageMagick.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class ImageMagickThumbnailer implements Thumbnailer {

	private ImageMagick imageMagick;
			
	public ImageMagickThumbnailer(ImageMagick imageMagick) {
		this.imageMagick = imageMagick;
	}

	public void renderThumbnail(File source, File dest, int width, int height,
			boolean fixedSize, String backgroundColor)
			throws IOException {
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(source.getAbsolutePath());
		args.add("-resize");
		if (fixedSize) {
			if (backgroundColor != null) {
				// Add padding ...
				args.add(width + "x" + height + ">");
				args.add("-size");
				args.add(width + "x" + height);
				args.add("xc:" + backgroundColor);
				args.add("+swap");
				args.add("-gravity");
				args.add("center");
				args.add("-composite");
			}
			else {
				// Crop from center ...
				args.add("x" + height * 2);
				args.add("-resize");
				args.add(width * 2 + "x<");
				args.add("-resize");
				args.add("50%");
				args.add("-gravity");
				args.add("center");
				args.add("-crop");
				args.add(width + "x" + height + "+0+0");
				args.add("+repage");
			}
			
		}
		else {
			args.add(width + "x" + height + ">");
		}
		
		args.add("-colorspace");
		args.add("RGB");
		args.add(dest.getAbsolutePath());
		imageMagick.invoke(args);
	}

}
