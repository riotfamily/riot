package org.riotfamily.common.collection;

import java.util.Comparator;

/**
 * Comparator that compares two classes by their hierarchy difference to a 
 * target class.
 * <p>
 * Example:
 *  <pre>
 * 	c = new TypeDifferenceComparator(Integer.class);
 * 
 *  c.compare(Object.class, Number.class); // returns 1
 *  c.compare(Integer.class, Number.class); // returns -1
 *  c.compare(Collection.class, Number.class); // returns Integer.MAX_VALUE - 1
 *  c.compare(Collection.class, Object.class); // returns Integer.MAX_VALUE - 2
 *  </pre>
 * </p>
 */
public class TypeDifferenceComparator implements Comparator<Class<?>> {

	private Class<?> targetClass;
	
	public TypeDifferenceComparator(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	public int compare(Class<?> class1, Class<?> class2) {
		return TypeComparatorUtils.compare(class1, class2, targetClass);
	}
	
}
