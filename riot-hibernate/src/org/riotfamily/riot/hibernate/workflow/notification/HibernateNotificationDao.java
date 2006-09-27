package org.riotfamily.riot.hibernate.workflow.notification;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.riotfamily.riot.hibernate.security.User;
import org.riotfamily.riot.workflow.notification.Notification;
import org.riotfamily.riot.workflow.notification.NotificationDao;
import org.riotfamily.riot.workflow.notification.support.DefaultNotification;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class HibernateNotificationDao extends HibernateDaoSupport 
		implements NotificationDao {		

	public List getNotifications(final String userId) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {

				String hql = "select un.notification from UserNotification un "
						+ "where un.read = false and un.user.id = :userId "
						+ "order by un.notification.issueDate desc";
				
				Query query = session.createQuery(hql);
				query.setParameter("userId", userId);
				return query.list();			
			}
		});
	}

	public void markAsRead(final String userId, final Long notificationId) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) 
					throws HibernateException, SQLException {
				
				String hql = "update UserNotification un set read = true " +
						"where un.user.id = :userId and " +
						"un.notification.id = :notificationId";
				
				Query query = session.createQuery(hql);
				query.setParameter("userId", userId);
				query.setParameter("notificationId", notificationId);
				
				query.executeUpdate();
				return null;
			}
		});
	}
		
	public void saveNotification(Notification notification) {
		final DefaultNotification n = (DefaultNotification) notification;
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) 
					throws HibernateException, SQLException {
				
				session.save(n);

				Query query = session.createQuery("from User user");
				Iterator it = query.iterate();
				while (it.hasNext()) {
					User user = (User) it.next();
					session.save(new UserNotification(user, n));
				}
				
				return null;
			}
		});
	}
	
}
