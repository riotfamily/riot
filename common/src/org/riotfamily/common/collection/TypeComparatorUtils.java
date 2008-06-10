package org.riotfamily.common.collection;

public class TypeComparatorUtils {
	
	private TypeComparatorUtils() {
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
	public static int getTypeDifference(Class<?> baseClass, Class<?> subClass) {
		if (!baseClass.isAssignableFrom(subClass)) {
			return Integer.MAX_VALUE;
		}
		int result = 0;
		Class<?> superClass = subClass.getSuperclass();
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
	
	public static int compare(Class<?> class1, Class<?> class2, Class<?> targetClass) {
		return TypeComparatorUtils.getTypeDifference(class1, targetClass)
				- TypeComparatorUtils.getTypeDifference(class2, targetClass);
	}
}
