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
package org.riotfamily.common.txt2img;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import org.riotfamily.common.image.ImageUtils;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ReplacementRule extends ListItemRenderer {

	private String selector;
	
	public String getSelector() {
		return this.selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public void generate(String text, Locale locale, int maxWidth, String color, boolean hover, 
			OutputStream os) throws IOException {
		
		BufferedImage image = generate(text, locale, maxWidth, color, hover);
		ImageUtils.write(image, "png", os);
		image.flush();
	}

}
