package org.riotfamily.forms.element.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.devlib.schmidt.imageinfo.ImageInfo;
import org.riotfamily.forms.bind.EditorBinder;
import org.riotfamily.forms.error.ErrorUtils;

/**
 * Specialized FileUpload element for image uploads.
 */
public class ImageUpload extends FileUpload {

	private int width;
	
	private int height;
	
	private int minWidth;
	
	private int maxWidth;
	
	private int minHeight;
	
	private int maxHeight;
	
	private String validFormats;

	private String widthProperty;
	
	private String heightProperty;
	
	private ImageInfo info;
	
	public void setHeight(int height) {
		this.height = height;
	}


	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}


	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}


	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}


	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}


	public void setValidFormats(String validFormats) {
		this.validFormats = validFormats;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	
	public void setHeightProperty(String heightProperty) {
		this.heightProperty = heightProperty;
	}

	public void setWidthProperty(String widthProperty) {
		this.widthProperty = widthProperty;
	}


	protected void validateFile(File file) {
		try {
			info = new ImageInfo();
			info.setInput(new FileInputStream(file));
			info.check();
			log.debug(info.getFormatName() + " Size: " 
					+ info.getWidth() + "x" + info.getHeight());
			
			if (validFormats != null) {
				if (validFormats.indexOf(info.getFormatName()) == -1) {
					ErrorUtils.reject(this, "image.invalidFormat", validFormats);
				}
			}
			int imageHeight = info.getHeight();
			int imageWidth = info.getWidth();
			
			if (width > 0) {
				if (imageWidth != width) {
					ErrorUtils.reject(this, "image.width.mismatch", new Integer(width));
				}
			}
			else {
				if (imageWidth < minWidth) {
					ErrorUtils.reject(this, "image.width.tooSmall", new Integer(minWidth));
				}
				if (maxWidth > 0 && imageWidth > maxWidth) {
					ErrorUtils.reject(this, "image.width.tooLarge", new Integer(maxWidth));
				}
			}
			
			if (height > 0) {
				if (imageHeight != height) {
					ErrorUtils.reject(this, "image.height.mismatch", new Integer(height));
				}
			}
			else {
				if (imageHeight < minHeight) {
					ErrorUtils.reject(this, "image.height.tooSmall", new Integer(minHeight));
				}
				if (maxHeight > 0 && imageHeight > maxHeight) {
					ErrorUtils.reject(this, "image.height.tooLarge", new Integer(maxHeight));
				}
			}
		}
		catch (IOException e) {
		}
	}

	public Object getValue() {
		if (info != null) {
			EditorBinder editorBinder = getEditorBinding().getEditorBinder();
			if (widthProperty != null) {
				editorBinder.setPropertyValue(widthProperty, 
						new Integer(info.getWidth()));
			}
			if (heightProperty != null) {
				editorBinder.setPropertyValue(heightProperty, 
						new Integer(info.getHeight()));
			}
		}
		return super.getValue();
	}

}
