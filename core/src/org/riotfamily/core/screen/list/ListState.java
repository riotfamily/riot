package org.riotfamily.core.screen.list;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormContext;
import org.riotfamily.forms.element.TextField;
import org.riotfamily.forms.request.SimpleFormRequest;

/**
 * State of a list screen. Instances are stored in the HTTP session.
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class ListState implements Serializable {

	private String key;
	
	private String screenId;
	
	private Locale locale;
	
	private Form filterForm;
	
	private String parentId;
	
	private TextField searchField;
	
	private ListParamsImpl params = new ListParamsImpl();
	
	private ChooserSettings chooserSettings;
	
	public static ListState get(HttpServletRequest request, String key) {
		return (ListState) request.getSession().getAttribute(key);
	}
	
	public static void put(HttpServletRequest request, String key, 
			ListState state) {
		
		request.getSession().setAttribute(key, state);
	}
	
	ListState(String key, String screenId, Locale locale,
			String parentId, Form filterForm, TextField searchField, 
			ChooserSettings chooserSettings) {
		
		this.key = key;
		this.screenId = screenId;
		this.locale = locale;
		this.parentId = parentId;
		this.filterForm = filterForm;
		this.searchField = searchField;
		this.chooserSettings = chooserSettings;
	}

	public boolean isInitialized() {
		return filterForm == null || filterForm.getFormContext() != null;
	}
	
	public void setFormContext(FormContext formContext) {
		if (filterForm != null) {
			filterForm.setFormContext(formContext);
			filterForm.setTemplate(ResourceUtils.getPath(getClass(), "FilterForm.ftl"));
			for (Element e : filterForm.getRegisteredElements()) {
				e.setRequired(false);
			}
			params.setFilteredProperties(filterForm.getEditorBinder()
					.getBoundProperties());

			params.setFilter(filterForm.populateBackingObject());
		}
	}
	
	public String getKey() {
		return key;
	}

	public String getScreenId() {
		return screenId;
	}

	public String getParentId() {
		return parentId;
	}

	public Locale getLocale() {
		return locale;
	}

	public Form getFilterForm() {
		return filterForm;
	}

	public TextField getSearchField() {
		return searchField;
	}

	public ListParamsImpl getParams() {
		return params;
	}
	
	public ChooserSettings getChooserSettings() {
		return chooserSettings;
	}
	
	public void setFilter(Map<String, String> filter) {
		if (filterForm != null) {
			filterForm.processRequest(new SimpleFormRequest(filter));
			params.setFilter(filterForm.populateBackingObject());
			if (searchField != null) {
				params.setSearch(searchField.getText());
			}
		}
		params.setPage(1);
	}

}
