package org.riotfamily.media.processing;

import java.io.File;
import java.io.IOException;

import org.riotfamily.common.image.ImageMagick;
import org.riotfamily.common.image.ImageMagickThumbnailer;
import org.riotfamily.common.image.Thumbnailer;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.model.RiotImage;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 * @deprecated
 */
public class ThumbnailCreator extends AbstractFileProcessor 
		implements InitializingBean {

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
