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
package org.riotfamily.media.service;

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
public class FFmpeg implements InitializingBean {

	private Logger log = LoggerFactory.getLogger(FFmpeg.class);
	
	private static final Pattern DURATION_PATTERN = Pattern.compile(
			"Duration: (\\d\\d):(\\d\\d):(\\d\\d)");

	private static final Pattern BITRATE_PATTERN = Pattern.compile(
			"bitrate: (\\d+) kb/s");
	
	private static final Pattern VIDEO_PATTERN = Pattern.compile(
			"Video: (\\w+).*?(\\d+)x(\\d+).*?(\\d+\\.?\\d+?) (fps|tb\\(r\\)|tbr)");
	
	private static final Pattern AUDIO_PATTERN = Pattern.compile(
			"Audio: (\\w+).*?(\\d+) Hz, (mono|stereo)");
	
	private String command;

	private String version;
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	private String getDefaultCommand() {
		String os = System.getProperty("os.name");
		return os.startsWith("Windows") ? "ffmpeg.exe" : "ffmpeg";
	}
	
	public void afterPropertiesSet() {
		try {
			if (command == null) {
				command = getDefaultCommand();
			}
			log.info("Looking for FFmpeg binary: " + command);
			version = IOUtils.exec(command, "-version");
			log.info(version);
		}
		catch (IOException e) {
			log.warn("FFmpeg not found.");
		}
	}

	public boolean isAvailable() {
		return version != null;
	}
	
	public String getVersion() {
		return this.version;
	}

	public String invoke(List<String> args) throws IOException {
		Assert.state(isAvailable(), "FFmpeg binary '" 
				+ command + "' not found in path.");
		
		return IOUtils.exec(command, args);
	}
	
	public VideoMetaData identify(File file) throws IOException {
		String out = IOUtils.exec(command, "-i", file.getAbsolutePath());
		VideoMetaData meta = new VideoMetaData(); 
		Matcher m = DURATION_PATTERN.matcher(out);
		if (m.find()) {
			int hh = Integer.parseInt(m.group(1));
			int mm = Integer.parseInt(m.group(2));
			int ss = Integer.parseInt(m.group(3));
			meta.setDuration(hh * 60 * 60 + mm * 60 + ss);
		}
		
		m = BITRATE_PATTERN.matcher(out);
		if (m.find()) {
			meta.setBps(Integer.parseInt(m.group(1)));
		}
		
		m = VIDEO_PATTERN.matcher(out);
		if (m.find()) {
			meta.setVideoCodec(m.group(1));
			meta.setWidth(Integer.parseInt(m.group(2)));
			meta.setHeight(Integer.parseInt(m.group(3)));
			meta.setFps(Float.parseFloat(m.group(4)));
		}
		
		m = AUDIO_PATTERN.matcher(out);
		if (m.find()) {
			meta.setAudioCodec(m.group(1));
			meta.setSamplingRate(Integer.parseInt(m.group(2)));
			meta.setStereo("stereo".equals(m.group(3)));
		}
		
		return meta;
	}
}
