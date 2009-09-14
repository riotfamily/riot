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
 *
 */
public interface FormListener {

	public void elementRendered(Element element);
	
	public void elementValidated(Element element);
	
	public void elementChanged(Element element);
	
	public void elementRemoved(Element element);
	
	public void elementAdded(Element element);
	
	public void elementFocused(Element element);
	
	public void elementEnabled(Element element);	
	
	public void refresh(Element element);
	
	public void eval(String script);

}
