package org.riotfamily.common.image;

import java.io.File;
import java.io.IOException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface ImageCropper {

	public void cropImage(File source, File dest, int width, int height,
			int x, int y, int scaledWidth) throws IOException;
	
}
