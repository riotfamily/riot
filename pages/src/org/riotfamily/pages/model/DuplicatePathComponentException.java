package org.riotfamily.pages.model;

import org.riotfamily.core.dao.InvalidPropertyValueException;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.5
 */
public class DuplicatePathComponentException extends InvalidPropertyValueException {
	
	private static final String CODE_NAME = "duplicate";

	private static final String FIELD_NAME = "pathComponent";

	private Page page;

	public DuplicatePathComponentException(Page page) {
		super(FIELD_NAME, CODE_NAME, new String[] { page.getPathComponent() },
				"There's already another page using the pathComponent.");
		
		this.page = page;
	}

	public Page getPage() {
		return this.page;
	}

}
