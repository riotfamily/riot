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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
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
