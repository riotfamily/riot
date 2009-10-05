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
package org.riotfamily.pages.riot.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.riotfamily.core.dao.Constraints;
import org.riotfamily.core.dao.CopyAndPaste;
import org.riotfamily.core.dao.CutAndPaste;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.SingleRoot;
import org.riotfamily.core.dao.Swapping;
import org.riotfamily.pages.model.ContentPage;
import org.riotfamily.pages.model.Site;
import org.springframework.dao.DataAccessException;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageRiotDao implements SingleRoot,	Constraints, Swapping, 
		CutAndPaste, CopyAndPaste {

	public Object getParent(Object entity) {
		ContentPage page = (ContentPage) entity;
		return page.getSite();
	}

	public boolean canAdd(Object parent) {
		if (parent instanceof ContentPage) {
			ContentPage parentPage = (ContentPage) parent;
			return parentPage.canHaveChildren();
		}
		return false;  
	}
	
	public boolean canDelete(Object entity) {
		ContentPage page = (ContentPage) entity;
		return !page.isSystemPage();
	}
	
	public void delete(Object entity, Object parent) throws DataAccessException {
		ContentPage page = (ContentPage) entity;
		if (parent instanceof ContentPage) {
			((ContentPage) parent).getChildren().remove(page);
		}
		page.delete();
	}

	public Class<?> getEntityClass() {
		return ContentPage.class;
	}

	public int getListSize(Object parent, ListParams params)
			throws DataAccessException {

		return -1;
	}

	public String getObjectId(Object entity) {
		ContentPage page = (ContentPage) entity;
		return page.getId().toString();
	}
		
	public Object getRootNode(Object parent) {
		if (parent == null) {
			parent = Site.loadDefaultSite();
		}
		return ((Site) parent).getRootPage();
	}
	
	public Collection<ContentPage> list(Object parent, ListParams params)
			throws DataAccessException {

		Assert.isInstanceOf(ContentPage.class, parent);
		return ((ContentPage) parent).getChildren();
	}
	
	public Object getParentNode(Object node) {
		ContentPage page = (ContentPage) node;
		return page.getParent();
	}

	public boolean hasChildren(Object node, Object parent, ListParams params) {
		ContentPage page = (ContentPage) node;
		if (parent instanceof Site) {
			if (!((Site) parent).equals(page.getSite())) {
				return false;
			}
		}
		return page.getChildren().size() > 0;
	}
	
	public Object load(String id) throws DataAccessException {
		return ContentPage.load(Long.valueOf(id));
	}

	public void save(Object entity, Object parent) throws DataAccessException {
		ContentPage page = (ContentPage) entity;
		Assert.isInstanceOf(ContentPage.class, parent);
		((ContentPage) parent).addPage(page);
		page.save();
	}

	public Object update(Object entity) throws DataAccessException {
		ContentPage page = (ContentPage) entity;
		return page.merge();
	}

	public boolean canSwap(Object entity, Object parent, ListParams params,
			int swapWith) {
		
		ContentPage page = (ContentPage) entity;
		List<ContentPage> siblings = page.getSiblings();
		int i = siblings.indexOf(page) + swapWith;
		return i >= 0 && i < siblings.size();
	}
	
	public void swapEntity(Object entity, Object parent, ListParams params,
			int swapWith) {

		ContentPage page = (ContentPage) entity;
		List<ContentPage> pages = page.getSiblings();
		int i = pages.indexOf(page);
		Collections.swap(pages, i, i+ swapWith);
	}
	
	public boolean canCut(Object entity) {
		ContentPage page = (ContentPage) entity;
		return !page.isSystemPage();
	}
	
	public void cut(Object entity, Object parent) {
	}
	
	public boolean canPasteCut(Object entity, Object target) {
		if (target instanceof ContentPage) {
			ContentPage targetPage = (ContentPage) target;
			return targetPage.isValidChild((ContentPage) entity);
		}
		return false;
	}

	public void pasteCut(Object entity, Object dest) {
		ContentPage page = (ContentPage) entity;
		page.getParent().removePage(page);
		((ContentPage) dest).addPage(page);
	}
	
	public boolean canCopy(Object entity) {
		return false;
	}
	
	public boolean canPasteCopy(Object entity, Object dest) {
		return false;
	}
	
	public void pasteCopy(Object entity, Object dest) {
	}

}
