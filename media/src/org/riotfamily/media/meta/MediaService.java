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
package org.riotfamily.media.meta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.FileTypeMap;

import org.riotfamily.media.processing.FFmpeg;
import org.riotfamily.media.store.FileStore;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class MediaService {
	
	private FileStore fileStore;
	
	private FileTypeMap fileTypeMap;
	
	private FFmpeg ffmpeg;
	
	private ImageMagickIdentify imageMagick;

	public MediaService(FileStore fileStore, FileTypeMap fileTypeMap, 
			ImageMagickIdentify imageMagick, FFmpeg ffmpeg) {
		
		this.fileStore = fileStore;
		this.fileTypeMap = fileTypeMap;
		this.imageMagick = imageMagick;
		this.ffmpeg = ffmpeg;
	}

	public void delete(String uri) {
		this.fileStore.delete(uri);
	}

	public File retrieve(String uri) {
		return this.fileStore.retrieve(uri);
	}
	
	public String store(InputStream in, String fileName, String bucket) throws IOException {
		return this.fileStore.store(in, fileName, bucket);
	}
	
	public String getContentType(File file) {
		return fileTypeMap.getContentType(file);
	}
	
	public ImageMetaData identifyImage(File file) throws UnknownFormatException {
		return imageMagick.identify(file);
	}
	
	public ImageMetaData identifySvg(File file) throws UnknownFormatException {
		return imageMagick.identifyWithoutType(file);
	}
	
	public VideoMetaData identifyVideo(File file) throws IOException {
		return ffmpeg.identify(file);
	}

}
