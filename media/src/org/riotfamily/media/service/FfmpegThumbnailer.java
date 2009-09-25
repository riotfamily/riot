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
import java.util.ArrayList;

import org.riotfamily.media.processing.ImageMagick;
import org.riotfamily.media.processing.ImageMagickThumbnailer;
import org.riotfamily.media.processing.Thumbnailer;

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
