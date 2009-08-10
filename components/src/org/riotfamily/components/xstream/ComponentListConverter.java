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

import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.Content;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class ComponentListConverter extends CollectionConverter {
	
	public ComponentListConverter(Mapper mapper) {
		super(mapper);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return ComponentList.class.isAssignableFrom(type);
	}
	
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, 
			MarshallingContext context) {
		
		ComponentList list = (ComponentList) source;
		writer.addAttribute("id", list.getPartId());
		super.marshal(list, writer, context);
	}
	
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, 
			UnmarshallingContext context) {
		
		Content owner = (Content) context.get("content");
		String id = reader.getAttribute("id");
		ComponentList list = new ComponentList(owner, id);
		ComponentList parentList = (ComponentList) context.get("list");
		context.put("list", list);
        populateCollection(reader, context, list);
        context.put("list", parentList);
        return list;
    }

}
