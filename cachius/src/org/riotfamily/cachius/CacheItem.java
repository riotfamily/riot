package org.riotfamily.cachius;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Container that holds information about the liveness as well as the actual data.
 */
public class CacheItem implements Serializable {

	private Serializable data;
	
	/** Time of the last modification */
    private long lastModified;
    
    /** Time when the item will expire */
    private long expires = -1;
    
    /** Whether the item has been invalidated */
    private boolean invalidated;
    
    /** Whether the content contains an error */
    private boolean error;
    
    /** Whether this item should be served as fallback in case of an error */
    private boolean serveStaleOnError;
    
    /** Whether this item should be served while another thread updates the content */ 
    private boolean serveStaleWhileRevalidate;
    
    /** Whether the expires date should be respected, even if invalidated */
    private boolean serveStaleUntilExpired;
    
    /** Set of tags to categorize the item */
    private Set<String> tags;
    
    /** Set of files involved in the creation of the cached data */
    private Set<File> involvedFiles;
    
    public CacheItem() {
    	this.lastModified = System.currentTimeMillis();
    }
    
    public CacheItem(CacheItem old) {
    	this();
    	this.data = old.data;
    }
    
    /**
     * Returns the last modification time.
     */
	public long getLastModified() {
        return lastModified;
    }
  
    public Serializable getData() {
		return data;
	}

	public void setData(Serializable data) {
		this.data = data;
	}

	public void setTimeToLive(long ttl) {
		setExpires(ttl == -1 ? -1 : (System.currentTimeMillis() + ttl));
	}
    
    public void setExpires(long expires) {
		this.expires = expires;
	}
    
	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	/**
	 * Invalidates the item.
	 */
	public void invalidate() {
    	this.invalidated = true;
    }

	public boolean isInvalidated() {
		return invalidated;
	}
	
	public boolean isExpired() {
		long now = System.currentTimeMillis();
    	return (expires == -1 && invalidated) 
    		|| (expires >= 0 && now >= expires);
    }
	
	public boolean isUpToDate(CacheHandler handler) {
		if (data == null) {
			return false;
		}
		if (isExpired() || (isInvalidated() && !serveStaleUntilExpired)) {
			// Expired or invalidated, ask handler
			if (handler.getLastModified() > lastModified) {
				return false;
			}
		}
		return !anyFileModified();
	}
	
	/**
	 * Adds the given file.
	 * @throws AssertionError if the file is <code>null</code>
	 */
	public void addInvolvedFile(File file) {
		assert file != null : "File must not be null.";
		if (involvedFiles == null) {
			involvedFiles = new HashSet<File>();
		}
		involvedFiles.add(file);
	}
	
	public void addInvolvedFiles(Collection<File> files) {
		if (files != null) {
			for (File file : files) {
				addInvolvedFile(file);
			}
		}
	}
		
    public boolean anyFileModified() {
    	if (involvedFiles != null) {
    		for (File file : involvedFiles) {
    			if (file.lastModified() > lastModified) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    /**
	 * Adds the given tag.
	 * @throws AssertionError if the tag is <code>null</code>
	 */
	public void addTag(String tag) {
		assert tag != null : "Tag must not be null.";
		if (tags == null) {
			tags = new HashSet<String>();
		}
		tag = tag.intern();
		tags.add(tag);
	}

	public void addTags(Collection<String> tags) {
		if (tags != null) {
			for (String tag : tags) {
				addTag(tag);
			}
		}
	}
	
    /**
	 * Returns the item's tags.
	 */
	public Set<String> getTags() {
		return this.tags;
	}
	
	public void addAll(CacheItem child) {
		addTags(child.tags);
		addInvolvedFiles(child.involvedFiles);
		if (child.error) {
			error = true;
		}
		if (child.expires < expires) {
			expires = child.expires;
		}
	}
	
	public void delete() {
		//TODO
	}

	public void serveStaleUntilExpired() {
		serveStaleUntilExpired = true;
	}

	public void serveStaleOnError() {
		serveStaleOnError = true;
	}
	
	public boolean isServeStaleOnError() {
		return serveStaleOnError;
	}

	public void serveStaleWhileRevalidate() {
		serveStaleWhileRevalidate = true;
	}
	
	public boolean isServeStaleWhileRevalidate() {
		return serveStaleWhileRevalidate;
	}
	
}
