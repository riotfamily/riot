package org.riotfamily.dbmsgsrc.riot;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.riotfamily.dbmsgsrc.support.DbMessageSource;
import org.riotfamily.riot.hibernate.dao.AbstractHqlDao;
import org.springframework.dao.DataAccessException;

public class MessageBundleEntryDao extends AbstractHqlDao {

	private String bundle = DbMessageSource.DEFAULT_BUNDLE;

	public MessageBundleEntryDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	public void setBundle(String bundle) {
		this.bundle = bundle;
	}
	
	public String getBundle() {
		return bundle;
	}

	public Class<?> getEntityClass() {
		return MessageBundleEntry.class;
	}
	
	@Override
	protected String getWhere() {
		return "this.bundle = :bundle";
	}
	
	@Override
	protected void setQueryParameters(Query query, Object parent, ListParams params) {
		super.setQueryParameters(query, parent, params);
		query.setParameter("bundle", bundle);
	}

	@Override
	public void save(Object entity, Object parent) throws DataAccessException {
		MessageBundleEntry entry = (MessageBundleEntry) entity;
		entry.setBundle(bundle);
		super.save(entity, parent);
	}
}
