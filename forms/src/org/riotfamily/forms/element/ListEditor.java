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
package org.riotfamily.forms.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.base.Element;
import org.riotfamily.forms.base.UserInterface;
import org.riotfamily.forms.base.Element.State;
import org.riotfamily.forms.client.FormResource;
import org.riotfamily.forms.client.Html;
import org.riotfamily.forms.client.ScriptResource;
import org.riotfamily.forms.value.TypeInfo;
import org.riotfamily.forms.value.Value;

public class ListEditor extends Element {

	private Element itemEditor;
	
	private boolean sortable = true;

	public boolean dragAndDrop = false;
	
	public ListEditor() {
	}
	
	public ListEditor(Element itemEditor) {
		setItemEditor(itemEditor);
	}

	public void setItemEditor(Element itemEditor) {
		this.itemEditor = itemEditor;
	}
		
	@Override
	public Collection<Element> getChildElements() {
		return Collections.singleton(itemEditor);
	}
	
	@Override
	public FormResource getResource() {
		return new ScriptResource("forms/listEditor.js");
	}
		
	public class State extends Element.State {

		private List<Element.State> itemStates = Generics.newArrayList();
		
		private TypeInfo itemTypeInfo;
		
		@Override
		protected void onInit() {
			itemTypeInfo = new TypeInfo(getParent().getTypeInfo().getElementType());
		}
		
		@Override
		public TypeInfo getTypeInfo() {
			return itemTypeInfo;
		}
		
		@Override
		public void setValue(Object value) {
			List<?> c = (List<?>) value;
			if (c != null) {
				for (Object item : c) {
					addItem(item);
				}
			}
			else {
				itemStates.clear();
			}
		}
				
		private Element.State addItem(Object object) {
			Element.State state = itemEditor.createState(this);
			itemStates.add(state);
			return state;
		}

		@Override
		@SuppressWarnings("unchecked")
		public void populate(Value value) {
			List c = getOrCreate(value, List.class, ArrayList.class);
			c.clear();
			for (Element.State itemState : itemStates) {
				Value itemValue = new Value();
				itemState.populate(itemValue);
				c.add(itemValue.get());
			}
		}
		
		/**
		 * Renders all list-items, an add-button and a script to initialize
		 * drag-and-drop (if enabled).
		 */
		@Override
		protected void renderElement(Html html) {
			Html ul = html.ul().cssClass("items");
			for (Element.State itemState : itemStates) {
				renderItem(ul, itemState);
			}
			html.button("add");
			if (dragAndDrop) {
				html.invoke(id(), "ul:first", "listEditor");
			}
		}
		
		/**
		 * Renders a single list-item, as well as buttons to move or delete it.
		 */
		private void renderItem(Html html, Element.State itemState) {
			Html tr = html.li().id(id() + "_" + itemState.id()).table().tr();
			if (dragAndDrop) {
				tr.td("handle");
			}
			else if (sortable) {
				tr.td("moveButtons")
					.button("up", index()).up()
					.button("down", index());
			}
			
			itemState.render(tr.td("itemElement"));
			tr.td("removeButton").button("remove", index());
		}
		
		/**
		 * Adds a new item to the end of the list. The method is invoked when
		 * the user clicks the add button.
		 */
		public void add(UserInterface ui, String value) {
			Element.State itemState = addItem(null);
			Html html = newHtml();
			renderItem(html, itemState);
			ui.insert(this, "ul:first", html);
			updateClasses(ui);
		}
		
		/**
		 * Updates the CSS class-names of the move-up/down buttons. It first
		 * removes the <code>disabled</code> class from all buttons and then
		 * adds it back to the first up and last down button.
		 */
		protected void updateClasses(UserInterface ui) {
			ui.removeClassName(this, "button", "disabled");
			ui.addClassName(this, "ul:first > li:first-child .up", "disabled");
			ui.addClassName(this, "ul:first > li:last-child .down", "disabled");
		} 
		
		private String selector(int i) {
			return String.format("ul:first > li:nth-child(%s)", i + 1);
		}
		
		/**
		 * Removes an element from the list. The method is invoked when a user
		 * clicks the remove button of an item. The index is determined by 
		 * evaluating the {@link #index()} JavaScript expression.
		 */
		public void remove(UserInterface ui, int index) {
			itemStates.remove(index);
			ui.remove(this, selector(index));
			updateClasses(ui);
		}
		
		/**
		 * Moves an element upwards in the list and updates the UI. The method 
		 * is invoked when the user clicks the move-up button of an item.
		 */
		public void up(UserInterface ui, int index) {
			if (index > 0) {
				Collections.swap(itemStates, index, index - 1);
				ui.moveUp(this, selector(index));
				updateClasses(ui);
			}
		}
		
		/**
		 * Moves an element down in the list and updates the UI. The method is 
		 * invoked when the user clicks the move-down button of an item.
		 */
		public void down(UserInterface ui, int index) {
			if (index < itemStates.size() - 1) {
				Collections.swap(itemStates, index, index + 1);
				ui.moveDown(this, selector(index));
				updateClasses(ui);
			}
		}
		
		/**
		 * Returns a JavaScript expression that counts the number of siblings
		 * preceding the enclosing &lt;li&gt;.
		 */
		protected String index() {
			return "$(this).closest('li').index()";
		}
		
		/**
		 * Sorts the list according to the specified order, where order is a 
		 * list of {@link Element.State#id() stateIds}. The method is invoked
		 * when the list is re-ordered via drag-and-drop.
		 */
		public void sort(UserInterface ui, List<String> order) {
			Collections.sort(itemStates, new SortOrderComparator(order));
		}
		
	}
	
	/**
	 * Comparator implementation used by the {@link #sort} method.
	 */
	private static class SortOrderComparator implements Comparator<Element.State> {

		private final List<String> order;

		public SortOrderComparator(List<String> order) {
			this.order = order;
		}

		public int compare(Element.State o1, Element.State o2) {
			Integer i1 = order.indexOf(o1.id());
			Integer i2 = order.indexOf(o2.id());
			return i1.compareTo(i2);
		}
	}

}
