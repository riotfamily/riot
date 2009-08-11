package org.riotfamily.media.service;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class VideoMetaData {

	private int width;
	
	private int height;

	private long duration;

	private int bps;
	
	private String videoCodec;
	
	private float fps;
	
	private String audioCodec;
	
	private int samplingRate;
	
	private boolean stereo;

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
