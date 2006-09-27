package org.riotfamily.common.collection;

import java.util.Comparator;

/**
 * Comparator that compares two classes by their hierarchy differnce to a 
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
public class TypeDifferenceComparator implements Comparator {

	private Class targetClass;
	
	public TypeDifferenceComparator(Class targetClass) {
		this.targetClass = targetClass;
	}

	public int compare(Object o1, Object o2) {
		return getTypeDifference((Class) o1, targetClass)
				- getTypeDifference((Class) o2, targetClass);
	}
	
	/**
	 * <p>
	 *  Example:
	 *  <pre>
	 *  getTypeDifference(Object.class, Integer.class); // returns 2
	 *  getTypeDifference(Number.class, Integer.class); // returns 1
	 *  getTypeDifference(Integer.class, Integer.class); // returns 0
	 *  
	 *  getTypeDifference(Integer.class, Float.class); // returns Integer.MAX_VALUE
	 *  getTypeDifference(Integer.class, Number.class); // returns Integer.MAX_VALUE
	 *  </pre>
	 * </p>
	 */
	public static int getTypeDifference(Class baseClass, Class subClass) {
		if (!baseClass.isAssignableFrom(subClass)) {
			return Integer.MAX_VALUE;
		}
		int result = 0;
		Class superClass = subClass.getSuperclass();
		while (superClass != null) {
			if (baseClass.isAssignableFrom(superClass)) {
				result++;
				superClass = superClass.getSuperclass();
			}
			else {
				superClass = null;
			}
		}
		return result;
	}

}
