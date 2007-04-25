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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.file;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;

/**
 * Default FileStore implementation.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class DefaultFileStore implements FileStore, ServletContextAware,
		InitializingBean {

	private Log log = LogFactory.getLog(DefaultFileStore.class);
	
	private String uriPrefix;
	
	private String storagePath;
	
	private File baseDir;

	private File storageDir;
	
	private int storageDirIndex;
	
	private int maxFilesPerDir = 500;
	
	private ServletContext servletContext;
		
	
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * Sets the path to the directory where the files are stored. Relative
	 * paths are resolved against the webapp's root directory by calling
	 * {@link ServletContext#getRealPath(String)}.
	 * 
	 * @param storagePath Either an absolute or relative path denoting a directory
	 */
	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	/**
	 * Sets a prefix that is added to all URIs returned by the 
	 * {@link #store(File, String) store()} method.  
	 */
	public void setUriPrefix(String uriPrefix) {
		Assert.notNull(uriPrefix, "The uriPrefix must not be null");
		if (uriPrefix.endsWith("/")) {
			uriPrefix = uriPrefix.substring(0, uriPrefix.length() - 1);
		}
		this.uriPrefix = uriPrefix;
	}
	
	/**
	 * Sets the maximum number of files that may be stored in a directory
	 * before a new storageDir is created. Defaults to 500.
	 */
	public void setMaxFilesPerDir(int maxFilesPerDir) {
		this.maxFilesPerDir = maxFilesPerDir;
	}
	
	/**
	 * Creates the baseDir after all properties have been set.
	 */
	public void afterPropertiesSet() throws IOException {
		Assert.notNull(uriPrefix, "The uriPrefix must not be null");
		if (storagePath == null) {
			storagePath = servletContext.getRealPath(uriPrefix);
		}
		else if (!storagePath.startsWith(File.separator) 
				&& storagePath.indexOf(":") != 1) {
			
			storagePath = servletContext.getRealPath(storagePath);
		}
		
		baseDir = createDir(new File(storagePath));	
		log.info("Files will be stored in " 
				+ baseDir.getCanonicalPath());
		
		getStorageDir();
	}
	
	/**
	 * Creates the given directory and all parent directories (unless they 
	 * already exist). If the directory can't be created or is not writable
	 * an error message is logged. 
	 */
	protected File createDir(File dir) {
		if (!(dir.exists() || dir.mkdirs())) {
			log.error("Error creating directory: " + dir.getPath());
		}
		if (!dir.canWrite()) {
			log.error("Directory " + dir.getPath() 
					+ " is not writable for user " 
					+ System.getProperty("user.name"));
		}
		return dir;
	}
	
	/**
	 * Returns whether the next storage directory should be used.
	 */
	private boolean storageExceeded() {
		return storageDir == null || storageDir.list().length >= maxFilesPerDir;
	}

	/**
	 * Returns the directory where the files should be stored. The default
	 * implementation limits the number of files per directory and creates a
	 * new directory when the number of files exceeds the 
	 * {@link #setMaxFilesPerDir(int) maxFilesPerDir} value.
	 */
	protected File getStorageDir() {
		if (storageExceeded()) {
			synchronized (this) {
				while (storageExceeded()) {
					String name = String.valueOf(storageDirIndex++);
					storageDir = createDir(new File(baseDir, name));
				}	
			}
		}
		return storageDir;
	}
	
	/**
	 * Returns an empty new directory with an unique name within the current
	 * storageDir. 
	 */
	protected File getUniqueDir() {
		File parent = getStorageDir();
		for (int i = 0; i < maxFilesPerDir; i++) {
			File dir = new File(parent, String.valueOf(
					System.currentTimeMillis()) + i);
			
			if (!dir.exists()) {
				dir.mkdir();
				return dir;
			}
		}
		//This should never happen ...
		throw new RuntimeException("Failed to create a unique directory name.");
	}
	
	/**
	 * Moves the given file into the store and returns an URI that can be
	 * used to request the file via HTTP.
	 */
	public String store(File file, String fileName)	throws IOException {
		if (fileName == null) {
			fileName = file.getName();
		}
		File dest = new File(getUniqueDir(), fileName); 
		if (!file.renameTo(dest)) {
			FileCopyUtils.copy(file, dest);
			file.delete();
		}
		String path = dest.getPath().substring(baseDir.getPath().length());
		path = StringUtils.replace(path, File.separator, "/");
		return uriPrefix + path;
	}
	
	/**
	 * Retrieves a file from the store that was previously added via the
	 * {@link #store(File, String) store()} method. 
	 */
	public File retrieve(String uri) {
		log.debug("Retrieving file for URI: " + uri);
		if (!uri.startsWith(uriPrefix)) {
			return null;
		}
		uri = stripQueryString(uri.substring(uriPrefix.length() + 1));
		uri = StringUtils.replace(uri, "/", File.separator);
		File file = new File(baseDir, uri);
		log.debug("File: " + file);
		return file;
	}
	
	/**
	 * Strips the query string from the given URI. Older FileStore 
	 * implementations had the option to append a timestamp parameter to the
	 * URI therefore this method is used to ensure backwards compatibility. 
	 */
	private String stripQueryString(String uri) {
		int i = uri.indexOf('?');
		if (i != -1) {
			uri = uri.substring(0, i);
		}
		return uri;
	}

	/**
	 * Deletes the file denoted by the given URI from the store.
	 */
	public void delete(String uri) {
		File file = retrieve(uri);
		file.delete();
		File dir = file.getParentFile();
		if (dir.isDirectory() && dir.list().length == 0 &&
			!dir.equals(baseDir)) {
			
			dir.delete();
		}
	}
	
}
