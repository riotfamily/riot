package org.riotfamily.dbmsgsrc.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.riotfamily.riot.hibernate.support.HibernateHelper;
import org.springframework.transaction.annotation.Transactional;

public class HibernateMessageSourceDao implements DbMessageSourceDao {

	private HibernateHelper hibernate;
	
	public HibernateMessageSourceDao(SessionFactory sessionFactory) {
		hibernate = new HibernateHelper(sessionFactory, "messages");
	}

	public MessageBundleEntry findEntry(String bundle, String code) {
		return (MessageBundleEntry) hibernate.createCacheableCriteria(
				MessageBundleEntry.class)
				.add(Restrictions.naturalId()
					.set("bundle", bundle)
					.set("code", code))
				.uniqueResult();
	}

	@Transactional
	public void saveEntry(MessageBundleEntry entry) {
		hibernate.save(entry);
	}
	
	@Transactional
	@SuppressWarnings("unchecked")
	public void removeEmptyEntries(String bundle) {
		List<MessageBundleEntry> entries = hibernate.createCacheableCriteria(
				MessageBundleEntry.class)
				.add(Restrictions.sizeLe("messages", 1))
				.add(Restrictions.naturalId().set("bundle", bundle))
				.list();
		
		for (MessageBundleEntry entry : entries) {
			hibernate.delete(entry);
		}
	}

}
