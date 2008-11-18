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

import org.riotfamily.cachius.support.Cookies;
import org.riotfamily.cachius.support.Headers;
import org.riotfamily.common.io.IOUtils;
import org.riotfamily.common.log.RiotLog;
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
    
    private transient RiotLog log = RiotLog.get(CacheItem.class);
    
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
    
    /** Time of the last modification */
    private long lastModified;
    
    /** Time when the item will expire */
    private long expires;
    
    /** Whether the item has been invalidated */
    private boolean invalidated;
    
    /** Whether to set Content-Length header or not */
	private boolean setContentLength;
    
    
    /**
     * Creates a new CacheItem in the specified directory.
     */
    protected CacheItem(File cacheDir) throws IOException {
        file = File.createTempFile(ITEM_PREFIX, ITEM_SUFFIX, cacheDir);
        lastModified = NOT_YET;
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
    
    /**
     * Returns the most recent modification date of the involved files.  
     */
    public long getLastFileModification() {
    	long mtime = -1;
    	if (involvedFiles != null) {
    		for (File file : involvedFiles) {
    			mtime = Math.max(file.lastModified(), mtime);
    			if (log.isDebugEnabled() 
    					&& file.lastModified() > this.lastModified 
    					&& this.lastModified > NOT_YET) {
    				
    				log.debug("File " + file + " has been modified");
    			}
    		}
    	}
    	return mtime;
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
        this.invalidated = false;
    }

	/**
	 * Invalidates the item.
	 */
	public void invalidate() {
    	this.invalidated = true;
    	if (log.isDebugEnabled()) {
    		log.debug(this + " has been invalidated");
    	}
    }
	
	public boolean isInvalidated() {
		return invalidated;
	}
	
	public void setTimeToLive(long ttl) {
		this.expires = ttl == -1 ? -1 : System.currentTimeMillis() + ttl;
	}
	
    public boolean isExpired() {
    	return (expires == -1 && invalidated) 
    		|| (expires >= 0 && System.currentTimeMillis() > expires) 
    		|| !exists(); 
    }

	/**
     * Checks whether the cache file exists, is a regular file and any
     * data has been ever written into it.
     */
	public boolean exists() {
        return file.isFile() && lastModified > NOT_YET;
    }
    
	/**
	 * Returns the size of the cached data in bytes.
	 */
	public int getSize() {
		return (int) file.length();
	}

	protected File getDir() {
		return file.getParentFile();
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
        if (file.exists()) {
    		if (!file.delete()) {
    			log.warn("Failed to delete cache file: " + file);
    		}
    	}
    }
         
    /**
     * Calls <code>in.defaultReadObject()</code> and creates a new log.
     */ 
    private void readObject(ObjectInputStream in) throws IOException, 
            ClassNotFoundException {
         
         in.defaultReadObject();
         log = RiotLog.get(CacheItem.class);
    }
    
}
