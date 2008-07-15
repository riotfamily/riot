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
package org.riotfamily.cachius;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.cachius.support.Cookies;
import org.riotfamily.cachius.support.Headers;
import org.riotfamily.cachius.support.ReaderWriterLock;
import org.riotfamily.common.io.IOUtils;
import org.springframework.util.FileCopyUtils;


/**
 * Representation of cached item that is backed by a file. The captured 
 * HTTP headers are kept in memory and are serialized by the default java 
 * serialization mechanism. The actual content is read from a file to avoid 
 * the overhead of object deserialization on each request.
 *
 * @author Felix Gnass
 */
public class CacheItem implements Serializable {
    	
    private static final String ITEM_PREFIX = "item";
    
    private static final String ITEM_SUFFIX = "";
                   
    private static final long NOT_YET = -1L;
    
    private static final String FILE_ENCODING = "UTF-8";
    
    private transient Log log = LogFactory.getLog(CacheItem.class);
    
    /** The key used for lookups */
    private String key;
    
    /** Set of tags to categorize the item */
    private Set<String> tags;
    
    /** The file containing the actual data */
    private File file;

    /** Set of files involved in the creation of the cached data */
    private Set<File> involvedFiles;
    
    /** The Content-Type of the cached data */
    private String contentType;
    
    /** Captured HTTP headers that will be sent */
    private Headers headers;
    
    /** Captured cookies that will be sent */
    private Cookies cookies;
    
    /** Map with extra properties */
    private Map<String, String> properties;
    
    /** Flag indicating whether the content is binary or character data */
    private boolean binary = true;
    
    /** 
     * Reader/writer lock to prevent concurrent threads from updating
     * the cached content while others are reading.
     */
    private transient ReaderWriterLock lock = new ReaderWriterLock();
    
    /** Time of the last usage */
    private long lastUsed;
    
    /** Time of the last modification */
    private long lastModified;
    
    /** Time of the last up-to-date check */
    private long lastCheck;

    /** Whether to set Content-Length header or not */
	private boolean setContentLength;
    
    
    /**
     * Creates a new CacheItem with the given key in the specified directory.
     */
    protected CacheItem(String key, File cacheDir) throws IOException {
        this.key = key;
        file = File.createTempFile(ITEM_PREFIX, ITEM_SUFFIX, cacheDir);
        lastModified = NOT_YET;
    }
        
    /**
     * Returns the key.
     */
    public String getKey() {
        return key;
    }
       
    /**
     * Sets tags which can be used to look up the item for invalidation.
     */
    public void setTags(Set<String> tags) {
    	this.tags = tags != null ? new HashSet<String>(tags) : null;
    	if (log.isDebugEnabled() && tags != null) {
    		for (String tag : tags) {
    			log.debug("Tag: " + tag);
    		}
    	}
    }
    
    /**
	 * Returns the item's tags.
	 */
	public Set<String> getTags() {
		return this.tags;
	}
	
    public void setInvolvedFiles(Set<File> files) {
    	this.involvedFiles = files != null ? new HashSet<File>(files) : null;
    	if (log.isDebugEnabled() && files != null) {
    		for (File file : files) {
    			log.debug("File: " + file.getName());
    		}
    	}
    }
    
    public Set<File> getInvolvedFiles() {
		return this.involvedFiles;
	}
    
    public long getLastFileModified() {
    	long mtime = -1;
    	if (involvedFiles != null) {
    		for (File file : involvedFiles) {
    			mtime = Math.max(file.lastModified(), mtime);
    			if (log.isDebugEnabled() && file.lastModified() > this.lastModified) {
    				log.debug("File " + file + " has been modified");
    			}
    		}
    	}
    	return mtime;
    }
	
	/**
	 * Returns whether the item is new. An item is considered as new if the
	 * {@link #getLastModified() lastModified} timestamp is set to 
	 * {@value #NOT_YET}.  
	 */
	public boolean isNew() {
        return lastModified == NOT_YET;
    }
    
    /**
     * Sets the lastUsed timestamp to the current time.
     */
    protected void touch() {
    	this.lastUsed = System.currentTimeMillis();
    }
    
    /**
	 * Returns the last usage time.
	 */
	public long getLastUsed() {
		return this.lastUsed;
	}
    
    /**
     * Returns the last modification time.
     */
	public long getLastModified() {
        return lastModified;
    }
  
    /**
     * Sets the last modification time.
     */
	public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

	/**
	 * Invalidates the item by setting the {@link #setLastModified(long) 
	 * lastModified} timestamp to {@value #NOT_YET}.
	 */
	public void invalidate() {
    	lastModified = NOT_YET;
    	if (log.isDebugEnabled()) {
    		log.debug(this + " has been invalidated");
    	}
    }
	
