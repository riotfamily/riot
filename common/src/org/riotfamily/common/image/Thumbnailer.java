package org.riotfamily.common.image;

import java.io.File;
import java.io.IOException;

/**
 * Interface to render thumbnail images.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface Thumbnailer {

	/**
	 * Renders a thumbnail image of the source file to the specified 
	 * destination.
	 */
	public void renderThumbnail(File source, File dest, int width, int height,
			boolean fixedSize, String backgroundColor) throws IOException;
	
}
