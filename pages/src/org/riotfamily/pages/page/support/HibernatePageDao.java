package org.riotfamily.pages.page.support;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.pages.page.PageAlias;
import org.riotfamily.pages.page.PageDao;
import org.riotfamily.pages.page.PersistentPage;
import org.riotfamily.riot.hibernate.support.HibernateSupport;

/**
 * PageDao implementation based on Hibernate.
 */
public class HibernatePageDao extends HibernateSupport implements PageDao {

	public HibernatePageDao(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}

	public List listRootPages() {
		return createQuery("from PersistentPage where parent is null " +
				"order by position").list();
	}

	public List listAliases() {
		return createCriteria(PageAlias.class).list();
	}

	public void deleteAlias(String path) {
		Query query = createQuery("delete from PageAlias where path = :path");
		query.setParameter("path", path);
		query.executeUpdate();
	}

	public void addAlias(String path, PersistentPage page) {
		PageAlias alias = new PageAlias(path, page);
		getSession().save(alias);
	}

	public void clearAliases(PersistentPage page) {
		Query query = createQuery("update PageAlias set page = null" +
				" where page = :page");
		
		query.setParameter("page", page);
		query.executeUpdate();		
	}

	public void deletePage(PersistentPage page) {
		getSession().delete(page);
	}

	public PersistentPage loadPage(Long id) {
		return (PersistentPage) getSession().get(PersistentPage.class, id);
	}

	public void savePage(PersistentPage page) {
		getSession().save(page);
	}

	public void updatePage(PersistentPage page) {
		getSession().update(page);
	}

}
