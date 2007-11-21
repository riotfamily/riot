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
package org.riotfamily.components.listener;

import java.io.File;
import java.io.IOException;

import org.riotfamily.common.image.Thumbnailer;
import org.riotfamily.components.service.UpdateListener;
import org.riotfamily.components.service.ComponentUpdate;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ThumbnailCreator implements UpdateListener {

	private String source;
	
	private String property;
	
	private int width;
	
	private int height;
	
	private String format = "jpg";
	
	private String fileStoreId;
	
	private Thumbnailer thumbnailer;
	
	private File tempDir;
	
	public void setProperty(String property) {
		this.property = property;
	}
	
	public void setSource(String source) {
		this.source = source;
	}

	public void setFileStoreId(String fileStoreId) {
		this.fileStoreId = fileStoreId;
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

	public void onUpdate(ComponentUpdate model) throws IOException {
		File imageFile = model.getFile(source);
		File thumbFile = model.getFile(property);
		if (imageFile != null) {
			if (thumbFile == null || thumbFile.lastModified() < imageFile.lastModified()) {
				File tempFile = File.createTempFile("thumb", "." + format, tempDir);
				thumbnailer.renderThumbnail(imageFile, tempFile, width, height);
				model.setFile(property, tempFile, fileStoreId);
			}
		}
		else {
			model.setFile(property, null, fileStoreId);
		}
	}
	
}
