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
package org.riotfamily.forms2.base;

import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.value.Value;

public abstract class TypedState<E extends Element> extends ElementState {

	@Override
	@SuppressWarnings("unchecked")
	protected final void onInit(Element element, Value value) {
		initInternal((E) element, value);
	}
	
	protected void initInternal(E element, Value value) {
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected final void renderElement(Html html, Element element) {
		renderInternal(html, (E) element);
	}
	
	protected abstract void renderInternal(Html html, E element);
	
	@Override
	@SuppressWarnings("unchecked")
	public final void populate(Value value, Element element) {
		populateInternal(value, (E) element);
	}
	
	protected abstract void populateInternal(Value value, E element) ;

}
