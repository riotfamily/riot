package org.riotfamily.media.model;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.riotfamily.common.image.ImageCropper;

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
			int width, int height, int x, int y, int scaledWidth) throws IOException {
		
		this.original = original;
		this.x = x;
		this.y = y;
		this.scaledWidth = scaledWidth;
		setCreationDate(new Date());
		setFileName(original.getFileName());
		setUri(mediaService.store(null, original.getFileName()));
		
		File croppedFile = getFile();
		cropper.cropImage(original.getFile(), croppedFile, width, height, x, y, 
				scaledWidth);

		setSize(croppedFile.length());
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
