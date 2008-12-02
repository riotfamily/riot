package org.riotfamily.linkcheck;

import java.util.Collection;
import java.util.Iterator;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.riot.hibernate.support.HibernateHelper;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HibernateLinkDao implements LinkDao {

	private HibernateHelper hibernate;
	
	public HibernateLinkDao(SessionFactory sessionFactory) {		
		this.hibernate = new HibernateHelper(sessionFactory);
	}	
	
	public Link loadLink(String source, String destination) {
		String hql = "from Link where id.source = :source and id.destination = :destination";
		Query query = hibernate.createQuery(hql);
		query.setParameter("source", source);
		query.setParameter("destination", destination);
		query.setMaxResults(1);
		return hibernate.uniqueResult(query);
	}
	
	public void deleteAll() {
		String hql = "delete from Link";
		Query query = hibernate.createQuery(hql);
		hibernate.executeUpdate(query);
	}
	
	public void saveAll(Collection<Link> links) {
		Iterator<Link> it = links.iterator();
		while (it.hasNext()) {
			Link link = it.next();
			hibernate.saveOrUpdate(link);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Link> findAllBrokenLinks() {
		String hql = "from Link order by id.source";
		Query query = hibernate.createQuery(hql);
		return hibernate.list(query);
	}
	
	public int countBrokenLinks() {
		String hql = "select count(*) from Link";
		Query query = hibernate.createQuery(hql);
		Number count = hibernate.uniqueResult(query);
		return count.intValue();
	}
	
	@SuppressWarnings("unchecked")
	public Collection<String> findBrokenLinksOnPage(String url) {
		String hql = "select id.destination from Link where id.source = :url";
		Query query = hibernate.createQuery(hql);
		query.setParameter("url", url);
		return hibernate.list(query);
	}
	
	public void deleteBrokenLinksFrom(String sourceUrl) {
		String hql = "delete from Link where id.source = :url";
		Query query = hibernate.createQuery(hql);
		query.setParameter("url", sourceUrl);
		hibernate.executeUpdate(query);
	}
	
	public void deleteBrokenLinksTo(String destUrl) {
		String hql = "delete from Link where id.destination = :url";
		Query query = hibernate.createQuery(hql);
		query.setParameter("url", destUrl);
		hibernate.executeUpdate(query);
	}
}
