package org.riotfamily.common.xml;


public interface ConfigurableBean {

	public boolean isReloadable();
	
	public void configure();
	
}
