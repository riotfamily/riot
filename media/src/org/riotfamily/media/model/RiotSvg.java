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

import org.riotfamily.media.meta.ImageMetaData;
import org.springframework.web.multipart.MultipartFile;

@Entity
@DiscriminatorValue("svg")
public class RiotSvg extends RiotFile {
	
	private static final String CONTENT_TYPE = "image/svg+xml";
	
	private String format;
	
	private int width;
	
	private int height;
	
	public RiotSvg() {
	}
	
	public RiotSvg(String bucket) {
		super(bucket);
	}
	
	public RiotSvg(File file) throws IOException {
		super(file);
	}

	public RiotSvg(MultipartFile multipartFile) throws IOException {
		super(multipartFile);
	}
	
	public RiotSvg(InputStream in, String fileName) throws IOException {
		super(in, fileName);
	}
	
	public RiotSvg(byte[] bytes, String fileName) throws IOException {
		super(bytes, fileName);
	}

	public RiotSvg(RiotSvg riotSvg) throws IOException {
		this(riotSvg, true);
	}

	public RiotSvg(RiotSvg riotSvg, boolean copyVariants) throws IOException {
		super(riotSvg, copyVariants);
	}
	
	@Override
	public RiotSvg copy(boolean copyVariants) throws IOException {
		return new RiotSvg(this, copyVariants);
	}
	
	protected void inspect(File file) throws IOException {
		ImageMetaData meta = mediaService.identifySvg(file);
		format = meta.getFormat();
		width = meta.getWidth();
		height = meta.getHeight();
		setContentType(CONTENT_TYPE);
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

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	@Transient
	public boolean isValid() {
		return "svg".equalsIgnoreCase(format);
	}
}
