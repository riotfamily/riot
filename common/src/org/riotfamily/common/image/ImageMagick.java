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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.riotfamily.common.io.RuntimeCommand;
import org.riotfamily.common.util.RiotLog;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class ImageMagick implements InitializingBean {

	private RiotLog log = RiotLog.get(ImageMagick.class);
	
	private static Pattern majorMinorPattern = Pattern.compile("ImageMagick ([0-9]).([0-9])");
	
	private static Pattern majorMinorMicroPattern = Pattern.compile("ImageMagick ([0-9]).([0-9]).([0-9])");
	
	private String commandName;
	
	private String command;
	
	private int majorVersion;
	
	private int minorVersion;
	
	private int microVersion;
	
	public ImageMagick() {
		this("convert");
	}
	
	public ImageMagick(String commandName) {
		this.commandName = commandName;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
	private String getDefaultCommand() {
		StringBuilder cmd = new StringBuilder();
		String os = System.getProperty("os.name");
		boolean windows = os.startsWith("Windows");
		String magickHome = System.getenv("MAGICK_HOME");
		if (magickHome != null) {
			cmd.append(magickHome).append(File.separatorChar);
			if (!windows) {
				cmd.append("bin");
				cmd.append(File.separatorChar);
			}
		}
		cmd.append(commandName);
		if (windows) {
			cmd.append(".exe");
		}
		return cmd.toString();
	}
	
	public void afterPropertiesSet() {
		try {
			if (command == null) {
				command = getDefaultCommand();
			}
			log.info("Looking for ImageMagick binary: " + command);
			String version = new RuntimeCommand(command, "-version").exec().getOutput();
			log.info(version);
			
			Matcher matcher = majorMinorMicroPattern.matcher(version);
			if (matcher.find()) {
				majorVersion = Integer.parseInt(matcher.group(1));
				minorVersion = Integer.parseInt(matcher.group(2));
				microVersion = Integer.parseInt(matcher.group(3));
			}
			else {
				matcher = majorMinorPattern.matcher(version);
				if (matcher.find()) {
					majorVersion = Integer.parseInt(matcher.group(1));
					minorVersion = Integer.parseInt(matcher.group(2));
				}
			}
			log.info(String.format("Major version: %d minor version: %d " 
						+ "micro version %d",majorVersion, minorVersion, 
						microVersion));
		}
		catch (NumberFormatException e) {
			log.warn("Could not determine ImageMagick version");
		}
		catch (IOException e) {
			log.warn("ImageMagick not found.");
		}
	}
	
	public boolean isAvailable() {
		return majorVersion > 0;
	}
	
	public boolean supportsVersion(int majorVersion, int minorVersion) {
		return (this.majorVersion == majorVersion 
			&& this.minorVersion >= minorVersion)
			|| this.majorVersion > majorVersion; 			
	}
	
	public boolean supportsVersion(int majorVersion, int minorVersion, 
				int microVersion) {
		
		return (this.majorVersion == majorVersion 
				&& this.minorVersion == minorVersion 
				&& this.microVersion >= microVersion)
				|| (this.majorVersion == majorVersion 
						&& this.minorVersion > minorVersion)
				|| this.majorVersion > majorVersion;
	}
	
	public String invoke(String... args) throws IOException {
		Assert.state(isAvailable(), "ImageMagick binary '" 
				+ command + "' not found in path.");
		
		String[] cmd = new String[args.length + 1];
		int i = 0;
		cmd[i++] = command;
		for (String arg : args) {
			cmd[i++] = arg;
		}
		return new RuntimeCommand(cmd).exec().getResult();
	}
	
	public String invoke(List<String> args) throws IOException {
		Assert.state(isAvailable(), "ImageMagick binary '" 
				+ command + "' not found in path.");
		
		String[] cmd = new String[args.size() + 1];
		cmd[0] = command;
		for (int i = 0; i < args.size(); i++) {
			cmd[i + 1] = (String) args.get(i);
		}
		return new RuntimeCommand(cmd).exec().getResult();
	}	
	
}
