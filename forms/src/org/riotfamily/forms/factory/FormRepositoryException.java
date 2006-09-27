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
