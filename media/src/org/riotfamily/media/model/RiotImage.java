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

import org.riotfamily.media.service.ImageMetaData;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
@Entity
@DiscriminatorValue("image")
public class RiotImage extends RiotFile {

	private String format;
	
	private int width;
	
	private int height;

	private boolean alpha;
	
	public RiotImage() {
	}

	public RiotImage(File file) throws IOException {
		super(file);
	}

	public RiotImage(MultipartFile multipartFile) throws IOException {
		super(multipartFile);
	}
	
	public RiotImage(InputStream in, String fileName) throws IOException {
		super(in, fileName);
	}

	public RiotImage(byte[] bytes, String fileName) throws IOException {
		super(bytes, fileName);
	}

	public RiotImage(RiotImage riotImage) throws IOException {
		this(riotImage, true);
	}

	public RiotImage(RiotImage riotImage, boolean copyVariants) throws IOException {
		super(riotImage, copyVariants);
		this.format = riotImage.getFormat();
		this.width = riotImage.getWidth();
		this.height = riotImage.getHeight();
		this.alpha = riotImage.isAlpha();
	}
	
	@Override
	public RiotImage copy(boolean copyVariants) throws IOException {
		return new RiotImage(this, copyVariants);
	}

	protected void inspect(File file) throws IOException {
		ImageMetaData meta = mediaService.identifyImage(file);
		format = meta.getFormat();
		width = meta.getWidth();
		height = meta.getHeight();
		alpha = meta.getType().contains("Matte");
		setContentType("image/" + format.toLowerCase());
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
	
	public boolean isAlpha() {
		return alpha;
	}

	public void setAlpha(boolean alpha) {
		this.alpha = alpha;
	}

	@Transient
	public boolean isValid() {
		return format != null;
	}
}
