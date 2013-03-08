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
package org.riotfamily.core.screen.list.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.core.dao.Sortable;
import org.riotfamily.core.dao.Tree;
import org.riotfamily.core.screen.list.ColumnConfig;
import org.riotfamily.core.screen.list.ListParamsImpl;
import org.riotfamily.core.screen.list.dto.ListColumn;
import org.riotfamily.core.screen.list.dto.ListItem;
import org.riotfamily.core.screen.list.dto.ListModel;
import org.riotfamily.forms.controller.FormContextFactory;

/**
 * List service handler that builds the complete list model, including column 
 * headings.
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class ListModelBuilder extends ListItemLoader {

	private FormContextFactory formContextFactory;
	
	public ListModelBuilder(ListService service, String key, 
			HttpServletRequest request) {
		
		super(service, key, request);
		formContextFactory = service.getFormContextFactory();
	}
	
	public ListModel buildModel() {
		return buildModel(null);
	}
	
	public ListModel buildModel(String expandedId) {
		if (state.getFilterForm() != null) {
			if (!state.isInitialized()) {
				state.setFormContext(formContextFactory.createFormContext(
						messageResolver, getContextPath(), null));
			}
		}
		List<ListItem> items = createItems(expandedId);
		ListParamsImpl params = state.getParams();
		int itemsTotal = dao.getListSize(getParent(), params);
		params.adjust(itemsTotal);
		
		ListModel model = new ListModel(items, itemsTotal, params);
		model.setColumns(createColumns());
		model.setCommandButtons(createButtons());
		model.setTree(dao instanceof Tree);
		model.setCssClass(screen.getId());
		model.setExpandedId(expandedId);
		//model.setInstantAction(chooser || singleAction);
		
		if (state.getFilterForm() != null) {
			StringWriter writer = new StringWriter();
			state.getFilterForm().render(new PrintWriter(writer));
			model.setFilterFormHtml(writer.toString());
		}
		
		return model;
	}
	
	public ListModelBuilder gotoPage(int page) {
		state.getParams().setPage(page);
		return this;
	}
	
	public ListModelBuilder sort(String property) {
		ColumnConfig col = screen.findColumn(property);
		state.getParams().orderBy(property, col.isAscending(), col.isCaseSensitive());
		return this;
	}
	
	public ListModelBuilder filter(Map<String, String> filter) {
		state.setFilter(filter);
		return this;
	}
	
	private List<ListColumn> createColumns() {
		ListParamsImpl params = state.getParams();
		ArrayList<ListColumn> listColumns = Generics.newArrayList();
		Iterator<ColumnConfig> it = screen.getColumns().iterator();
		int i = 0;
		while (it.hasNext()) {
			ColumnConfig config = it.next();
			ListColumn column = new ListColumn();
			column.setProperty(config.getProperty());
			column.setHeading(getHeading(config.getProperty(), config.getLookupLevel(), i++));
			column.setSortable(canSortBy(config));
			column.setCssClass(FormatUtils.toCssClass(config.getProperty()));
			if (params.hasOrder() && params.getPrimaryOrder()
					.getProperty().equals(config.getProperty())) {

				column.setSorted(true);
				column.setAscending(params.getPrimaryOrder().isAscending());
			}
			listColumns.add(column);
		}
		return listColumns;
	}
	
	private boolean canSortBy(ColumnConfig column) {
		if (column.isSortable() && dao instanceof Sortable) {
			return ((Sortable) dao).canSortBy(column.getProperty());
		}
		return false;
	}
	
	private String getHeading(String property, int lookupLevel, int columnIndex) {
		return getHeading(dao.getEntityClass(), property, 
				lookupLevel, columnIndex);
	}

	private String getHeading(Class<?> clazz, String property, 
			int lookupLevel, int columnIndex) {
		
		if (property == null) {	
			return messageResolver.getPropertyLabelWithoutDefault(
					screen.getId(), clazz, String.valueOf(columnIndex));			
		}
		if (clazz != null) {
			String root = property;
			int pos = property.indexOf('.');
			if (pos > 0) {
				root = property.substring(0, pos);
			}
			if (lookupLevel > 1) {
				clazz = PropertyUtils.getPropertyType(clazz, root);
				if (pos > 0) {
					String nestedProperty = property.substring(pos + 1);
					return getHeading(clazz, nestedProperty, 
							lookupLevel - 1, columnIndex);
				}
				else {
					return messageResolver.getClassLabel(null, clazz);
				}
			}
			else if (lookupLevel == 0) {
				return messageResolver.getPropertyLabel(screen.getId(), clazz, root);
			}
		}
		return messageResolver.getPropertyLabel(screen.getId(), clazz, property);
	}
	
}
