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
package org.riotfamily.media.processing;

import java.io.File;
import java.io.IOException;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.model.RiotImage;
import org.riotfamily.media.processing.ImageMagick;
import org.riotfamily.media.processing.ImageMagickThumbnailer;
import org.riotfamily.media.processing.Thumbnailer;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class ThumbnailCreator {

	private Thumbnailer thumbnailer;
	
	private boolean crop;
	
	private String backgroundColor;
	
	private String format;
	
	private int width;
	
	private int height;
	

	public ThumbnailCreator(Thumbnailer thumbnailer) {
		this.thumbnailer = thumbnailer;
	}

	public ThumbnailCreator(ImageMagick imageMagick) {
		this.thumbnailer = new ImageMagickThumbnailer(imageMagick);
	}
	
	public void setCrop(boolean crop) {
		this.crop = crop;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}
		
	protected RiotFile createVariant(RiotFile original) throws IOException {
		RiotImage thumbnail = new RiotImage();
		String thumbName = original.getFileName();
		if (format != null) {
			thumbName = FormatUtils.stripExtension(thumbName);
			thumbName += "." + format.toLowerCase();
		}
		File dest = thumbnail.createEmptyFile(thumbName);
		boolean fixedSize = crop || backgroundColor != null;
		thumbnailer.renderThumbnail(original.getFile(), dest, width, height,
				fixedSize, backgroundColor);
		
		thumbnail.updateMetaData();
		if (!thumbnail.isValid()) {
			throw new IOException("Thumbnail creation failed");
		}
		return thumbnail;
	}
	
}
