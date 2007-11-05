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
package org.riotfamily.cachius;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class CacheFactory {
	
	public static final int DEFAULT_CAPACITY = 10000;

	public static final String DEFAULT_CACHE_DIR_NAME = "cache";
	
	private static final String CACHE_FILE = "cache-info";

	private static Log log = LogFactory.getLog(CacheFactory.class);
	
	private int capacity = DEFAULT_CAPACITY;

	private File cacheDir;

	private boolean restore = true;
	
	private boolean enabled = true;

	/**
	 * Sets the capacity of the Cache. If not set, the capacity will default
	 * to <code>DEFAULT_CAPACITY</code> (10000).
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public void setCacheDir(File cacheDir) {
		this.cacheDir = cacheDir;
	}
	
	protected File getCacheDir() {
		return this.cacheDir;
	}
	
	/**
	 * Sets whether the factory should try to restore a previously persisted
	 * version of the cache. Default is <code>true</code>.
	 */
	public void setRestore(boolean restore) {
		this.restore = restore;
	}
	
	public boolean isRestore() {
		return this.restore;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Cache createInstance() throws Exception {
		Cache cache = null;
		if (!cacheDir.exists() && !cacheDir.mkdirs()) {
		    throw new IOException("Can't create cache directory: " + cacheDir);
		}
		File f = new File(cacheDir, CACHE_FILE);
		if (restore && f.exists()) {
		    log.info("Trying to build cache from file: " + f);
		    cache = deserialize(f);
		}
		
		if (cache == null) {
			cache = new Cache(cacheDir, capacity, enabled);
		}
		else {
	        cache.setCacheDir(cacheDir);
	        cache.setCapacity(capacity);
	        cache.setEnabled(enabled);
		}
		return cache;
	}
	
	protected void persist(Cache cache) {
        File f = new File(cacheDir, CACHE_FILE);
        if (!f.exists()) {
            try {
                 log.info("Persisting the cache state ...");
                 ObjectOutputStream out = new ObjectOutputStream(
                         new FileOutputStream(f));

                 out.writeObject(this);
                 out.close();
                 log.info("Cache state saved in " + f);
            }
            catch (IOException e) {
                log.error("Can't save cache state", e);
            }
        }
	}
	
	protected Cache deserialize(File f) {
		try {
	        ObjectInputStream in = new ObjectInputStream(
	                 new FileInputStream(f));

	        Cache cache = (Cache) in.readObject();
	        in.close();
	        log.info("Cache has been successfully deserialized.");
	        
	        return cache;
	    }
	    catch (InvalidClassException e) {
	    	log.info("Serialized cache has been discarded due to " +
	    			"version incompatibilies.");
	    }
	    catch (IOException e) {
	        log.warn("Deserialization failed.");
	    }
	    catch (ClassNotFoundException e) {
	        log.warn("Deserialization failed.", e);
	    }
	    finally {
    		f.delete();
	    }
	    return null;
	}
}
