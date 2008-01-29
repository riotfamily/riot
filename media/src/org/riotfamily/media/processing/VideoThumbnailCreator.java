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
package org.riotfamily.media.processing;

import java.util.ArrayList;

import org.riotfamily.common.io.CommandUtils;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.model.RiotVideo;
import org.riotfamily.media.model.data.FileData;
import org.riotfamily.media.model.data.VideoData;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class VideoThumbnailCreator extends AbstractFileProcessor {

	private int width;
	
	private int height;
	
	private boolean fill;
	
	
	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setFill(boolean fill) {
		this.fill = fill;
	}

	protected RiotFile createVariant(FileData data) throws Exception {
		VideoData video = (VideoData) data;
		VideoData thumb = new VideoData();
		ArrayList args = new ArrayList();
		args.add("ffmpeg");
		args.add("-i");
		args.add(video.getFile().getAbsolutePath());
		args.add("-an");
		args.add("-ss");
		args.add("00:00:10");
		args.add("-vframes");
		args.add("1");
		args.add("-s");
		double w = video.getWidth();
		double h = video.getHeight();
		double scaleX = (double) width / w; 
		double scaleY = (double) height / h;
		double scale = Math.min(Math.min(scaleX, scaleY), 1);
		int scaledWidth = (int) (w * scale);
		if (scaledWidth % 2 == 1) {
			scaledWidth--;
		}
		int scaledHeight = (int) (h * scale);
		if (scaledHeight % 2 == 1) {
			scaledHeight--;
		}
		args.add(scaledWidth + "x" + scaledHeight);
		if (fill) {
			args.add("-padtop");
			args.add(String.valueOf(Math.ceil((double) (height - scaledHeight) / 2)));
			args.add("-padright");
			args.add(String.valueOf(Math.ceil((double) (height - scaledHeight) / 2)));
			args.add("-padbottom");
			args.add(String.valueOf(Math.floor((double) (height - scaledHeight) / 2)));
			args.add("-padleft");
			args.add(String.valueOf(Math.ceil((double) (width - scaledWidth) / 2)));
			args.add("-padcolor");
			args.add("000000");
		}
		args.add("-f");
		args.add("mjpeg");
		args.add("-y");
		String thumbName = FormatUtils.stripExtension(data.getFileName()) + ".jpg";
		args.add(thumb.createEmptyFile(thumbName).getAbsolutePath());
		CommandUtils.exec(args);
		thumb.update();
		return new RiotVideo(thumb);
	}
	
}
