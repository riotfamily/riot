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
package org.riotfamily.media.model.data;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.riotfamily.common.io.RuntimeCommand;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class VideoData extends FileData {

	private static final Pattern DURATION_PATTERN = Pattern.compile(
			"Duration: (\\d\\d):(\\d\\d):(\\d\\d)");
	
	private static final Pattern BITRATE_PATTERN = Pattern.compile(
			"bitrate: (\\d+) kb/s");

	private static final Pattern VIDEO_PATTERN = Pattern.compile(
			"Video: (\\w+).*?(\\d+)x(\\d+).*?(\\d+\\.\\d+) fps");
	
	private static final Pattern AUDIO_PATTERN = Pattern.compile(
			"Audio: (\\w+).*?(\\d+) Hz, (mono|stereo)");

	private int width;
	
	private int height;

	private long duration;

	private int bps;
	
	private String videoCodec;
	
	private float fps;
	
	private String audioCodec;
	
	private int samplingRate;
	
	private boolean stereo;
	
	
	public VideoData() {
	}
	
	public VideoData(File file) throws IOException {
		super(file);
	}

	public VideoData(MultipartFile multipartFile) throws IOException {
		super(multipartFile);
	}

	protected void inspect(File file) throws IOException {
		RuntimeCommand cmd = new RuntimeCommand(new String[] {"ffmpeg", "-i", 
				file.getAbsolutePath()});

		String out = cmd.exec().getErrors();
		
		Matcher m = DURATION_PATTERN.matcher(out);
		if (m.find()) {
			int hh = Integer.parseInt(m.group(1));
			int mm = Integer.parseInt(m.group(2));
			int ss = Integer.parseInt(m.group(3));
			setDuration(hh * 60 * 60 + mm * 60 + ss);
			setContentType("video/mpeg");
		}
		
		m = BITRATE_PATTERN.matcher(out);
		if (m.find()) {
			setBps(Integer.parseInt(m.group(1)));
		}
		
		m = VIDEO_PATTERN.matcher(out);
		if (m.find()) {
			setVideoCodec(m.group(1));
			setWidth(Integer.parseInt(m.group(2)));
			setHeight(Integer.parseInt(m.group(3)));
			setFps(Float.parseFloat(m.group(4)));
		}
		
		m = AUDIO_PATTERN.matcher(out);
		if (m.find()) {
			setAudioCodec(m.group(1));
			setSamplingRate(Integer.parseInt(m.group(2)));
			setStereo("stereo".equals(m.group(3)));
		}
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public long getDuration() {
		return this.duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getBps() {
		return this.bps;
	}

	public void setBps(int bps) {
		this.bps = bps;
	}

	public String getVideoCodec() {
		return this.videoCodec;
	}

	public void setVideoCodec(String videoCodec) {
		this.videoCodec = videoCodec;
	}

	public float getFps() {
		return this.fps;
	}

	public void setFps(float fps) {
		this.fps = fps;
	}

	public String getAudioCodec() {
		return this.audioCodec;
	}

	public void setAudioCodec(String audioCodec) {
		this.audioCodec = audioCodec;
	}

	public int getSamplingRate() {
		return this.samplingRate;
	}

	public void setSamplingRate(int samplingRate) {
		this.samplingRate = samplingRate;
	}

	public boolean isStereo() {
		return this.stereo;
	}

	public void setStereo(boolean stereo) {
		this.stereo = stereo;
	}
	
}
