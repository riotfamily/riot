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
import org.riotfamily.crawler.Href;
import org.riotfamily.crawler.PageData;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Entity
@AccessType("field")
@Table(name="riot_broken_links")
public class BrokenLink extends ActiveRecord implements Serializable {

//	private Logger log = LoggerFactory.getLogger(Link.class);

	@Id
	private BrokenLinkPK primaryKey;

	private int statusCode;
	
	private String statusText;

	public BrokenLink() {
	}

	public BrokenLink(Href href) {
		this(href.getBaseUri(), href.getResolvedUri());		
	}
	
	public BrokenLink(String source, String destination) {
		if (primaryKey == null) {
			primaryKey = new BrokenLinkPK();
		}
		primaryKey.setDestination(destination);
		primaryKey.setSource(source);		
	}
	
	public BrokenLink(PageData pageData) {
		this(pageData.getReferrer(), pageData.getUrl());				
		this.statusCode = pageData.getStatusCode();
		this.statusText = pageData.getError();
	}

	public BrokenLinkPK getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(BrokenLinkPK primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getSource() {
		return primaryKey.getSource();
	}

	public String getDestination() {
		return primaryKey.getDestination();
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

	@Override
	public int hashCode() {
		if (primaryKey != null) {
			return primaryKey.hashCode();
		}
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof BrokenLink) {
			BrokenLink other = (BrokenLink) obj;
			return ObjectUtils.nullSafeEquals(primaryKey, other.getPrimaryKey());
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(getDestination());
		if (statusCode > 0) {
			result.append(" [").append(statusCode);
			if (StringUtils.hasText(statusText)) {
				result.append(" - ").append(statusText);
			}
			result.append(']');
		}
		if (StringUtils.hasText(getSource())) {
			result.append(" (Source: ").append(getSource()).append(')');
		}
		return result.toString();
	}

	// ----------------------------------------------------------------------
	// ActiveRecord methods
	// ----------------------------------------------------------------------
	
	public static BrokenLink load(String source, String destination) {
		String hql = "from BrokenLink where source = :source and destination = :destination";
		Query query = getSession().createQuery(hql);
		query.setParameter("source", source);
		query.setParameter("destination", destination);
		query.setMaxResults(1);
		return (BrokenLink) query.uniqueResult();
	}	
	
	public static void deleteAll() {
		String hql = "delete from BrokenLink";
		Query query = getSession().createQuery(hql);
		query.executeUpdate();
	}
	
	public static void saveAll(Collection<BrokenLink> links) {
		Iterator<BrokenLink> it = links.iterator();
		while (it.hasNext()) {
			BrokenLink link = it.next();
			if (link.getSource() == null) {
				LoggerFactory.getLogger(BrokenLink.class).error(
						"Trying to save broken link without a source. Possibly " +
						"this link refers to a broken crawler start page.");
			}
			else {
				getSession().saveOrUpdate(link);
			}
		}
	}	
	
	@SuppressWarnings("unchecked")
	public static Collection<BrokenLink> findAllBrokenLinks() {
		String hql = "from BrokenLink order by id.source";
		Query query = getSession().createQuery(hql);
		return query.list();
	}
	
	public static int countBrokenLinks() {
		String hql = "select count(*) from BrokenLink";
		Query query = getSession().createQuery(hql);
		Number count = (Number) query.uniqueResult();
		return count.intValue();
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<String> findBrokenLinksOnPage(String url) {
		String hql = "select id.destination from BrokenLink where id.source = :url";
		Query query = getSession().createQuery(hql);
		query.setParameter("url", url);
		return query.list();
	}
	
	public static void deleteBrokenLinksFrom(String sourceUrl) {
		String hql = "delete from BrokenLink where id.source = :url";
		Query query = getSession().createQuery(hql);
		query.setParameter("url", sourceUrl);
		query.executeUpdate();
	}
	
	public static void deleteBrokenLinksTo(String destUrl) {
		String hql = "delete from BrokenLink where id.destination = :url";
		Query query = getSession().createQuery(hql);
		query.setParameter("url", destUrl);
		query.executeUpdate();
	}	

}
