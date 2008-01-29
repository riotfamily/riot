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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import org.riotfamily.media.model.data.FileData;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class RiotFile {

	private Long id;
	
	private FileData fileData;
	
	public RiotFile() {
	}

	public RiotFile(FileData data) {
		this.fileData = data;
	}
	
	public RiotFile(File file) throws IOException {
		this(new FileData(file));
	}
	
	public RiotFile(MultipartFile multipartFile) throws IOException {
		this(new FileData(multipartFile));
	}
	
	public RiotFile createCopy() {
		return new RiotFile(fileData);
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FileData getFileData() {
		return fileData;
	}
	
	public void setFileData(FileData fileData) {
		this.fileData = fileData;
	}

	public String getUri() {
		return getFileData().getUri();
	}

	public InputStream getInputStream() throws FileNotFoundException {
		return getFileData().getInputStream();
	}
	
	public File getFile() {
		return getFileData().getFile();
	}
	
	public String getContentType() {
		return getFileData().getContentType();
	}

	public Date getCreationDate() {
		return getFileData().getCreationDate();
	}

	public String getFileName() {
		return getFileData().getFileName();
	}

	public String getFormatedSize() {
		return getFileData().getFormatedSize();
	}

	public long getSize() {
		return getFileData().getSize();
	}

	public String getUploadedBy() {
		return getFileData().getOwner();
	}

	public Map getVariants() {
		return getFileData().getVariants();
	}
	
	public RiotFile get(String name) {
		return getFileData().getVariant(name) ;
	}
}
