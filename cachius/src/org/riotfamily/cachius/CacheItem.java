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
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.cachius.support.ReaderWriterLock;
import org.riotfamily.cachius.support.TokenFilterWriter;
import org.riotfamily.common.io.IOUtils;


/**
 * Representation of cached item that is backed by a file. The 
 * <code>lastModified</code> and the <code>contentType</code> properties
 * are kept in memory and are serialized by the default java serialization
 * mechanism. The actual content is read from a file to avoid the overhead
 * of object deserialization on each request.
 * <br>
 * If URL rewriting is used to track the session, the sessionId is
 * replaced by a special token in the cache file. When such an item is
 * send to a client, the token is replaced with the current sessionId.
 * <br>
 * If the sessionId comes from a cookie or the controller outputs binary data,
 * a direct stream copy is performed instead of using Readers/Writers to 
 * improve performance.
 *
 * @author Felix Gnass
 */
public class CacheItem implements Serializable {
    
	private static final String HEADER_EXPIRES = "Expires";
	
	private static final String HEADER_CACHE_CONTROL = "Cache-Control";
	
    private static final String ITEM_PREFIX = "item";
    
    private static final String ITEM_SUFFIX = "";
                   
    private static final long NOT_YET = -1L;
    
    private static final String FILE_ENCODING = "UTF-8";
    
    private Log log = LogFactory.getLog(CacheItem.class);
    
    /** The previous item in the linked list */
    private transient CacheItem previous;
    
    /** The next item in the linked list */
    private transient CacheItem next;
    
    /** The key used for lookups */
    private String key;
    
    /** List of tags to categorize the item */
    private String[] tags;
    
    /** Flag indicating whether session IDs are filtered */  
    private boolean filterSessionId;
    
    /** The file containing the actual data */
    private File file = null;
    
    /** File containing the gzipped data */
    //private File compressedFile = null;
    
    /** The content type (may be null) */
    private String contentType = null;
    
    /**
     * The Cache-Control header to be sent (may be null).
     */
    private String cacheControl;
    
    /**
     * The Expires header to be sent.
     */
    private long expires = -1;
    
    
    /** 
     * Flag indicating whether the cached content is binary 
     * or character data.
     */
    private boolean binary = true;
    
    /** 
     * Reader/writer lock to prevent concurrent threads from updating
     * the cached content while others are reading.
     */
    private transient ReaderWriterLock lock = new ReaderWriterLock();
    
    /**
     * Time of the last modification.
     */
    private long lastModified;
    
    /**
     * Time of the last up-to-date check.
     */
    private long lastCheck;
    
    
    public CacheItem(String key, File cacheDir) throws IOException {
        this.key = key;
        file = File.createTempFile(ITEM_PREFIX, ITEM_SUFFIX, cacheDir);
        lastModified = NOT_YET;
    }
        
    public String getKey() {
        return key;
    }
   
    public void setKey(String key) {
        this.key = key;
    }
    
	protected ReaderWriterLock getLock() {
		return this.lock;
	}
	
    public void setTags(String[] tags) {
    	if (tags != null) {
    		Arrays.sort(tags);
    	}
    	this.tags = tags;
    }
    
    public String[] getTags() {
    	return tags;
    }
    
    public boolean hasTag(String tag) {
    	return tags != null && Arrays.binarySearch(tags, tag) >= 0;
    }
    
    public CacheItem getPrevious() {
        return previous;
    }
   
    public void setPrevious(CacheItem previous) {
        this.previous = previous;
    }

    public CacheItem getNext() {
        return next;
    }
   
    public void setNext(CacheItem next) {
        this.next = next;
    }
    
    public int hashCode() {
        return key.hashCode();
    }

    public boolean isFilterSessionId() {
		return filterSessionId;
	}
	
	public void setFilterSessionId(boolean filterSessionId) {
		this.filterSessionId = filterSessionId;
	}
	
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
        
    public void setCacheControl(String cacheControl) {
		this.cacheControl = cacheControl;
	}

	public void setExpires(long expires) {
		this.expires = expires;
	}

	public long getLastModified() {
        return lastModified;
    }
  
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getLastCheck() {
		return this.lastCheck;
	}

	public void setLastCheck(long lastCheck) {
		this.lastCheck = lastCheck;
	}

	public boolean isNew() {
        return lastModified == NOT_YET;
    }
    
    /**
     * Checks whether the cache file exists an is a regular file.
     */
    public boolean exists() {
        return file != null && file.isFile();
    }
    
    public OutputStream getOutputStream() throws FileNotFoundException {
    	return new FileOutputStream(file);
    }
    
    public Writer getWriter() throws UnsupportedEncodingException, FileNotFoundException {
    	binary = false;
    	return new OutputStreamWriter(getOutputStream(), FILE_ENCODING);
    }

    
    public void writeTo(HttpServletResponse response, String sessionId) 
    		throws IOException {
            
        try {
            if (contentType != null) {
                response.setContentType(contentType);
            }
            if (expires >= 0) {
            	response.setDateHeader(HEADER_EXPIRES, expires);
            }
            if (cacheControl != null) {
            	response.setHeader(HEADER_CACHE_CONTROL, cacheControl);
            }
            
            if (file.length() > 0) {
	            if (binary) {
	                InputStream in = new BufferedInputStream(
	                        new FileInputStream(file));
	                        
	                IOUtils.copy(in, response.getOutputStream());
	            }
	            else {
	                Reader in = new BufferedReader(new InputStreamReader(
	                        new FileInputStream(file), FILE_ENCODING));
	                
	                Writer out = response.getWriter();
	                if (filterSessionId) {
	                    out = new TokenFilterWriter("${jsessionid}", 
	                            sessionId, out);
	                }
	                
	                IOUtils.copy(in, out);
	            }
            }
        }
        catch (FileNotFoundException e) {
            log.warn("Cache file not found. Resetting modification time " +
                    "to trigger update on next request.");
            
            lastModified = NOT_YET;
        }
    }
    
    public void invalidate() {
    	lastModified = NOT_YET;
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
    }
  
}
