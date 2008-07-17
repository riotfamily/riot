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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.HashUtils;
import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.service.MediaService;
import org.riotfamily.riot.security.AccessController;
import org.riotfamily.riot.security.auth.RiotUser;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
@Entity
@Table(name="riot_file_data")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("file")
public class FileData {
	
	protected static MediaService mediaService;

	public static void setMediaService(MediaService mediaService) {
		FileData.mediaService = mediaService;
	}
	
	private Long id;
	
	private String uri;
	
	private String fileName;
	
	private String contentType;
	
	private long size;
	
	private String md5;
	
	private String owner;
	
	private Date creationDate;
	
	private Set<RiotFile> files;
	
	private Map<String, RiotFile> variants;
	
	private transient boolean emptyFileCreated;
	
	public FileData() {
	}
	
	public FileData(File file) throws IOException {
		setFile(file);
	}
	
	public FileData(MultipartFile multipartFile) throws IOException {
		setMultipartFile(multipartFile);
	}
	
	public FileData(InputStream in, String fileName) throws IOException {
		setInputStream(in, fileName);
	}
	
	public FileData(byte[] bytes, String fileName) throws IOException {
		setBytes(bytes, fileName);
	}

	@Transient
	public void setMultipartFile(MultipartFile multipartFile) throws IOException {
		fileName = multipartFile.getOriginalFilename();
		size = multipartFile.getSize();
		contentType = multipartFile.getContentType();
		initCreationInfo();
		uri = mediaService.store(multipartFile.getInputStream(), fileName);
		md5 = HashUtils.md5(multipartFile.getInputStream());
		inspect(getFile());
	}
	
	@Transient
	public void setFile(File file) throws IOException {
		fileName = file.getName();
		size = file.length();
		contentType = mediaService.getContentType(file);
		initCreationInfo();
		uri = mediaService.store(new FileInputStream(file), fileName);
		md5 = HashUtils.md5(new FileInputStream(file));
		inspect(file);
	}
	
	@Transient
	public void setInputStream(InputStream in, String fileName) throws IOException {
		File f = createEmptyFile(fileName);
		FileCopyUtils.copy(in, new FileOutputStream(f));
		contentType = mediaService.getContentType(f);		
		update();
	}
	
	@Transient
	public void setBytes(byte[] bytes, String fileName) throws IOException {
		File f = createEmptyFile(fileName);
		FileCopyUtils.copy(bytes, f);
		contentType = mediaService.getContentType(f);		
		update();
	}
	
	public File createEmptyFile(String name) throws IOException {
		fileName = name;
		uri = mediaService.store(null, name);
		emptyFileCreated = true;
		initCreationInfo();
		return getFile();
	}
	
	public void update() throws IOException {
		Assert.state(emptyFileCreated == true, "update() must only be called " +
				"after createEmptyFile() has been invoked!");
		
		size = getFile().length();
		md5 = HashUtils.md5(getInputStream());
		inspect(getFile());
	}
	
	private void initCreationInfo() {
		creationDate = new Date();
		RiotUser user = AccessController.getCurrentUser();
		//FIXME Does not work with swfupload.js (no session, no user)
		if (user != null) {
			owner = user.getUserId();
		}
	}
	
	protected void inspect(File file) throws IOException {
	}
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
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
	
	@Transient
	public File getFile() {
		return mediaService.retrieve(uri);
	}
	
	public void deleteFile() {
		mediaService.delete(uri);
	}

	@Transient
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
	
	@Transient
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

	@OneToMany(mappedBy="fileData")
	public Set<RiotFile> getFiles() {
		return this.files;
	}

	public void setFiles(Set<RiotFile> files) {
		this.files = files;
	}
	
	@OneToMany
	@Cascade(CascadeType.ALL)
	public Map<String, RiotFile> getVariants() {
		return this.variants;
	}

	public void setVariants(Map<String, RiotFile> variants) {
		this.variants = variants;
	}
	
	public void addVariant(String name, RiotFile variant) {
		if (variants == null) {
			variants = new HashMap<String, RiotFile>();
		}
		variants.put(name, variant);
	}
	
	public RiotFile getVariant(String name) {
		if (variants == null) {
			return null;
		}
		return (RiotFile) variants.get(name);
	}

	protected void finalize() throws Throwable {
		if (id == null && uri != null) {
			mediaService.delete(uri);
		}
	}
	
	@Override
	public int hashCode() {
		if (uri != null) {
			return uri.hashCode();
		}
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (uri != null && obj instanceof FileData) {
			FileData other = (FileData) obj;
			return uri.equals(other.uri);
		}
		return super.equals(obj);
	}
}
