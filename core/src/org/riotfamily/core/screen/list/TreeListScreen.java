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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
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

import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.core.screen.AbstractRiotScreen;
import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.RiotScreen;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.ScreenLink;
import org.riotfamily.core.screen.list.command.Command;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.element.TextField;
import org.riotfamily.forms.factory.FormRepository;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.support.RequestContextUtils;


public class TreeListScreen extends AbstractRiotScreen implements Controller, ListScreen {

	private String viewName = ResourceUtils.getPath(
			TreeListScreen.class, "list.ftl");
	
	private RiotDao dao;
	
	private FormRepository filterFormRepository;
	
	private String filterFormId;
	
	private String searchProperties;
	
	private List<ColumnConfig> columns;
	
	private String labelProperty;
	
	private Map<String, Command> commands = Generics.newLinkedHashMap();
		
	private RiotScreen itemScreen;

	/* (non-Javadoc)
	 * @see org.riotfamily.core.screen.list.ListScreen#getDao()
	 */
	public RiotDao getDao() {
		return dao;
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

	public void setCommands(List<Command> commands) {
		for (Command command : commands) {
			String id = ObjectUtils.getIdentityHexString(command);
			this.commands.put(id, command);
		}
	}

	public Map<String, Command> getCommandMap() {
		return commands;
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
	
	public Collection<RiotScreen> getChildScreens() {
		if (itemScreen == null) {
			return Collections.emptySet();
		}
		return Collections.singleton(itemScreen);
	}
	
	@Override
	public String getTitle(Object object) {
		return getId();
	}
	
	/* (non-Javadoc)
	 * @see org.riotfamily.core.screen.list.ListScreen#getItemLabel(java.lang.Object)
	 */
	public String getItemLabel(Object object) {
		StringBuilder label = new StringBuilder();
		Pattern p = Pattern.compile("([\\w.]+)(\\W*)");
		Matcher m = p.matcher(getLabelProperty());
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
		return null;
	}
	
	public void setLabelProperty(String labelProperty) {
		this.labelProperty = labelProperty;
	}
	
	private String getLabelProperty() {
		if (labelProperty == null) {
			labelProperty = getFirstProperty();
		}
		return labelProperty;
	}
	
	private String getFirstProperty() {
		if (columns != null && !columns.isEmpty()) {
			return columns.get(0).getProperty();
		}
		return null;
	}
	
	private String getStateKey(ScreenContext context, ChooserSettings chooserSettings) {
		StringBuilder key = new StringBuilder();
		key.append(getId()).append('/');
		if (context.getObjectId() != null) {
			key.append(context.getObjectId());
		}
		else {
			key.append("new");
		}
		if (context.getParentId() != null) {
			key.append('/').append(context.getParentId());
			if (context.isNestedTreeItem()) {
				key.append('/').append(getId());	
			}
		}
		if (chooserSettings.getTargetScreenId() != null) {
			key.append("?choose=").append(chooserSettings.getTargetScreenId());
		}
		return key.toString();
	}
	
	public ListState getOrCreateListState(HttpServletRequest request, 
			ScreenContext screenContext, ChooserSettings chooserSettings) {
		
		String key = getStateKey(screenContext, chooserSettings);
		ListState state = ListState.get(request, key);
		if (state == null) {
			Locale locale = RequestContextUtils.getLocale(request);
			
			Form filterForm = null;
			TextField searchField = null;
			if (filterFormId != null) {
				filterForm = filterFormRepository.createForm(filterFormId);
			}
			if (searchProperties != null) {
				if (filterForm == null) {
					filterForm = new Form();
					filterForm.setBeanClass(HashMap.class);
				}
				searchField = new TextField();
				searchField.setLabel("Search");
				filterForm.addElement(searchField);
			}
			state = new ListState(key, getId(), locale, filterForm, 
					searchField, chooserSettings);
			
			ListState.put(request, key, state);
		}
		return state;
	}
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		
		ScreenContext screenContext = ScreenContext.get(request);
		ChooserSettings chooserSettings = new ChooserSettings(request);
		ModelAndView mv = new ModelAndView(viewName);
		
		if (chooserSettings.getTargetScreenId() != null) {
			List<ScreenLink> path = Generics.newArrayList();
			ScreenContext ctx = screenContext;
			while (ctx != null) {
				if (ctx.getScreen() instanceof ListScreen) {
					path.add(0, chooserSettings.appendTo(ctx.getLink()));
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
