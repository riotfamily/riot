package org.riotfamily.pages.mvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface to provide a model for the GenericController.
 */
public interface ModelBuilder {

	Map buildModel(HttpServletRequest request) throws Exception;

}
