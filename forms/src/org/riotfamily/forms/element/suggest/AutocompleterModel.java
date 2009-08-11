package org.riotfamily.forms.element.suggest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public interface AutocompleterModel {

	public Collection<String> getSuggestions(String search, 
			AutocompleteTextField element, HttpServletRequest request);

}
