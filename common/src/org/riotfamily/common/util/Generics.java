package org.riotfamily.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

public final class Generics {

	private Generics() {
	}
	
	public static<K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}
	
	public static<K, V> HashMap<K, V> newHashMap(Map<K, V> m) {
		return new HashMap<K, V>(m);
	}
	
	public static<V> ArrayList<V> newArrayList() {
		return new ArrayList<V>();
	}
	
	public static<V> ArrayList<V> newArrayList(Collection<V> c) {
		return new ArrayList<V>(c);
	}
	
	public static<V> LinkedList<V> newLinkedList() {
		return new LinkedList<V>();
	}
	
	public static<V> LinkedList<V> newLinkedList(Collection<V> c) {
		return new LinkedList<V>(c);
	}
	
	public static<V> HashSet<V> newHashSet() {
		return new HashSet<V>();
	}
	
	public static<V> HashSet<V> newHashSet(Collection<V> c) {
		return new HashSet<V>(c);
	}
	
	public static<V> LinkedHashSet<V> newLinkedHashSet() {
		return new LinkedHashSet<V>();
	}
	
	public static<V> LinkedHashSet<V> newLinkedHashSet(Collection<V> c) {
		return new LinkedHashSet<V>(c);
	}

}
