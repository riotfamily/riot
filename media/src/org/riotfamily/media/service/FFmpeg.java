package org.riotfamily.media.service;

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
public class FFmpeg implements InitializingBean {

	private RiotLog log = RiotLog.get(FFmpeg.class);
	
	private static final Pattern DURATION_PATTERN = Pattern.compile(
			"Duration: (\\d\\d):(\\d\\d):(\\d\\d)");

	private static final Pattern BITRATE_PATTERN = Pattern.compile(
			"bitrate: (\\d+) kb/s");
	
	private static final Pattern VIDEO_PATTERN = Pattern.compile(
			"Video: (\\w+).*?(\\d+)x(\\d+).*?(\\d+\\.\\d+) (fps|tb\\(r\\))");
	
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
			
			RuntimeCommand cmd = new RuntimeCommand(command, "-version");
			version = cmd.exec().getErrors();
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
		
		String[] cmd = new String[args.size() + 1];
		cmd[0] = command;
		for (int i = 0; i < args.size(); i++) {
			cmd[i + 1] = (String) args.get(i);
		}
		return new RuntimeCommand(cmd).exec().getResult();
	}
	
	public VideoMetaData identify(File file) throws IOException {
		RuntimeCommand cmd = new RuntimeCommand(new String[] {command, "-i", 
				file.getAbsolutePath()});

		String out = cmd.exec().getErrors();
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
