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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.file;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.util.FormatUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;

public class DefaultFileStore implements FileStore, ServletContextAware,
		InitializingBean {

	private Log log = LogFactory.getLog(DefaultFileStore.class);
	
	private String uriPrefix;
	
	private String storagePath;
	
	private File storageDir;

	private ServletContext servletContext;
	
	private boolean overwriteFiles = true;
	
	private boolean deleteOldFiles = true;
	
	private boolean appendTimestampParam = true;
	
	
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	public void setUriPrefix(String prefix) {
		this.uriPrefix = prefix; 
	}
	
	public void setOverwriteFiles(boolean overwriteFiles) {
		this.overwriteFiles = overwriteFiles;
	}
	
	public void setDeleteOldFiles(boolean deleteOldFiles) {
		this.deleteOldFiles = deleteOldFiles;
	}

	public void setAppendTimestampParam(boolean appendTimestampParam) {
		this.appendTimestampParam = appendTimestampParam;
	}

	public void afterPropertiesSet() throws IOException {
		if (storagePath != null) {
			boolean absolutePath = storagePath.startsWith(File.separator) 
					|| storagePath.indexOf(":") == 1;
			
			if (!absolutePath) {
				storagePath = servletContext.getRealPath(storagePath);
			}
			storageDir = new File(storagePath);	
			
		}
		if (uriPrefix == null) {
			uriPrefix = "/media";
		}
		else if (uriPrefix.endsWith("/")) {
			uriPrefix = uriPrefix.substring(0, uriPrefix.length() - 1);
		}
		if (storageDir == null) {
			storageDir = new File(servletContext.getRealPath(uriPrefix));
		}
		
		if (!storageDir.exists() && !storageDir.mkdirs()) {
			log.error("Error creating directory: " 
					+ storageDir.getCanonicalPath());
		}
		else if (!storageDir.canWrite()) {
			log.error("Directory " + storageDir.getCanonicalPath() 
					+ " is not writeable for user " 
					+ System.getProperty("user.name"));
		}
		else {
			log.info("Files will be stored in " 
					+ storageDir.getCanonicalPath());
		}
	}
	
	public String store(File file, String originalFileName, String previousUri) 
			throws IOException {
		
		String ext = FormatUtils.getExtension(originalFileName);
		
		File dest = null;
		if (previousUri != null) {
			File oldFile = retrieve(previousUri); 
			if (overwriteFiles && FormatUtils.getExtension(
					oldFile.getName()).equals(ext)) {
				
				dest = oldFile;	
			}
			else if (deleteOldFiles) {
				oldFile.delete();
			}
		}
		
		storageDir.mkdirs();
		if (!storageDir.canWrite()) {
			throw new IOException("The directory " 
					+ storageDir.getAbsolutePath()
					+ " is not writeable for user " 
					+ System.getProperty("user.name"));
		}
		
		if (dest == null) {
			if (StringUtils.hasLength(ext)) {
				ext = '.' + ext;
			}
			dest = File.createTempFile("000", ext, storageDir);
		}
		
		if (!file.renameTo(dest)) {
			FileCopyUtils.copy(file, dest);
			file.delete();
		}
		return getUri(dest);
	}
	
	protected String getUri(File f) {
		StringBuffer uri = new StringBuffer();
		if (uriPrefix != null) {
			uri.append(uriPrefix);
		}
		uri.append('/').append(f.getName());
		
		if (overwriteFiles && appendTimestampParam) {
			uri.append("?").append(System.currentTimeMillis());
		}
		return uri.toString();
	}
	
	public File retrieve(String uri) {
		log.debug("Retrieving file for URI: " + uri);
		if (uriPrefix != null) {
			if (!uri.startsWith(uriPrefix)) {
				return null;
			}
			uri = uri.substring(uriPrefix.length() + 1);
		}
		
		int i = uri.indexOf('?');
		if (i != -1) {
			uri = uri.substring(0, i);
		}
		
		File file = new File(storageDir, uri);
		log.debug("File: " + file);
		return file;
	}

	public void delete(String uri) {
		retrieve(uri).delete();
	}
	
	public String copy(String uri) throws IOException {
		File f = retrieve(uri);
		String ext = FormatUtils.getExtension(f.getName());
		File dest = File.createTempFile("000", "." + ext, storageDir);
		FileCopyUtils.copy(f, dest);
		return getUri(dest);
	}
}
