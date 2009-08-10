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

import java.io.StringReader;

import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentMap;
import org.riotfamily.components.model.ContentMapMarshaller;
import org.springframework.beans.factory.InitializingBean;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.Mapper;

public class XStreamContentMapMarshaller implements ContentMapMarshaller, 
		InitializingBean {

	private XStream xstream;
	
	private HierarchicalStreamDriver driver;
	
	public void setDriver(HierarchicalStreamDriver driver) {
		this.driver = driver;
	}
	
	public void afterPropertiesSet() throws Exception {
		if (driver == null) {
			driver = new DomDriver("UTF-8");
		}
		xstream = new XStream(driver);

		xstream.alias("component", Component.class);
		xstream.alias("component-list", ComponentList.class);
		xstream.alias("content-map", ContentMap.class);
		
		Mapper mapper = xstream.getMapper();
		
		xstream.registerConverter(new ActiveRecordConverter(mapper), 1);
		xstream.registerConverter(new ComponentListConverter(mapper), 1);
		xstream.registerConverter(new ComponentConverter(mapper), 2);
		xstream.registerConverter(new ContentMapConverter(mapper), 1);
	}
	
	public ContentMap unmarshal(Content owner, String xml) {
		DataHolder dataHolder = xstream.newDataHolder();
		dataHolder.put("content", owner);
		HierarchicalStreamReader reader = driver.createReader(new StringReader(xml));
		return (ContentMap) xstream.unmarshal(reader, null, dataHolder);	
	}
	
	public String marshal(ContentMap contentMap) {
		return xstream.toXML(contentMap);
	}
}
