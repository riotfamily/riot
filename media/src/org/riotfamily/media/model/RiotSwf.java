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
import java.io.IOException;
import java.io.InputStream;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.riotfamily.media.meta.FlashInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
@Entity
@DiscriminatorValue("swf")
public class RiotSwf extends RiotFile {

	private static final String CONTENT_TYPE = "application/x-shockwave-flash";
	
	private int width;
	
	private int height;
	
	private int version;
	
	public RiotSwf() {
	}
	
	public RiotSwf(String bucket) {
		super(bucket);
	}
	
	public RiotSwf(File file) throws IOException {
		super(file);
	}

	public RiotSwf(MultipartFile multipartFile) throws IOException {
		super(multipartFile);
	}
	
	public RiotSwf(InputStream in, String fileName) throws IOException {
		super(in, fileName);
	}
	
	public RiotSwf(byte[] bytes, String fileName) throws IOException {
		super(bytes, fileName);
	}

	public RiotSwf(RiotSwf riotSwf) throws IOException {
		this(riotSwf, true);
	}

	public RiotSwf(RiotSwf riotSwf, boolean copyVariants) throws IOException {
		super(riotSwf, copyVariants);
		this.width = riotSwf.getWidth();
		this.height = riotSwf.getHeight();
		this.version = riotSwf.getVersion();
	}
	
	@Override
	public RiotSwf copy(boolean copyVariants) throws IOException {
		return new RiotSwf(this, copyVariants);
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
