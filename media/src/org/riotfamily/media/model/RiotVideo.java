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
package org.riotfamily.media.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.riotfamily.media.model.data.VideoData;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
@Entity
@DiscriminatorValue("video")
public class RiotVideo extends RiotFile {

	public RiotVideo() {
		super();
	}

	public RiotVideo(VideoData data) {
		super(data);
	}
	
	public RiotVideo(File file) throws IOException {
		super(new VideoData(file));
	}
	
	public RiotVideo(MultipartFile multipartFile) throws IOException {
		super(new VideoData(multipartFile));
	}
	
	public RiotVideo(InputStream in, String fileName) throws IOException {
		super(new VideoData(in, fileName));
	}
	
	public RiotVideo(byte[] bytes, String fileName) throws IOException {
		super(new VideoData(bytes, fileName));
	}
	
	public RiotFile createCopy() {
		return new RiotVideo(getVideoData());
	}
	
	@Transient
	public VideoData getVideoData() {
		return (VideoData) getFileData();
	}
	
	@Transient
	public int getWidth() {
		return getVideoData().getWidth();
	}

	@Transient
	public int getHeight() {
		return getVideoData().getHeight();
	}

	@Transient
	public long getDuration() {
		return getVideoData().getDuration();
	}

	@Transient
	public int getBps() {
		return getVideoData().getBps();
	}

	@Transient
	public String getVideoCodec() {
		return getVideoData().getVideoCodec();
	}

	@Transient
	public float getFps() {
		return getVideoData().getFps();
	}

	@Transient
	public String getAudioCodec() {
		return getVideoData().getAudioCodec();
	}

	@Transient
	public int getSamplingRate() {
		return getVideoData().getSamplingRate();
	}

	@Transient
	public boolean isStereo() {
		return getVideoData().isStereo();
	}

}