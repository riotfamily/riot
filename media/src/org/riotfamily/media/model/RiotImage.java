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
package org.riotfamily.media.model;

import java.io.File;
import java.io.IOException;

import org.riotfamily.media.model.data.ImageData;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class RiotImage extends RiotFile {

	public RiotImage() {
		super();
	}

	public RiotImage(ImageData data) {
		super(data);
	}

	public RiotImage(File file) throws IOException {
		super(new ImageData(file));
	}
	
	public RiotImage(MultipartFile multipartFile) throws IOException {
		super(new ImageData(multipartFile));
	}
	
	public RiotImage(byte[] bytes, String fileName) throws IOException {
		super(new ImageData(bytes, fileName));
	}
	
	public RiotFile createCopy() {
		return new RiotImage(getImageData());
	}
	
	public ImageData getImageData() {
		return (ImageData) getFileData();
	}

	public int getWidth() {
		return getImageData().getWidth();
	}
	
	public int getHeight() {
		return getImageData().getHeight();
	}

	public String getFormat() {
		return getImageData().getFormat();
	}

}
