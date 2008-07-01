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
package org.riotfamily.website.txt2img;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

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
	
	public void generate(String text, int maxWidth, String color, boolean hover, 
			OutputStream os) throws IOException {
		
		BufferedImage image = generate(text, maxWidth, color, hover);
		ImageUtils.write(image, "png", os);
		image.flush();
	}

}
