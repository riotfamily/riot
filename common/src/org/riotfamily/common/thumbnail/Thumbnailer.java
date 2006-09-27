package org.riotfamily.common.thumbnail;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface Thumbnailer {

	public boolean supports(String mimeType);
	
	public void renderThumbnail(File source, String mimeType, OutputStream out)
			throws IOException;
	
}
