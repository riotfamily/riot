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

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Enumeration that works on an Iterator.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class IteratorEnumeration<T> implements Enumeration<T> {

	private Iterator<T> iterator;
	
	public IteratorEnumeration(Iterator<T> iterator) {
		this.iterator = iterator;
	}

	public boolean hasMoreElements() {
		return iterator.hasNext();
	}
	
	public T nextElement() {
		return iterator.next();
	}

}
