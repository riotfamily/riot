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
package org.riotfamily.common.image;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.io.CommandUtils;
import org.riotfamily.common.io.RuntimeCommand;
import org.riotfamily.common.util.FormatUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class ImageMagick implements InitializingBean {

	private static Log log = LogFactory.getLog(ImageMagick.class);
	
	private String convertCommand;

	private int majorVersion;
	
	public void setConvertCommand(String convertCommand) {
		this.convertCommand = convertCommand;
	}

	private String getDefaultConvertCommand() {
		String os = System.getProperty("os.name");
		return os.startsWith("Windows") ? "convert.exe" : "convert";
	}
	
	public void afterPropertiesSet() {
		try {
			if (convertCommand == null) {
				convertCommand = getDefaultConvertCommand();
			}
			log.info("Looking for ImageMagick binary: " + convertCommand);
			String version = new RuntimeCommand(new String[] {convertCommand, "-version"}).exec().getOutput();
			log.info(version);
			majorVersion = FormatUtils.extractInt(version, "ImageMagick ([0-9])");
			log.info("Major version: " + majorVersion);
		}
		catch (IOException e) {
			log.warn("ImageMagick not found.");
		}
	}
	
	public boolean isAvailable() {
		return majorVersion > 0;
	}
	
	public int getMajorVersion() {
		return this.majorVersion;
	}
	
	public String invoke(List args) throws IOException {
		Assert.state(isAvailable(), "ImageMagick binary '" 
				+ convertCommand + "' not found in path.");
		
		String[] cmd = new String[args.size() + 1];
		cmd[0] = convertCommand;
		for (int i = 0; i < args.size(); i++) {
			cmd[i + 1] = (String) args.get(i);
		}
		return CommandUtils.exec(cmd);
	}
	
	public static ImageMagick getInstance(ApplicationContext ctx) {
		return (ImageMagick) BeanFactoryUtils.beanOfTypeIncludingAncestors(
				ctx, ImageMagick.class);
	}
}
