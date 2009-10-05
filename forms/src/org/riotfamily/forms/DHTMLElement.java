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
package org.riotfamily.forms;




/**
 * Interface to be implemented by elements that need to execute a client side
 * script in order to be functional. 
 */
public interface DHTMLElement extends Element {

	/**
	 * Returns a JavaScript that is evaluated in order to initialize the 
	 * element, or <code>null</code> if no initialization is needed.
	 */
	public String getInitScript();

}
