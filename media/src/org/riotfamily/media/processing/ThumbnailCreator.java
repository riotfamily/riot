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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.media.processing;

import java.io.File;
import java.io.IOException;

import org.riotfamily.common.image.ImageMagick;
import org.riotfamily.common.image.ImageMagickThumbnailer;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.model.RiotImage;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class ThumbnailCreator extends AbstractFileProcessor 
		implements InitializingBean {

	private ImageMagickThumbnailer thumbnailer;
	
	private boolean crop;
	
	private String backgroundColor;
	
	private String format;
	
	private int width;
	
	private int height;
	

	public ThumbnailCreator(ImageMagick imageMagick) {
		thumbnailer = new ImageMagickThumbnailer(imageMagick);
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
		
	public void afterPropertiesSet() {
		thumbnailer.setBackgroundColor(backgroundColor);
		thumbnailer.setCrop(crop);
	}
	
	protected RiotFile createVariant(RiotFile original) throws IOException {
		RiotImage thumbnail = new RiotImage();
		String thumbName = original.getFileName();
		if (format != null) {
			thumbName = FormatUtils.stripExtension(thumbName);
			thumbName += "." + format.toLowerCase();
		}
		File dest = thumbnail.createEmptyFile(thumbName);
		thumbnailer.renderThumbnail(original.getFile(), dest, width, height);
		thumbnail.update();
		if (!thumbnail.isValid()) {
			throw new IOException("Thumbnail creation failed");
		}
		return thumbnail;
	}
	
}
