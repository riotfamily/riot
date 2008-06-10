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
