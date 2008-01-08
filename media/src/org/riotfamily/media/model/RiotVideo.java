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

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class RiotVideo extends RiotFile {

	public RiotVideo() {
		super();
	}

	public RiotVideo(VideoData data) {
		super(data);
	}
	
	public RiotFile createCopy() {
		return new RiotVideo(getVideoData());
	}
	
	public VideoData getVideoData() {
		return (VideoData) getFileData();
	}
	
	public int getWidth() {
		return getVideoData().getWidth();
	}

	public int getHeight() {
		return getVideoData().getHeight();
	}

	public long getDuration() {
		return getVideoData().getDuration();
	}

	public int getBps() {
		return getVideoData().getBps();
	}

	public String getVideoCodec() {
		return getVideoData().getVideoCodec();
	}

	public float getFps() {
		return getVideoData().getFps();
	}

	public String getAudioCodec() {
		return getVideoData().getAudioCodec();
	}

	public int getSamplingRate() {
		return getVideoData().getSamplingRate();
	}

	public boolean isStereo() {
		return getVideoData().isStereo();
	}

}