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
package org.riotfamily.forms.client;

import java.io.Serializable;

public class IdGenerator implements Serializable {
	
	private int index;
	
	private String prevId;
	
	private String nextId;
	
	/**
	 * Returns the id from the last call to inputId().
	 */
	String prev() {
		return prevId;
	}
	
	/**
	 * Returns a new id that will be used as next inputId.
	 */
	String next() {
		nextId = newId();
		return nextId;
	}
	
	private String newId() {
		return "f" + index++;
	}
	
	String inputId() {
		prevId = nextId != null ? nextId : newId();
		return prevId;
	}
}