package org.riotfamily.riot.dao;

import java.util.List;

public interface ListParams {

	public String getParentId();
	
	public Object getFilter();

	public boolean hasOrder();
	
	public List getOrder();

	public int getPageSize();

	public int getOffset();

}