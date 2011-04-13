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
package org.riotfamily.pages.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.riotfamily.common.hibernate.ActiveRecordBeanSupport;



/**
 * Alias for a page. Aliases are created whenever a page (or one of it's 
 * ancestors) is renamed or moved.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
@Entity
@Table(name="riot_page_aliases", uniqueConstraints={
	@UniqueConstraint(columnNames={"site_id","path"})
})
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
public class PageAlias extends ActiveRecordBeanSupport {

	private ContentPage page;
	
	private Site site;
	
	private String path;
	
	public PageAlias() {
	}
	
	public PageAlias(ContentPage page, String path) {
		this.page = page;
		this.site = page.getSite();
		this.path = path;
	}

	@ManyToOne
	public ContentPage getPage() {
		return this.page;
	}

	public void setPage(ContentPage page) {
		this.page = page;
	}
	
	@ManyToOne(cascade=CascadeType.MERGE)
	public Site getSite() {
		return this.site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "PageAlias[" + page + " --> " + path + "]";
	}
	
	// ----------------------------------------------------------------------
	// 
	// ----------------------------------------------------------------------
	
	public static PageAlias loadBySiteAndPath(Site site, String path) {
		return query(PageAlias.class,
				"from {} where site = ? and path = ?", site, path)
				.cache().load();
	}

	private static void deleteBySiteAndPath(Site site, String path) {
		query(PageAlias.class, 
				"delete from {} where site = ? and path = ?", site, path)
				.executeUpdate();
	}
	
	public static void create(ContentPage page, String oldPath) {
		deleteBySiteAndPath(page.getSite(), page.getPath());
		if (oldPath != null) {
			deleteBySiteAndPath(page.getSite(), oldPath);
			PageAlias alias = new PageAlias(page, oldPath);
			alias.save();
		}
	}
	
	public static void resetByPage(ContentPage page) {
		query(PageAlias.class, "update {} set page = null where page = ?", page)
				.executeUpdate();
	}
	
	public static void deleteAlias(Page page) {
		PageAlias alias = loadBySiteAndPath(page.getSite(), page.getPath());
		if (alias != null) {
			alias.delete();
		}
	}
	
}
