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
package org.riotfamily.media.store;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.servlet.ServletContext;

import org.riotfamily.common.io.RecursiveFileIterator;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.RandomStringGenerator;
import org.riotfamily.common.util.RandomStringGenerator.Chars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;

/**
 * Default FileStore implementation.
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class DefaultFileStore implements FileStore, ServletContextAware, 
		InitializingBean {

	private Logger log = LoggerFactory.getLogger(DefaultFileStore.class);
	
	private String uriPrefix;
	
	private String storagePath;
	
	private File baseDir;

	private File storageDir;
	
	private int storageDirIndex = 0;
	
	private int maxFilesPerDir = 500;
	
	private RandomStringGenerator dirNameGenerator = 
			new RandomStringGenerator(14, true, Chars.DIGITS);
	
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
	 * already exist). If the directory can't be created an IOException is
	 * thrown.
	 */
	protected File createDir(File dir) throws IOException {
		if (dir.exists()) {
			return dir;
		}
		if (!dir.mkdirs()) {
			throw new IOException("Can't create directory " + dir.getPath()
					+ " as user " + System.getProperty("user.name"));
		}
		return dir;
	}
	
	/**
	 * Returns whether the next storage directory should be used.
	 */
	private boolean shouldUseNewStorageDir() {
		return storageDir == null || !storageDir.exists() 
				|| storageDir.list().length >= maxFilesPerDir;
	}

	/**
	 * Returns the directory where the files should be stored. The default
	 * implementation limits the number of files per directory and creates a
	 * new directory when the number of files exceeds the 
	 * {@link #setMaxFilesPerDir(int) maxFilesPerDir} value.
	 */
	protected File getStorageDir() throws IOException {
		if (shouldUseNewStorageDir()) {
			synchronized (this) {
				while (shouldUseNewStorageDir()) {
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
	protected File getUniqueDir() throws IOException {
		File parent = getStorageDir();
		for (int i = 0; i < maxFilesPerDir; i++) {
			File dir = new File(parent, dirNameGenerator.generate());
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					throw new IOException("Can't create directory " + dir.getPath()
							+ " as user " + System.getProperty("user.name"));
				}
				return dir;
			}
		}
		//This should never happen ...
		throw new RuntimeException("Failed to create a unique directory name.");
	}
	
	public String store(InputStream in, String fileName) throws IOException {
		File dest = new File(getUniqueDir(), FormatUtils.toFilename(fileName));
		if (in != null) {
			FileCopyUtils.copy(in, new FileOutputStream(dest));
			log.debug("stored at: " + dest.getAbsolutePath());
		}
		else {
			dest.createNewFile();
		}
		return getUri(dest);
	}
	
	public String getUri(File file) {
		String path = file.getPath();
		if (path.startsWith(storagePath)) {
			path = path.substring(storagePath.length());
			path = StringUtils.replace(path, File.separator, "/");
			return uriPrefix + path;
		}
		return null;
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
	
	public Iterator<String> iterator() {
		return new FileUriIterator();
	}
	
	private class FileUriIterator implements Iterator<String> {
		
		private RecursiveFileIterator it = new RecursiveFileIterator(baseDir);
		
		public boolean hasNext() {
			return it.hasNext();
		}
		
		public String next() {
			return getUri(it.next());
		}
		
		public void remove() {
			it.remove();
		}
	}
	
}
