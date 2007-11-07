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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.editor;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.image.ImageCropper;
import org.riotfamily.common.util.PasswordGenerator;
import org.riotfamily.common.web.file.FileStore;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class UploadManagerImpl implements UploadManager {

	private static Log log = LogFactory.getLog(UploadManagerImpl.class);
	
	private PasswordGenerator tokenGenerator = 
			new PasswordGenerator(16, true, true, true);
	
	private Set validTokens = Collections.synchronizedSet(new HashSet());

	private FileStore fileStore;
	
	private ImageCropper imageCropper;
	
	public UploadManagerImpl(FileStore fileStore, ImageCropper imageCropper) {
		this.fileStore = fileStore;
		this.imageCropper = imageCropper;
	}

	public String generateToken() {
		String token = tokenGenerator.generate();
		validTokens.add(token);
		log.debug("Generated token: " + token);
		return token;
	}
	
	boolean isValidToken(String token) {
		boolean valid = validTokens.contains(token);
		log.debug((valid ? "Valid" : "Invalid") + " token: " + token);
		return valid;
	}
	
	public void invalidateToken(String token) {
		validTokens.remove(token);
	}
	
	String storeFile(String token, File file, String originalFileName) 
			throws IOException {
		
		String path = fileStore.store(file, originalFileName);
		log.debug("File uploaded - token: " + token + ", path: " + path);
		return path;
	}
	
	public String cropImage(String path, int width, int height, int x, int y, 
			int scaledWidth) throws IOException {
		
		File src = fileStore.retrieve(path);
		File dest = File.createTempFile("crop", ".img");
		imageCropper.cropImage(src, dest, width, height, x, y, scaledWidth);
		fileStore.delete(path);
		return fileStore.store(dest, src.getName());
	}
}
