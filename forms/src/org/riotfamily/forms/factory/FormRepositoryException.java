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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.factory;

import org.springframework.core.io.Resource;

/**
 * Exception thrown when a FormRepository encounters an internal error, and
 * its definitions are invalid: for example, if an XML document containing
 * form definitions isn't well-formed.
 */
public class FormRepositoryException extends FormDefinitionException {

	private String resourceDescription;

	private String formId;
	
	
	public FormRepositoryException(String msg) {
		super(msg);
	}

	public FormRepositoryException(String msg, Throwable ex) {
		super(msg, ex);
	}
	
	/**
	 * Create a new FormRepositoryException.
	 * @param documentLocation descriptor of the resource
	 * location that the form definition came from
	 * @param formId the id of the form requested
	 * @param msg the detail message
	 */
	public FormRepositoryException(Resource documentLocation, 
			String formId, String msg) {
		
		this(documentLocation.getDescription(), formId, msg, null);
	}

	/**
	 * Create a new FormRepositoryException.
	 * @param documentLocation descriptor of the resource
	 * location that the form definition came from
	 * @param formId the id of the form requested
	 * @param msg the detail message
	 * @param ex the root cause
	 */
	public FormRepositoryException(Resource documentLocation, 
			String formId, String msg, Throwable ex) {
		
		this(documentLocation.getDescription(), formId, msg, ex);
	}

	/**
	 * Create a new FormRepositoryException.
	 * @param resourceDescription description of the resource
	 * that the form definition came from
	 * @param formId the id of the form requested
	 * @param msg the detail message
	 */
	public FormRepositoryException(String resourceDescription, 
			String formId, String msg) {
		
		this(resourceDescription, formId, msg, null);
	}

	/**
	 * Create a new FormRepositoryException.
	 * @param resourceDescription description of the resource
	 * that the form definition came from
	 * @param formId the id of the form requested
	 * @param msg the detail message
	 * @param ex the root cause
	 */
	public FormRepositoryException(String resourceDescription, 
			String formId, String msg, Throwable ex) {
		
		super("Error registering form with id '" + formId + "' defined in " 
				+ resourceDescription + ": " + msg, ex);
		
		this.resourceDescription = resourceDescription;
		this.formId = formId;
	}

	/**
	 * Return the description of the resource that the form
	 * definition came from, if any.
	 */
	public String getResourceDescription() {
		return resourceDescription;
	}

	/**
	 * Return the id of the form requested, if any.
	 */
	public String getFormId() {
		return formId;
	}

}
