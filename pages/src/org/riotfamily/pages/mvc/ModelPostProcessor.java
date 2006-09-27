package org.riotfamily.pages.mvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface to post process a model built by a ModelBuilder.
 * 
 * @see GenericController
 * @see ModelBuilder
 */
public interface ModelPostProcessor {

	public void postProcess(Map model, HttpServletRequest request)
			throws Exception;
}
