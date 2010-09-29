/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.media.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.riotfamily.common.hibernate.ActiveRecordBeanSupport;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.HashUtils;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.media.meta.MediaService;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
@Entity
@Table(name="riot_files",
	uniqueConstraints={
		@UniqueConstraint(columnNames={"uri"})			
	}
)
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("file")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="media")
public class RiotFile extends ActiveRecordBeanSupport {
	
	protected MediaService mediaService;

	private String uri;
	
	private String fileName;
	
	private String contentType;
	
	private long size;
	
	private String md5;
	
	private String owner;
	
	private Date creationDate;
	
	private Map<String, RiotFile> variants;
	
	private transient boolean emptyFileCreated;
	
	public RiotFile() {
	}
	
	public RiotFile(File file) throws IOException {
		setFile(file);
	}
	
	public RiotFile(MultipartFile multipartFile) throws IOException {
		setMultipartFile(multipartFile);
	}
	
	public RiotFile(InputStream in, String fileName) throws IOException {
		setInputStream(in, fileName);
	}
	
	public RiotFile(byte[] bytes, String fileName) throws IOException {
		setBytes(bytes, fileName);
	}

	public RiotFile(RiotFile riotFile) throws IOException {
		this(riotFile, true);
	}
	
	public RiotFile(RiotFile riotFile, boolean copyVariants) throws IOException {
		this.fileName = riotFile.getFileName();
		this.uri = mediaService.store(new FileInputStream(riotFile.getFile()), fileName);
		this.contentType = riotFile.getContentType();
		this.size = riotFile.getSize();
		this.md5 = riotFile.getMd5();
		this.owner = riotFile.getOwner();
		this.creationDate = riotFile.getCreationDate();

		if (copyVariants) {
			Map<String, RiotFile> otherVariants = riotFile.getVariants();
			if (otherVariants != null) {
				this.variants = Generics.newHashMap();
				for (Entry<String, RiotFile> entry : otherVariants.entrySet()) {
					RiotFile variant = entry.getValue();
					if (variant != null) {
						addVariant(entry.getKey(), variant.copy(true));
					}
				}
			}
		}
	}

	public void setMediaService(MediaService mediaService) {
		this.mediaService = mediaService;
	}
	
	public RiotFile copy(boolean copyVariants) throws IOException {
		return new RiotFile(this, copyVariants);
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
		updateMetaData();
	}
	
	@Transient
	public void setBytes(byte[] bytes, String fileName) throws IOException {
		File f = createEmptyFile(fileName);
		FileCopyUtils.copy(bytes, f);
		contentType = mediaService.getContentType(f);		
		updateMetaData();
	}
	
	public File createEmptyFile(String name) throws IOException {
		fileName = name;
		uri = mediaService.store(null, name);
		emptyFileCreated = true;
		initCreationInfo();
		return getFile();
	}
	
	public void updateMetaData() throws IOException {
		Assert.state(emptyFileCreated == true, "updateMetaData() must only be "
				+ "called after createEmptyFile() has been invoked");
		
		size = getFile().length();
		md5 = HashUtils.md5(getInputStream());
		inspect(getFile());
	}
	
	private void initCreationInfo() {
		creationDate = new Date();
		RiotUser user = AccessController.getCurrentUser();
		if (user != null) {
			owner = user.getUserId();
		}
	}
	
	protected void inspect(File file) throws IOException {
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

	@Column(name="`size`")
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

	@OneToMany(cascade=CascadeType.ALL)
	@JoinTable(name="riot_file_variants")
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
	
	public RiotFile get(String name) {
		if (variants == null) {
			return null;
		}
		return variants.get(name);
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
		if (uri != null && obj instanceof RiotFile) {
			RiotFile other = (RiotFile) obj;
			return uri.equals(other.uri);
		}
		return super.equals(obj);
	}
	
	// ----------------------------------------------------------------------
	// Active record methods
	// ----------------------------------------------------------------------
	
	public static RiotFile loadByUri(String uri) {
		return query(RiotFile.class, "from {} where uri = ?", uri).load();
	}
	
	public static RiotFile loadByMd5(String md5) {
		return query(RiotFile.class, "from {} where md5 = ?", md5).load();
	}
	
}
