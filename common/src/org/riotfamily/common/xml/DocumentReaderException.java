package org.riotfamily.common.xml;

import org.springframework.beans.FatalBeanException;
import org.springframework.core.io.Resource;

public class DocumentReaderException extends FatalBeanException {

	public DocumentReaderException(Resource resource, Throwable cause) {
		super(resource.getDescription(), cause);
	}
	
}
