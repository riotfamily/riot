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
		if (imageMagick.supportsVersion(6, 0)) {
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
