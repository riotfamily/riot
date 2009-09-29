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
package org.riotfamily.cachius;

import java.io.File;


/**
 */
public class CacheContext {
	
	private static ThreadLocal<CacheItem> currentItem = new ThreadLocal<CacheItem>();
	
	static CacheItem getItem() {
		return currentItem.get();
	}

	static void setItem(CacheItem item) {
		currentItem.set(item);
	}
	
	public static void tag(String tag) {
		CacheItem item = currentItem.get();
		if (item != null) {
			item.addTag(tag);
		}
	}
	
	public static void addFile(File file) {
		CacheItem item = currentItem.get();
		if (item != null) {
			item.addInvolvedFile(file);
		}
	}
	
	public static void error() {
		CacheItem item = currentItem.get();
		if (item != null) {
			item.setError(true);
		}
	}
	
	public static void expireIn(long millis) {
		CacheItem item = currentItem.get();
		if (item != null) {
			item.setExpires(System.currentTimeMillis() + millis);
		}
	}
	
	public static void serveStaleOnError() {
		CacheItem item = currentItem.get();
		if (item != null) {
			item.serveStaleOnError();
		}
	}
	
	public static void serveStaleWhileRevalidate() {
		CacheItem item = currentItem.get();
		if (item != null) {
			item.serveStaleWhileRevalidate();
		}
	}
	
	public static void serveStaleUntilExpired() {
		CacheItem item = currentItem.get();
		if (item != null) {
			item.serveStaleUntilExpired();
		}
	}

	public static boolean exists() {
		return currentItem.get() != null;
	}
}
