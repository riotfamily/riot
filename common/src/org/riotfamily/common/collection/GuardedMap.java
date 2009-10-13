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
package org.riotfamily.common.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Wrapper around a <code>java.util.Map</code> that throws an 
 * {@link IllegalArgumentException} when a key is added that already 
 * exists in the underlying map.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class GuardedMap<K, V> implements Map<K, V> {

	private Map<K, V> map;

	public static <K, V> GuardedMap<K, V> guard(Map<K, V> map) {
		return new GuardedMap<K, V>(map);
	}

	private GuardedMap(Map<K, V> map) {
		this.map = map;
	}

	private void assertNotPresent(K key) {
		if (map.containsKey(key)) {
			throw new IllegalArgumentException(
					"The map already contains the key '" + key + "': " 
					+ map.get(key));
		}
	}
	
	public V put(K key, V value) {
		assertNotPresent(key);
		return map.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		for (K key: m.keySet()) {
			assertNotPresent(key);
		}
		map.putAll(m);
	}
	
	public void clear() {
		map.clear();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	public V get(Object key) {
		return map.get(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public V remove(Object key) {
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Collection<V> values() {
		return map.values();
	}
	
}
