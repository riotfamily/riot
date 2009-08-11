package org.riotfamily.dbmsgsrc.dao;

import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;

public interface DbMessageSourceDao {

	public MessageBundleEntry findEntry(String bundle, String code);
	
	public void saveEntry(MessageBundleEntry entry);

	public void removeEmptyEntries(String bundle);
	
}
