package org.riotfamily.common.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.AbstractFactoryBean;

public class KeyValueListFactoryBean extends AbstractFactoryBean {

	private List list;
	
	public void setEntries(Map entries) {
		list = new ArrayList(entries.size());
		Iterator it = entries.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			list.add(new KeyValueBean(entry.getKey(), entry.getValue()));
		}
	}
	
	protected Object createInstance() throws Exception {
		return list;
	}

	public Class getObjectType() {
		return List.class;
	}

	
	

}
