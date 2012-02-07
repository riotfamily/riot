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
package org.riotfamily.media.meta;

import java.io.File;
import java.io.IOException;

import net.sf.json.JSONObject;

import org.riotfamily.media.processing.ImageMagick;

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
			throw new UnknownFormatException(e.getMessage(), e);
		}
	}
}
