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
package org.riotfamily.forms2.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms2.base.Element;
import org.riotfamily.forms2.base.ElementState;
import org.riotfamily.forms2.base.TypedState;
import org.riotfamily.forms2.base.UserInterface;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.value.Value;
import org.riotfamily.forms2.value.ValueFactory;
import org.springframework.core.convert.TypeDescriptor;

public class ListEditor extends Element {

	private Element itemEditor;
	
	public ListEditor() {
	}
	
	public ListEditor(Element itemEditor) {
		setItemEditor(itemEditor);
	}

	public void setItemEditor(Element itemEditor) {
		this.itemEditor = itemEditor;
		itemEditor.setParent(this);
	}
		
	@Override
	protected ElementState createState(Value value) {
		value.require(List.class);
		State state = new State(value.getTypeDescriptor().getElementTypeDescriptor());
		if (value.get() != null) {
			Collection<?> c = value.get();
			for (Object item : c) {
				state.add(itemEditor, item);
			}
		}
		return state;
	}
	
	protected static class State extends TypedState<ListEditor> {

		private List<ElementState> itemStates = Generics.newArrayList();
		
		private TypeDescriptor itemTypeDescriptor;
		
		public State(TypeDescriptor itemTypeDescriptor) {
			this.itemTypeDescriptor = itemTypeDescriptor;
		}
		
		@Override
		protected void renderInternal(Html html, ListEditor list) {
			Html items = html.elem("ul").cssClass("items");
			for (ElementState itemState : itemStates) {
				renderItem(items, list, itemState);
			}
			html.button("Add").propagate("click", "add");
		}
		
		private void renderItem(Html html, ListEditor list, ElementState itemState) {
			Html li = html.elem("li");
			itemState.render(li, list.itemEditor);
			li.button("Remove").propagate("click", "remove", "Element.up(this).previousSiblings().length");
		}
		
		public void add(UserInterface ui, ListEditor list, String value) {
			ElementState itemState = add(list.itemEditor, null);
			renderItem(ui.insert(this, ".items"), list, itemState);
		}
		
		private ElementState add(Element itemElement, Object object) {
			Value value = ValueFactory.createValue(itemTypeDescriptor);
			value.set(object);
			ElementState state = itemElement.createState(this, value);
			itemStates.add(state);
			return state;
		}
		
		public void remove(UserInterface ui, ListEditor list, String value) {
			int i = Integer.parseInt(value);
			itemStates.remove(i);
			ui.remove(this, String.format("li:nth-child(%s)", i + 1));
		}
		
		@Override
		protected void populateInternal(Value value, ListEditor list) {
			value.require(List.class);
			List<Object> c = value.getOrCreate(ArrayList.class);
			c.clear();
			for (ElementState itemState : itemStates) {
				Value itemValue = ValueFactory.createValue(itemTypeDescriptor);
				itemState.populate(itemValue, list.itemEditor);
				c.add(itemValue.get());
			}
		}

	}

}
