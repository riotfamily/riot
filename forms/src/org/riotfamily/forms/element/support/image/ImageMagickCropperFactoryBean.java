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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.element.support.image;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.util.CommandUtils;
import org.riotfamily.common.util.FormatUtils;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 *
 */
public class ImageMagickCropperFactoryBean extends AbstractFactoryBean {

	private static Log log = LogFactory.getLog(ImageMagickCropperFactoryBean.class);
	
	private String convertCommand = "convert";
	
	public void setConvertCommand(String convertCommand) {
		this.convertCommand = convertCommand;
	}

	protected Object createInstance() throws Exception {
		try {
			String version = CommandUtils.exec(convertCommand, "-version");
			log.info(version);
			ImageMagickCropper cropper = new ImageMagickCropper();
			int majorVersion = FormatUtils.parseInt(version, "ImageMagick ([0-9])");
			log.info("Major version: " + majorVersion);
			if (majorVersion < 6) {
				cropper.setRepage(false);
			}
			cropper.setConvertCommand(convertCommand);
			return cropper;
		}
		catch (IOException e) {
			log.warn("ImageMagick not found in PATH - cropping disabled.");
		}
		return null;
	}

	public Class getObjectType() {
		return ImageMagickCropper.class;
	}

}
