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
import java.util.Collections;
import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms2.base.Element;
import org.riotfamily.forms2.base.ElementState;
import org.riotfamily.forms2.base.TypedState;
import org.riotfamily.forms2.base.UserInterface;
import org.riotfamily.forms2.client.FormResource;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.client.Resources;
import org.riotfamily.forms2.client.ScriptResource;
import org.riotfamily.forms2.value.Value;
import org.riotfamily.forms2.value.ValueFactory;
import org.springframework.core.convert.TypeDescriptor;

public class ListEditor extends Element {

	private Element itemEditor;
	
	private boolean sortable = true;

	public boolean dragAndDrop = true;
	
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
		return new State(value.getTypeDescriptor().getElementTypeDescriptor());
	}
		
	@Override
	public FormResource getResource() {
		return new ScriptResource("forms/listEditor.js", Resources.SCRIPTACULOUS_DRAG_DROP);
	}
	
	protected static class State extends TypedState<ListEditor> {

		private List<ElementState> itemStates = Generics.newArrayList();
		
		private TypeDescriptor itemTypeDescriptor;
		
		public State(TypeDescriptor itemTypeDescriptor) {
			this.itemTypeDescriptor = itemTypeDescriptor;
		}
		
		@Override
		protected void initInternal(ListEditor list, Value value) {
			if (value.get() != null) {
				Collection<?> c = value.get();
				for (Object item : c) {
					addItem(list.itemEditor, item);
				}
			}
		}
		
		private ElementState addItem(Element itemElement, Object object) {
			Value value = ValueFactory.createValue(itemTypeDescriptor);
			value.set(object);
			ElementState state = itemElement.createState(this, value);
			itemStates.add(state);
			return state;
		}
		
		@Override
		protected void renderInternal(Html html, ListEditor list) {
			Html ul = html.ul().cssClass("items");
			for (ElementState itemState : itemStates) {
				appendItem(ul, list, itemState);
			}
			html.button("add");
			html.script("riot.ListEditor.init('%s')", getId());
		}
		
		private Html renderItem(ListEditor list, ElementState itemState) {
			Html html = newHtml();
			appendItem(html, list, itemState);
			return html;
		}
		
		private static String selector(int i) {
			return String.format("li:nth-child(%s)", i + 1);
		}
				
		private static String index() {
			return "Element.up(this, 'li').previousSiblings().length";
		}
		
		private void appendItem(Html html, ListEditor list, ElementState itemState) {
			Html tr = html.li().id("item-" + itemState.getId()).table().tr();
			if (list.dragAndDrop) {
				tr.td("handle");
			}
			else if (list.sortable) {
				tr.td("moveButtons")
					.button("up", index()).up()
					.button("down", index());
			}
			
			itemState.render(tr.td("itemElement"), list.itemEditor);
			tr.td("removeButton").button("remove", index());
		}
		
		public void add(UserInterface ui, ListEditor list, String value) {
			ElementState itemState = addItem(list.itemEditor, null);
			ui.insert(this, ".items", renderItem(list, itemState));
			updateClasses(ui);
		}
		
		private void updateClasses(UserInterface ui) {
			ui.removeClassName(this, "button", "disabled");
			ui.addClassName(this, "li:first-child .up", "disabled");
			ui.addClassName(this, "li:last-child .down", "disabled");
		} 
				
		public void remove(UserInterface ui, ListEditor list, int i) {
			itemStates.remove(i);
			ui.remove(this, selector(i));
			updateClasses(ui);
		}
		
		public void up(UserInterface ui, ListEditor list, int i) {
			if (i > 0) {
				Collections.swap(itemStates, i, i - 1);
				ui.moveUp(this, selector(i));
				updateClasses(ui);
			}
		}
		
		public void down(UserInterface ui, ListEditor list, int i) {
			if (i < itemStates.size() - 1) {
				Collections.swap(itemStates, i, i + 1);
				ui.moveDown(this, selector(i));
				updateClasses(ui);
			}
		}
		
		public void sort(UserInterface ui, ListEditor list, List<String> order) {
			order.size();
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
