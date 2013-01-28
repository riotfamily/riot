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
import java.io.IOException;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.riotfamily.common.util.HashUtils;
import org.riotfamily.media.processing.ImageCropper;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
@Entity
@DiscriminatorValue("crop")
public class CroppedRiotImage extends RiotImage {

	private RiotImage original;
	
	private int x;
	
	private int y;
	
	private int scaledWidth;

	public CroppedRiotImage() {
	}

	public CroppedRiotImage(RiotImage original, ImageCropper cropper,
			int width, int height, int x, int y, int scaledWidth, String bucket) throws IOException {
		
		super(bucket);
		this.original = original;
		this.x = x;
		this.y = y;
		this.scaledWidth = scaledWidth;
		setCreationDate(new Date());
		setFileName(original.getFileName());
		setUri(mediaService.store(null, original.getFileName(), getBucket()));
		
		File croppedFile = getFile();
		cropper.cropImage(original.getFile(), croppedFile, width, height, x, y, 
				scaledWidth);

		setSize(croppedFile.length());
		setMd5(HashUtils.md5(new FileInputStream(croppedFile)));
		inspect(croppedFile);
	}

	@ManyToOne(cascade=CascadeType.ALL)
	public RiotImage getOriginal() {
		return this.original;
	}

	public void setOriginal(RiotImage original) {
		this.original = original;
	}

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getScaledWidth() {
		return this.scaledWidth;
	}

	public void setScaledWidth(int scaledWidth) {
		this.scaledWidth = scaledWidth;
	}
	
}
