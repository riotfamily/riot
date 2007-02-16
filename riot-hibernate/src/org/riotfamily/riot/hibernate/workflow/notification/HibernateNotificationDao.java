/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
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
