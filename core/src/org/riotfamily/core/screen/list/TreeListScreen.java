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
package org.riotfamily.core.screen.list;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.core.dao.Searchable;
import org.riotfamily.core.dao.Sortable;
import org.riotfamily.core.screen.AbstractRiotScreen;
import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.RiotScreen;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.ScreenLink;
import org.riotfamily.core.screen.ScreenUtils;
import org.riotfamily.core.screen.list.command.Command;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.element.TextField;
import org.riotfamily.forms.factory.FormRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.support.RequestContextUtils;


public class TreeListScreen extends AbstractRiotScreen implements Controller, 
		ListScreen, ApplicationContextAware, InitializingBean {

	private String viewName = ResourceUtils.getPath(
			TreeListScreen.class, "list.ftl");
	
	private RiotDao dao;
	
	private int pageSize = 25;
	
	private FormRepository filterFormRepository;
	
	private String filterFormId;
	
	private List<ColumnConfig> columns;
	
	private String labelProperty;
	
	private Map<String, Command> commandMap;
		
	private RiotScreen itemScreen;

	private ApplicationContext applicationContext;


	public TreeListScreen(FormRepository filterFormRepository) {
		this.filterFormRepository = filterFormRepository;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		
		this.applicationContext = applicationContext;
	}
	
	public void afterPropertiesSet() throws Exception {
		if (commandMap == null) {
			setCommands(SpringUtils.getBeanIfExists(applicationContext, 
					"defaultCommands", Collection.class));
		}
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public void setFilterFormId(String filterFormId) {
		this.filterFormId = filterFormId;
	}

	/* (non-Javadoc)
	 * @see org.riotfamily.core.screen.list.ListScreen#getDao()
	 */
	public RiotDao getDao() {
		return dao;
	}

	@Override
	public String getTitle(ScreenContext context) {
		if (context.getParent() != null && getParentScreen() instanceof ListScreen) {
			return ScreenUtils.getParentListScreen(this)
					.getItemLabel(context.getParent());			
		}
		return super.getTitle(context);
	}
	
	public void setDao(RiotDao dao) {
		this.dao = dao;
	}

	public List<ColumnConfig> getColumns() {
		return columns;
	}
	
	public void setColumns(List<ColumnConfig> columns) {
		this.columns = columns;
	}
	
	public ColumnConfig findColumn(String property) {		
		property = property.trim();		
		for (ColumnConfig column : columns) {			
			if (property.equals(column.getProperty())) {
				return column;
			}
		}
		return null;
	}

	public void setCommands(Collection<?> commands) {
		this.commandMap = Generics.newLinkedHashMap();
		addCommands(commands);
	}
	
	private void addCommands(Collection<?> commands) {
		if (commands != null) {
			for (Object command : commands) {
				if (command instanceof Command) {
					String id = ObjectUtils.getIdentityHexString(command);
					this.commandMap.put(id, (Command) command);
				}
				else if (command instanceof Collection<?>) {
					addCommands((Collection<?>) command);
				}
				else {
					throw new IllegalArgumentException(
							"Expected command or Collection but found " + command);
				}
			}
		}
	}

	public Map<String, Command> getCommandMap() {
		return commandMap;
	}
	
	/* (non-Javadoc)
	 * @see org.riotfamily.core.screen.list.ListScreen#getItemScreen()
	 */
	public RiotScreen getItemScreen() {
		return itemScreen;
	}

	public void setItemScreen(RiotScreen itemScreen) {
		this.itemScreen = itemScreen;
		itemScreen.setParentScreen(this);
	}
	
	@Override
	public Collection<RiotScreen> getChildScreens() {
		if (itemScreen == null) {
			return Collections.emptySet();
		}
		return Collections.singleton(itemScreen);
	}
		
	/* (non-Javadoc)
	 * @see org.riotfamily.core.screen.list.ListScreen#getItemLabel(java.lang.Object)
	 */
	public String getItemLabel(Object object) {
		StringBuilder label = new StringBuilder();
		String labelProperty = getLabelProperty(); 
		if (labelProperty != null) {
			Pattern p = Pattern.compile("([\\w.\\[\\]]+)(\\W*)");
			Matcher m = p.matcher(labelProperty);
			while (m.find()) {
				String property = m.group(1);
				Object value = PropertyUtils.getProperty(object, property);
				if (value != null) {
					label.append(value);
					label.append(m.group(2));
				}
			}
			if (label.length() > 0) {
				return label.toString();
			}
		}
		return null;
	}
	
	public void setLabelProperty(String labelProperty) {
		this.labelProperty = labelProperty;
	}
	
	private String getLabelProperty() {
		if (labelProperty == null) {
			labelProperty = getFirstReadableProperty();
		}
		return labelProperty;
	}
	
	private String getFirstReadableProperty() {
		if (columns != null && !columns.isEmpty()) {
			for (int i = 0; i < columns.size(); i++) {
				String property = columns.get(i).getProperty();
				if (StringUtils.hasText(property)) {
					return property;
				}
			}
		}
		return null;
	}
	
	public String getListStateKey(ScreenContext context) {
		return getListStateKey(context, null);
	}
	
	private String getListStateKey(ScreenContext context, 
			ChooserSettings chooserSettings) {
		
		StringBuilder key = new StringBuilder("list:");
		key.append(getId()).append("/-");
		if (context.getParentId() != null) {
			key.append('/').append(context.getParentId());
			if (context.isNestedTreeItem()) {
				key.append('/').append(getId());	
			}
		}
		if (chooserSettings != null && chooserSettings.getTargetScreenId() != null) {
			key.append("?choose=").append(chooserSettings.getTargetScreenId())
				.append("&").append(chooserSettings.getTargetClass().getName());
		}
		return key.toString();
	}
	
	public ListState getOrCreateListState(HttpServletRequest request, 
			ScreenContext screenContext, ChooserSettings chooserSettings) {
		
		String key = getListStateKey(screenContext, chooserSettings);
		ListState state = ListState.get(request, key);
		if (state == null) {
			Locale locale = RequestContextUtils.getLocale(request);
			
			Form filterForm = null;
			TextField searchField = null;
			if (filterFormId != null) {
				filterForm = filterFormRepository.createForm(filterFormId);
			}
			if (dao instanceof Searchable) {
				String[] search = ((Searchable) dao).getSearchableProperties();
				if (search != null && search.length > 0) {
					if (filterForm == null) {
						filterForm = new Form();
						filterForm.setBeanClass(HashMap.class);
					}
					searchField = new TextField();
					searchField.setLabel(applicationContext.getMessage("label.list.filter.search", null, "Search", locale));
					filterForm.addElement(searchField);
				}
			}
			state = new ListState(key, getId(), locale, 
					screenContext.getParentId(), filterForm, 
					searchField, pageSize, chooserSettings);
			
			if (dao instanceof Sortable) {
				Sortable sortable = (Sortable) dao;
				for (ColumnConfig col : columns) {
					if (col.isSortable() && sortable.canSortBy(col.getProperty())) {
						state.getParams().orderBy(col.getProperty(), col.isAscending(), col.isCaseSensitive());
						break;
					}
				}
			}
			
			ListState.put(request, key, state);
		}
		return state;
	}
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		
		ScreenContext screenContext = ScreenContext.Binding.get(request);
		ChooserSettings chooserSettings = new ChooserSettings(request);
		ModelAndView mv = new ModelAndView(viewName);
		
		if (chooserSettings.getTargetScreenId() != null) {
			mv.addObject("chooser", true);
			List<ScreenLink> path = Generics.newArrayList();
			ScreenContext ctx = screenContext;
			while (ctx != null) {
				if (ctx.getScreen() instanceof ListScreen) {
					if (AccessController.isGranted("viewScreen", ctx.getScreen(), screenContext)) {
						path.add(0, chooserSettings.appendTo(ctx.getLink()));
					}
				}
				if (ctx.getScreen().getId().equals(chooserSettings.getStartScreenId())) {
					break;
				}
				ctx = ctx.createParentContext();
			}
			mv.addObject("path", path);
		}
		ListState state = getOrCreateListState(request, screenContext, chooserSettings);
		mv.addObject("listState", state);
		return mv;
	}
		
}
