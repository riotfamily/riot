package org.riotfamily.common.beans.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.config.AbstractFactoryBean;

public class JoinedListFactoryBean extends AbstractFactoryBean {

	private List<Collection<?>> lists;
	
	public void setLists(List<Collection<?>> lists) {
		this.lists = lists;
	}
	
	@Override
	protected Object createInstance() throws Exception {
		ArrayList<Object> joinedList = new ArrayList<Object>();
		for (Collection<?> list : lists) {
			if (list != null) {
				joinedList.addAll(list);
			}
		}
		return joinedList;
	}

	@Override
	public Class<?> getObjectType() {
		return ArrayList.class;
	}

}
