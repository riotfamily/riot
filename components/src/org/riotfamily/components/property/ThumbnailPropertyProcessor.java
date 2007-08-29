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
package org.riotfamily.components.property;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.image.Thumbnailer;
import org.riotfamily.common.web.file.FileStore;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class ThumbnailPropertyProcessor extends PropertyProcessorAdapter {

	private static final Log log = LogFactory.getLog(ThumbnailPropertyProcessor.class);
	
	private static final String MTIME_SUFFIX = "-mtime";
	
	private String source;
	
	private String dest;
	
	private int width;
	
	private int height;
	
	private String format = "jpg";
	
	private FileStore fileStore;
	
	private Thumbnailer thumbnailer;
	
	private File tempDir;
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public void setDest(String dest) {
		this.dest = dest;
	}

	public void setFileStore(FileStore fileStore) {
		this.fileStore = fileStore;
	}

	public void setThumbnailer(Thumbnailer thumbnailer) {
		this.thumbnailer = thumbnailer;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}

	public void convertToStrings(Map map) {
		try {
			String sourceUri = (String) map.get(source);
			File sourceFile = fileStore.retrieve(sourceUri);
			
			String mtime = (String) map.get(dest + MTIME_SUFFIX);
			if (mtime == null || Long.parseLong(mtime) < sourceFile.lastModified()) {
				File tempFile = File.createTempFile("thumb", "." + format, tempDir);
				thumbnailer.renderThumbnail(sourceFile, tempFile, width, height);
				String destUri = (String) map.get(dest);
				if (destUri != null) {
					fileStore.delete(destUri);
				}
				destUri = fileStore.store(tempFile, null);
				map.put(dest, destUri);
				map.put(dest + MTIME_SUFFIX, String.valueOf(System.currentTimeMillis()));
			}
		}
		catch (IOException e) {
			log.error(e);
		}
	}
	
}
