/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.linkcheck;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.Query;
import org.hibernate.annotations.AccessType;
import org.riotfamily.common.hibernate.ActiveRecord;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.RiotLog;
import org.riotfamily.crawler.Href;
import org.riotfamily.crawler.PageData;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Entity
@AccessType("field")
@Table(name="riot_links")
public class Link extends ActiveRecord implements Serializable {

	private static RiotLog log = RiotLog.get(Link.class);
	
	@Id	
	private LinkPK primaryKey;
	
	private int statusCode;
	
	private String statusText;

	public Link() {
	}

	public Link(Href href) {
		this(href.getBaseUri(), href.getResolvedUri());		
	}
	
	public Link(String source, String destination) {
		if (primaryKey == null) {
			primaryKey = new LinkPK();
		}
		primaryKey.setDestination(destination);
		primaryKey.setSource(source);		
	}
	
	public Link(PageData pageData) {
		this(pageData.getReferrer(), pageData.getUrl());				
		this.statusCode = pageData.getStatusCode();
		this.statusText = pageData.getError();
	}
	
	public String getDestination() {
		return primaryKey.getDestination();
	}

	public String getSource() {
		return primaryKey.getSource();
	}
	
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = FormatUtils.truncate(statusText, 255);
	}

	public int hashCode() {
		int result = 1;
		if (primaryKey.getSource() != null) {
			result += primaryKey.getSource().hashCode();
		}
		if (primaryKey.getDestination() != null) {
			result += primaryKey.getDestination().hashCode();
		}
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Link other = (Link) obj;
		return ObjectUtils.nullSafeEquals(primaryKey.getSource(), other.primaryKey.getSource())
				&& ObjectUtils.nullSafeEquals(primaryKey.getSource(), other.primaryKey.getDestination());
	}
	
	public String toString() {
		if (!StringUtils.hasText(primaryKey.getDestination())) {
			return super.toString();
		}
		StringBuffer result = new StringBuffer(primaryKey.getDestination());
		
		if (statusCode > 0) {
			result.append(" [").append(statusCode);
			if (StringUtils.hasText(statusText)) {
				result.append(" - ").append(statusText);
			}
			result.append(']');
		}
		if (StringUtils.hasText(primaryKey.getSource())) {
			result.append(" (Source: ").append(primaryKey.getSource()).append(')');
		}
		return result.toString();
	}

	public LinkPK getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(LinkPK primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	public static Link loadLink(String source, String destination) {
		String hql = "from Link where id.source = :source and id.destination = :destination";
		Query query = getSession().createQuery(hql);
		query.setParameter("source", source);
		query.setParameter("destination", destination);
		query.setMaxResults(1);
		return (Link) query.uniqueResult();
	}	
	
	public static void deleteAll() {
		String hql = "delete from Link";
		Query query = getSession().createQuery(hql);
		query.executeUpdate();
	}
	
	public static void saveAll(Collection<Link> links) {
		Iterator<Link> it = links.iterator();
		while (it.hasNext()) {
			Link link = it.next();
			if (link.getSource() == null) {
				log.error("Trying to save link without a source. Possibly this link refers to a broken start page.");
			}
			else {
				getSession().saveOrUpdate(link);
			}
		}
	}	
	
	@SuppressWarnings("unchecked")
	public static Collection<Link> findAllBrokenLinks() {
		String hql = "from Link order by id.source";
		Query query = getSession().createQuery(hql);
		return query.list();
	}
	
	public static int countBrokenLinks() {
		String hql = "select count(*) from Link";
		Query query = getSession().createQuery(hql);
		Number count = (Number) query.uniqueResult();
		return count.intValue();
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<String> findBrokenLinksOnPage(String url) {
		String hql = "select id.destination from Link where id.source = :url";
		Query query = getSession().createQuery(hql);
		query.setParameter("url", url);
		return query.list();
	}
	
	public static void deleteBrokenLinksFrom(String sourceUrl) {
		String hql = "delete from Link where id.source = :url";
		Query query = getSession().createQuery(hql);
		query.setParameter("url", sourceUrl);
		query.executeUpdate();
	}
	
	public static void deleteBrokenLinksTo(String destUrl) {
		String hql = "delete from Link where id.destination = :url";
		Query query = getSession().createQuery(hql);
		query.setParameter("url", destUrl);
		query.executeUpdate();
	}	

}
