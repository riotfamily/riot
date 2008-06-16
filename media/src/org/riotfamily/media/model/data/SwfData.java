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
package org.riotfamily.media.model.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.riotfamily.common.util.FlashInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
@Entity
@DiscriminatorValue("swf")
public class SwfData extends FileData {

	private static final String CONTENT_TYPE = "application/x-shockwave-flash";
	
	private int width;
	
	private int height;
	
	private int version;
	
	public SwfData() {
	}
	
	public SwfData(File file) throws IOException {
		super(file);
	}

	public SwfData(MultipartFile multipartFile) throws IOException {
		super(multipartFile);
	}
	
	public SwfData(InputStream in, String fileName) throws IOException {
		super(in, fileName);
	}
	
	public SwfData(byte[] bytes, String fileName) throws IOException {
		super(bytes, fileName);
	}

	protected void inspect(File file) throws IOException {
		FlashInfo flashInfo = new FlashInfo(file);
		setContentType(CONTENT_TYPE);
		if (flashInfo.isValid()) {
			width = flashInfo.getWidth();
			height = flashInfo.getHeight();
			version = flashInfo.getVersion();
		}
	}

	@Transient
	public boolean isValid() {
		return version > 0;
	}
	
	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
}
