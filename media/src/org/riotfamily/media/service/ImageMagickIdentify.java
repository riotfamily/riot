package org.riotfamily.media.service;

import java.io.File;
import java.io.IOException;

import net.sf.json.JSONObject;

import org.riotfamily.common.image.ImageMagick;

public class ImageMagickIdentify extends ImageMagick {

	private static final String JSON_FORMAT = "{format: '%m', width: %w, "
			+ "height: %h, type: '%r'}";

	public ImageMagickIdentify() {
		super("identify");
	}
	
	public ImageMetaData identify(File file) throws UnknownFormatException {
		try {
			String meta = invoke("-ping", "-format", JSON_FORMAT, file.getAbsolutePath());
			JSONObject json = JSONObject.fromObject(meta);
			return (ImageMetaData) JSONObject.toBean(json, ImageMetaData.class);
		}
		catch (IOException e) {
			throw new UnknownFormatException();
		}
	}
}
