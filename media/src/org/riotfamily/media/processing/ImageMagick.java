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
package org.riotfamily.media.processing;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.riotfamily.common.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class ImageMagick implements InitializingBean {

	private Logger log = LoggerFactory.getLogger(ImageMagick.class);
	
	private static Pattern majorMinorPattern = Pattern.compile("ImageMagick ([0-9]).([0-9])");
	
	private static Pattern majorMinorMicroPattern = Pattern.compile("ImageMagick ([0-9]).([0-9]).([0-9])");
	
	private String magickHome;
	
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

	public void setMagickHome(String magickHome) {
		this.magickHome = magickHome;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	protected boolean isWindows() {
		String os = System.getProperty("os.name");
		return os.startsWith("Windows");
	}
	
	private String getDefaultCommand() {
		StringBuilder cmd = new StringBuilder();
		
		String dir = (magickHome != null) 
				? magickHome 
				: System.getenv("MAGICK_HOME");
		
		if (dir != null) {
			cmd.append(dir).append(File.separatorChar);
			if (!isWindows()) {
				cmd.append("bin");
				cmd.append(File.separatorChar);
			}
		}
		cmd.append(commandName);
		if (isWindows()) {
			cmd.append(".exe");
		}
		return cmd.toString();
	}
	
	public void afterPropertiesSet() {
		try {
			if (command == null) {
				command = getDefaultCommand();
			}
			log.info("Looking for ImageMagick binary '{}'", command);
			String version = IOUtils.exec(command, "-version");
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
			log.info(String.format("Version: %d.%d.%d", majorVersion, minorVersion, microVersion));
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
		
		return IOUtils.exec(command, args);
	}
	
	public String invoke(List<String> args) throws IOException {
		Assert.state(isAvailable(), "ImageMagick binary '" 
				+ command + "' not found in path.");
		
		return IOUtils.exec(command, args);
	}	
	
}
