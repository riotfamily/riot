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
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import org.riotfamily.common.collection.IteratorChain;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

public class MultiBucketFileStore implements FileStore {

	private FileStore defaultFileStore;
	
	private Map<String, FileStore> buckets;

	@Required
	public void setDefaultFileStore(FileStore defaultFileStore) {
		this.defaultFileStore = defaultFileStore;
	}
	
	@Required
	public void setBuckets(Map<String, FileStore> buckets) {
		this.buckets = buckets;
	}
	
	public Iterator<String> iterator() {
		IteratorChain<String> it = new IteratorChain<String>(defaultFileStore.iterator());
		for (FileStore store : buckets.values()) {
			it.add(store.iterator());
		}
		return it;
	}

	public String store(InputStream in, String fileName, String bucket)	throws IOException {
		if (bucket != null) {
			FileStore store = buckets.get(bucket);
			Assert.notNull(store);
			return store.store(in, fileName, bucket);
		}
		else {
			return defaultFileStore.store(in, fileName, null);
		}
	}

	public File retrieve(String uri) {
		FileStore store = findStore(uri);
		if (store != null) {
			return store.retrieve(uri);
		}
		return null;
	}

	public void delete(String uri) {
		FileStore store = findStore(uri);
		if (store != null) {
			store.delete(uri);
		}
	}

	private FileStore findStore(String uri) {
		if (defaultFileStore.retrieve(uri) != null) {
			return defaultFileStore;
		}
		for (FileStore store : buckets.values()) {
			if (store.retrieve(uri) != null) {
				return store;
			}
		}
		return null;
	}

}
