package org.riotfamily.pages.component.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ContextMap implements Serializable {

	private transient Map contexts;
	
	public void removeExpiredContexts() {
		if (contexts != null) {
			Iterator it = contexts.values().iterator();
			while (it.hasNext()) {
				PageRequestContext context = (PageRequestContext) it.next();
				if (context.hasExpired()) {
					it.remove();
				}
			}
		}
	}
	
	public void put(String uri, PageRequestContext context) {
		if (contexts == null) {
			 contexts = new HashMap();
		}
		contexts.put(uri, context);
	}
	
	public PageRequestContext get(String uri) {
		if (contexts == null) {
			return null;
		}
		return (PageRequestContext) contexts.get(uri);
	}

}
