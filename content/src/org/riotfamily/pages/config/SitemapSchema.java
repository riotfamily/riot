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
package org.riotfamily.pages.config;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.pages.model.ContentPage;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class SitemapSchema implements ApplicationContextAware, InitializingBean {

	private String name;
	
	private String label;
		
	private RootPageType rootPage;
	
	private Map<String, PageType> typeMap = Generics.newHashMap();
	
	private Set<String> virtualParents = Generics.newHashSet();

	private SitemapSchemaRepository repository;
	
	private TransactionTemplate transaction;
	
	@Autowired
	public SitemapSchema(PlatformTransactionManager tx) {
		transaction = new TransactionTemplate(tx);
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		repository = BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext, SitemapSchemaRepository.class);
	}

	public void afterPropertiesSet() throws Exception {
		repository.addSchema(this);
		transaction.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				for (Site site : Site.findBySchema(SitemapSchema.this)) {
					syncSystemPages(site);
				}
				Site.loadOrCreateDefaultSite();
			}
		});
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		if (label == null) {
			label = FormatUtils.xmlToTitleCase(name);
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public void setRootPage(RootPageType rootPage) {
		this.rootPage = rootPage;
		rootPage.register(this, null);
	}
	
	void addType(PageType type) {
		if (typeMap.put(type.getName(), type) != null) {
			throw new IllegalArgumentException("Duplicate type: " + type.getName());
		}
		if (isVirtualParent(type)) {
			virtualParents.add(type.getName());
		}
	}
	
	private boolean isVirtualParent(PageType type) {
		if (type instanceof SystemPageType) {
			return ((VirtualPageParent) type).getVirtualChildType() != null;
		}
		return false;
	}
	
	public PageType getPageType(String name) {
		return typeMap.get(name);
	}
	
	public Set<String> getVirtualParents() {
		return virtualParents;
	}
	
	void syncSystemPages() {
		List<Site> sites = Site.findAll();
		if (sites.isEmpty()) {
			Site site = new Site();
			site.setName("Default");
			site.setLocale(Locale.getDefault());
			site.save();
			syncSystemPages(site);
		}
		else {
			for (Site site : sites) {
				syncSystemPages(site);
			}
		}
	}
	
	void syncSystemPages(Site site) {
		rootPage.sync(site);
	}
	
	public VirtualPageType getVirtualChildType(Page page) {
		PageType parentType = page.getPageType();
		if (parentType instanceof VirtualPageParent) {
			return ((VirtualPageParent) parentType).getVirtualChildType();
		}
		return null;
	}

	public boolean isSystemPage(Page page) {
		return page.getPageType() instanceof SystemPageType;
	}

	private List<? extends PageType> getChildTypes(Page page) {
		List<? extends PageType> types = page.getPageType().getChildTypes();
		if (types == null) {
			types = Collections.emptyList();
		}
		return types;
	}
	
	public boolean canHaveChildren(ContentPage parent) {
		return !getChildTypes(parent).isEmpty();
	}
	
	public boolean isValidChild(ContentPage parent, ContentPage child) {
		return getChildTypes(parent).contains(child.getPageType());
	}

}
