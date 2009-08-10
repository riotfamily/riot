/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.xstream;

import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class ComponentConverter extends ContentMapConverter {

	public ComponentConverter(Mapper mapper) {
		super(mapper);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return Component.class.isAssignableFrom(type);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, 
			MarshallingContext context) {
		
		Component component = (Component) source;
		writer.addAttribute("type", component.getType());
		super.marshal(component, writer, context);
	}
	
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, 
			UnmarshallingContext context) {
		
		ComponentList list = (ComponentList) context.get("list");
		String id = reader.getAttribute("id");
		Component component = new Component(list, id);
		component.setType(reader.getAttribute("type"));
        populateMap(reader, context, component);
        return component;
    }
}
