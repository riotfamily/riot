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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Carsten Woelk [cwoelk at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.media.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.riotfamily.common.image.ImageMagick;
import org.riotfamily.common.image.ImageMagickThumbnailer;
import org.riotfamily.common.image.Thumbnailer;

public class FfmpegThumbnailer implements Thumbnailer {

	private FFmpeg ffmpeg;

	private ImageMagickThumbnailer thumbnailer;
	
	public FfmpegThumbnailer(FFmpeg ffmpeg, ImageMagick imageMagick) {
		this.ffmpeg = ffmpeg;
		thumbnailer = new ImageMagickThumbnailer(imageMagick);		
	}
	
	public void renderThumbnail(File source, File dest, int width, int height,
			boolean fixedSize, String backgroundColor) throws IOException {

		File frame = File.createTempFile("frame", ".jpg", dest.getParentFile());
		
		ArrayList<String> args = new ArrayList<String>();
		args.add("-i");
		args.add(source.getAbsolutePath());
		args.add("-an");
		args.add("-ss");
		args.add("00:00:10");
		args.add("-vframes");
		args.add("1");
		args.add("-f");
		args.add("mjpeg");
		args.add("-y");
		args.add(frame.getAbsolutePath());
		ffmpeg.invoke(args);
		
		thumbnailer.renderThumbnail(frame, dest, width, height, fixedSize, backgroundColor);
		frame.delete();
	}

}
