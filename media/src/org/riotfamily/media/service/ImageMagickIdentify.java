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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
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
