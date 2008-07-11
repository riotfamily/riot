package org.riotfamily.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public final class Generics {

	private Generics() {
	}
	
	public static<K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}
	
	public static<K, V> HashMap<K, V> newHashMap(Map<? extends K, ? extends V> m) {
		return new HashMap<K, V>(m);
	}
	
	public static<K, V> TreeMap<K, V> newTreeMap() {
		return new TreeMap<K, V>();
	}
	
	public static<K, V> TreeMap<K, V> newTreeMap(Map<? extends K, ? extends V> m) {
		return new TreeMap<K, V>(m);
	}	
	
	public static<V> ArrayList<V> newArrayList() {
		return new ArrayList<V>();
	}
	
	public static<V> ArrayList<V> newArrayList(Collection<? extends V> c) {
		return new ArrayList<V>(c);
	}
	
	public static<V> LinkedList<V> newLinkedList() {
		return new LinkedList<V>();
	}
	
	public static<V> LinkedList<V> newLinkedList(Collection<? extends V> c) {
		return new LinkedList<V>(c);
	}
	
	public static<V> HashSet<V> newHashSet() {
		return new HashSet<V>();
	}
	
	public static<V> HashSet<V> newHashSet(Collection<? extends V> c) {
		return new HashSet<V>(c);
	}
	
	public static<V> LinkedHashSet<V> newLinkedHashSet() {
		return new LinkedHashSet<V>();
	}
	
	public static<V> LinkedHashSet<V> newLinkedHashSet(Collection<? extends V> c) {
		return new LinkedHashSet<V>(c);
	}
	
	public static<V> TreeSet<V> newTreeSet() {
		return new TreeSet<V>();
	}
	
	public static<V> TreeSet<V> newTreeSet(Comparator<? super V> comparator) {
		return new TreeSet<V>(comparator);
	}

}
