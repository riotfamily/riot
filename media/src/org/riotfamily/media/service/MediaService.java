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
package org.riotfamily.media.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.FileTypeMap;

import org.riotfamily.media.store.FileStore;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class MediaService {
	
	private FileStore fileStore;
	
	private FileTypeMap fileTypeMap;
	
	private FFmpeg ffmpeg;

	public MediaService(FileStore fileStore, FileTypeMap fileTypeMap, FFmpeg ffmpeg) {
		this.fileStore = fileStore;
		this.fileTypeMap = fileTypeMap;
		this.ffmpeg = ffmpeg;
	}

	public void delete(String uri) {
		this.fileStore.delete(uri);
	}

	public File retrieve(String uri) {
		return this.fileStore.retrieve(uri);
	}

	public String store(InputStream in, String fileName) throws IOException {
		return this.fileStore.store(in, fileName);
	}
	
	public String getContentType(File file) {
		return fileTypeMap.getContentType(file);
	}
	
	public VideoMetaData identifyVideo(File file) throws IOException {
		return ffmpeg.identify(file);
	}

}
