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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Set;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.HashUtils;
import org.riotfamily.media.store.FileStore;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class FileData {
	
	protected static FileStore fileStore;

	public static void setFileStore(FileStore fileStore) {
		FileData.fileStore = fileStore;
	}
	
	private Long id;
	
	private String uri;
	
	private String fileName;
	
	private String contentType;
	
	private long size;
	
	private String md5;
	
	private String owner;
	
	private Date creationDate;
	
	private Set files;
	
	
	public FileData() {
	}
	
	public FileData(File file) throws IOException {
		setFile(file);
	}
	
	public FileData(MultipartFile multipartFile) throws IOException {
		setMultipartFile(multipartFile);
	}

	public void setMultipartFile(MultipartFile multipartFile) throws IOException {
		fileName = multipartFile.getOriginalFilename();
		size = multipartFile.getSize();
		contentType = multipartFile.getContentType();
		creationDate = new Date();
		md5 = HashUtils.md5(multipartFile.getInputStream());
		//FIXME Does not work with SwfUpload (no session, no user)
		//owner = AccessController.getCurrentUser().getUserId();
		uri = fileStore.store(multipartFile.getInputStream(), fileName);
		inspect(getFile());
	}
	
	public void setFile(File file) throws IOException {
		fileName = file.getName();
		size = file.length();
		//FIXME Use static FileTypeMap
		//contentType = file.getContentType();
		creationDate = new Date();
		//FIXME Does not work with SwfUpload (no session, no user)
		//owner = AccessController.getCurrentUser().getUserId();
		uri = fileStore.store(new FileInputStream(file), fileName);
		md5 = HashUtils.md5(new FileInputStream(file));
		inspect(file);
	}
	
	protected void inspect(File file) throws IOException {
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUri() {
		return this.uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public File getFile() {
		return fileStore.retrieve(uri);
	}
	
	public void deleteFile() {
		fileStore.delete(uri);
	}

	public InputStream getInputStream() throws FileNotFoundException {
		return new FileInputStream(getFile());
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getSize() {
		return this.size;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
	public String getFormatedSize() {
		return FormatUtils.formatByteSize(size);
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String uploadedBy) {
		this.owner = uploadedBy;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getMd5() {
		return this.md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public Set getFiles() {
		return this.files;
	}

	public void setFiles(Set files) {
		this.files = files;
	}

}
