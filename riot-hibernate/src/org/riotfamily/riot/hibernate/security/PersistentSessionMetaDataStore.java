package org.riotfamily.riot.hibernate.security;

import java.util.List;

import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.core.security.session.SessionMetaDataStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PersistentSessionMetaDataStore 
		implements SessionMetaDataStore<PersistentSessionMetaData> {

	public List<PersistentSessionMetaData> listAll() {
		return PersistentSessionMetaData.findAll();
	}

	public void sessionEnded(PersistentSessionMetaData data) {
		data = data.merge();
		data.sessionEnded();
	}

	public PersistentSessionMetaData sessionStarted(String userName, RiotUser user, 
			String loginIP) {
		
		PersistentSessionMetaData meta = PersistentSessionMetaData.loadByUser(user); 
		if (meta == null) {
			meta = new PersistentSessionMetaData(user);
			meta.save();
		}
		meta.sessionStarted(userName, loginIP);
		return meta;
	}

}