	/**
	 * Returns the time when the last up-to-date check was performed.
	 */
	public long getLastCheck() {
		return this.lastCheck;
	}

	/**
	 * Sets the time of the last up-to-date check.
	 */
	protected void setLastCheck(long lastCheck) {
		this.lastCheck = lastCheck;
	}

    /**
     * Checks whether the cache file exists an is a regular file.
     */
	public boolean exists() {
        return file != null && file.isFile();
    }
    
	/**
	 * Returns the size of the cached data in bytes.
	 */
	public int getSize() {
		return file != null ? (int) file.length() : 0;
	}
    
	/**
	 * Sets the Content-Type.
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	/**
	 * Sets whether a Content-Length header should be set.
	 */
	public void setSetContentLength(boolean setContentLength) {
		this.setContentLength = setContentLength;
	}
	
	/**
	 * Sets HTTP headers. 
	 */
	public void setHeaders(Headers headers) {
		this.headers = headers;
	}
	
	public Headers getHeaders() {
		return headers;
	}
	
	/**
	 * Sets cookies.
	 */
	public void setCookies(Cookies cookies) {
		this.cookies = cookies;
	}

	/**
	 * Sets shared properties.
	 * @see org.riotfamily.common.web.collaboration.SharedProperties
	 */
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	/**
	 * Returns the properties.
	 */
	public Map<String, String> getProperties() {
		return properties;
	}
	
	/**
	 * Returns the lock. 
	 */
	protected ReaderWriterLock getLock() {
		return this.lock;
	}
	
    public Writer getWriter() 
    		throws UnsupportedEncodingException, FileNotFoundException {
    	
    	binary = false;
    	return new OutputStreamWriter(getOutputStream(), FILE_ENCODING);
    }

    public OutputStream getOutputStream() throws FileNotFoundException {
    	return new FileOutputStream(file);
    }
    
    public void gzipContent() throws IOException {
    	InputStream in = new BufferedInputStream(new FileInputStream(file));
    	File zipFile = new File(file.getParentFile(), file.getName() + ".gz");
    	OutputStream out = new GZIPOutputStream(new FileOutputStream(zipFile));
    	FileCopyUtils.copy(in, out);
    	binary = true;
    	file.delete();
    	zipFile.renameTo(file);
    }
    
    public void writeTo(HttpServletResponse response) throws IOException {
            
    	if (contentType != null) {
    		response.setContentType(contentType);
    	}
        if (headers != null) {
            headers.addToResponse(response);
        }
        if (cookies != null) {
        	cookies.addToResponse(response);
        }
        int contentLength = getSize();
        if (contentLength > 0) {
        	if (setContentLength) {
        		response.setContentLength(contentLength);
        	}
            if (binary) {
                writeTo(response.getOutputStream());
            }
            else {
                writeTo(response.getWriter());
            }
        }
    }
    
    public void writeTo(OutputStream out) throws IOException {
    	try {
	    	InputStream in = new BufferedInputStream(
	                new FileInputStream(file));
	                
	        IOUtils.serve(in, out);
    	}
        catch (FileNotFoundException e) {
            log.warn("Cache file not found. Invalidating item to trigger " +
                    "an update on the next request.");
            
            invalidate();
        }
    }
    
    public void writeTo(Writer out) throws IOException {
		try {
		    if (getSize() > 0) {
	            Reader in = new BufferedReader(new InputStreamReader(
	                    new FileInputStream(file), FILE_ENCODING));
	            
	            IOUtils.serve(in, out);
		    }
		}
		catch (FileNotFoundException e) {
		    log.warn("Cache file not found. Invalidating item to trigger " +
		            "an update on the next request.");
		    
		    invalidate();
		}
    }

    public void clear() throws IOException {
    	IOUtils.clear(file);
    }

    protected void delete() {
    	try {
            lock.lockForWriting();
            if (file.exists()) {
        		if (!file.delete()) {
        			log.warn("Failed to delete cache file: " + file);
        		}
        	}
        }
        finally {
            lock.releaseWriterLock();
        }
    }
         
    /**
     * Calls <code>in.defaultReadObject()</code> and creates a new lock.
     */ 
    private void readObject(ObjectInputStream in) throws IOException, 
            ClassNotFoundException {
         
         in.defaultReadObject();
         lock = new ReaderWriterLock();
         log = LogFactory.getLog(CacheItem.class);
    }
    
    public int hashCode() {
        return key.hashCode();
    }
    
    public boolean equals(Object obj) {
    	if (obj == this) {
    		return true;
    	}
    	if (obj instanceof CacheItem) {
    		CacheItem other = (CacheItem) obj;
    		return key.equals(other.key);
    	}
    	return false;
    }
    
    public String toString() {
    	return key;
    }

}
