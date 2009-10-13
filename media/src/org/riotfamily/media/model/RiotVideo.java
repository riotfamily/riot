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
package org.riotfamily.media.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.riotfamily.media.service.VideoMetaData;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
@Entity
@DiscriminatorValue("video")
public class RiotVideo extends RiotFile {

	private int width;
	
	private int height;

	private long duration;

	private int bps;
	
	private String videoCodec;
	
	private float fps;
	
	private String audioCodec;
	
	private int samplingRate;
	
	private boolean stereo;
	
	
	public RiotVideo() {
	}
	
	public RiotVideo(File file) throws IOException {
		super(file);
	}

	public RiotVideo(MultipartFile multipartFile) throws IOException {
		super(multipartFile);
	}

	public RiotVideo(InputStream in, String fileName) throws IOException {
		super(in, fileName);
	}
	
	public RiotVideo(byte[] bytes, String fileName) throws IOException {
		super(bytes, fileName);
	}

	public RiotVideo(RiotVideo riotVideo) throws IOException {
		this(riotVideo, true);
	}
	
	public RiotVideo(RiotVideo riotVideo, boolean copyVariants) throws IOException {
		super(riotVideo, copyVariants);
		this.width = riotVideo.getWidth();
		this.height = riotVideo.getHeight();
		this.duration = riotVideo.getDuration();
		this.bps = riotVideo.getBps();
		this.videoCodec = riotVideo.getVideoCodec();
		this.fps = riotVideo.getFps();
		this.audioCodec = riotVideo.getAudioCodec();
		this.samplingRate = riotVideo.getSamplingRate();
		this.stereo = riotVideo.isStereo();
	}
	
	@Override
	public RiotVideo copy(boolean copyVariants) throws IOException {
		return new RiotVideo(this, copyVariants);
	}

	protected void inspect(File file) throws IOException {
		VideoMetaData meta = mediaService.identifyVideo(file);
		setContentType("video/mpeg");
		setDuration(meta.getDuration());
		setBps(meta.getBps());
		setVideoCodec(meta.getVideoCodec());
		setWidth(meta.getWidth());
		setHeight(meta.getHeight());
		setFps(meta.getFps());
		setAudioCodec(meta.getAudioCodec());
		setSamplingRate(meta.getSamplingRate());
		setStereo(meta.isStereo());
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
