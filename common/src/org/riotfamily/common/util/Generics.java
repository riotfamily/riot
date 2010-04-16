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
package org.riotfamily.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public final class Generics {

	private Generics() {
	}

	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}

	public static <K, V> HashMap<K, V> newHashMap(
			Map<? extends K, ? extends V> m) {
		return new HashMap<K, V>(m);
	}

	public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
		return new LinkedHashMap<K, V>();
	}

	public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(
			Map<? extends K, ? extends V> m) {
		return new LinkedHashMap<K, V>(m);
	}

	public static <K, V> TreeMap<K, V> newTreeMap() {
		return new TreeMap<K, V>();
	}

	public static <K, V> TreeMap<K, V> newTreeMap(
			Map<? extends K, ? extends V> m) {
		return new TreeMap<K, V>(m);
	}
	
	public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
		return new ConcurrentHashMap<K, V>();
	}
	
	public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(
			Map<? extends K, ? extends V> m) {
		return new ConcurrentHashMap<K, V>(m);
	}

	public static <V> ArrayList<V> newArrayList() {
		return new ArrayList<V>();
	}

	public static <V> ArrayList<V> newArrayList(int initialCapacity) {
		return new ArrayList<V>(initialCapacity);
	}

	public static <V> ArrayList<V> newArrayList(Collection<? extends V> c) {
		return new ArrayList<V>(c);
	}
	
	public static <V> LinkedList<V> newLinkedList() {
		return new LinkedList<V>();
	}

	public static <V> LinkedList<V> newLinkedList(Collection<? extends V> c) {
		return new LinkedList<V>(c);
	}

	public static <V> List<V> newSynchronizedLinkedList() {
		LinkedList<V> list = new LinkedList<V>();
		return Collections.synchronizedList(list);
	}

	public static <V> HashSet<V> newHashSet() {
		return new HashSet<V>();
	}

	public static <V> HashSet<V> newHashSet(Collection<? extends V> c) {
		return new HashSet<V>(c);
	}

	public static <V> Set<V> newSynchronizedHashSet(Collection<? extends V> c) {
		HashSet<V> set = new HashSet<V>(c);
		return Collections.synchronizedSet(set);
	}

	public static <V> LinkedHashSet<V> newLinkedHashSet() {
		return new LinkedHashSet<V>();
	}

	public static <V> LinkedHashSet<V> newLinkedHashSet(
			Collection<? extends V> c) {
		return new LinkedHashSet<V>(c);
	}

	public static <V> TreeSet<V> newTreeSet() {
		return new TreeSet<V>();
	}

	public static <V> TreeSet<V> newTreeSet(Comparator<? super V> comparator) {
		return new TreeSet<V>(comparator);
	}

	public static <V> Stack<V> newStack() {
		return new Stack<V>();
	}
	
	public static <V> ThreadLocal<V> newThreadLocal() {
		return new ThreadLocal<V>();
	}

	public static <V> List<V> emptyIfNull(List<V> list) {
		if (list == null) {
			return Collections.emptyList();
		}
		return list;
	}
	
	public static <V> Set<V> emptyIfNull(Set<V> set) {
		if (set == null) {
			return Collections.emptySet();
		}
		return set;
	}
	
	public static <V> Collection<V> emptyIfNull(Collection<V> collection) {
		if (collection == null) {
			return Collections.emptyList();
		}
		return collection;
	}
	
	public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
		if (map == null) {
			return Collections.emptyMap();
		}
		return map;
	}
	
}
