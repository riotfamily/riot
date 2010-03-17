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
package org.riotfamily.media.processing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 *
 */
public class ImageMagickCropper implements ImageCropper {

	private ImageMagick imageMagick;
	
	private int quality = 92;
	
	public ImageMagickCropper(ImageMagick imageMagick) {
		this.imageMagick = imageMagick;
	}
	
	public void setQuality(int quality) {
		this.quality = quality;
	}

	public void cropImage(File source, File dest, int width, int height,
			int x, int y, int scaledWidth) throws IOException {
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(source.getAbsolutePath());
		args.add("-resize");
		args.add(scaledWidth + "x>");
		args.add("-crop");
		args.add(width + "x" + height + "+" + x + "+" + y);
		if (imageMagick.supportsVersion(6, 0)) {
			args.add("+repage");
		}
		else {
			args.add("-page");
			args.add("+0+0");
		}
		args.add("-strip");
		args.add("-quality");
		args.add(String.valueOf(quality));
		
		args.add(dest.getAbsolutePath());
		imageMagick.invoke(args);
	}
	
}
