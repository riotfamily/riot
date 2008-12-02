package org.riotfamily.linkcheck;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.riot.hibernate.dao.HqlDao;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Transactional
public class BrokenLinkRiotDao extends HqlDao {
	
	public BrokenLinkRiotDao(SessionFactory sessionFactory) {
		super(sessionFactory);		
		setEntityClass(Link.class);
	}
	
	public String getObjectId(Object entity) {
		Link link = (Link) entity;
		return link.getSource() + '|' + link.getDestination();
	}
	
	public Object load(String id) throws DataAccessException {
		String[] s = StringUtils.split(id, "|");
		String hql = "from Link where id.source = :source and id.destination = :destination";
		Query query = getSession().createQuery(hql);
		query.setParameter("source", s[0]);
		query.setParameter("destination", s[1]);
		query.setMaxResults(1);
		return query.uniqueResult();
	}
	
}
