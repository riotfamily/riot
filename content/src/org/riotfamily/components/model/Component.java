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
package org.riotfamily.components.model;

import java.util.ListIterator;

import org.springframework.util.Assert;

/**
 * A ContentMap that is contained in a {@link ComponentList}. In addition to a
 * reference to their list, components also have a type. This type is used to 
 * select a rendering view and a form to edit the component's properties.   
 */
public class Component extends ContentMapImpl {

	private ComponentList list;
	
	private String type;
	
	public Component(ComponentList list) {
		super(list.getContent());
		this.list = list;
	}
	
	public Component(ComponentList list, String id) {
		super(list.getContent(), id);
		this.list = list;
	}

	public ComponentList getList() {
		return list;
	}

	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public void delete() {
		Assert.isTrue(list.remove(this));
	}
	
	public void move(String after) {
		delete();
		if (after != null) {
			ListIterator<Component> it = list.listIterator();
			while (it.hasNext()) {
				if (it.next().getCompositeId().equals(after)) {
					it.add(this);
					break;
				}
			}
		}
		else {
			list.add(0, this);
		}
	}
	
	public static Component load(String id) {
		return (Component) Content.loadFragment(id);
	}

	public int getPosition() {
		return list.indexOf(this);
	}
	
	@Override
	public String getPath() {
		return list.getPath() + " " + type;
	}

}
