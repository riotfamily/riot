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

import java.util.Comparator;

/**
 * Comparator that compares two classes by their hierarchy difference to a 
 * target class.
 * <p>
 * Example:
 * </p>
 *  <pre>
 * 	c = new TypeDifferenceComparator(Integer.class);
 * 
 *  c.compare(Object.class, Number.class); // returns 1
 *  c.compare(Integer.class, Number.class); // returns -1
 *  c.compare(Collection.class, Number.class); // returns Integer.MAX_VALUE - 1
 *  c.compare(Collection.class, Object.class); // returns Integer.MAX_VALUE - 2
 *  </pre>
 *
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
