package org.riotfamily.pages.component.property;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.file.FileStore;

/**
 * PropertyProcessor for URIs resolvable by a {@link FileStore}. When a 
 * component model is copied, the referenced file is also copied and the new
 * file URI is put into the target model. Upon deletion the file is also deleted. 
 */
public class FileStoreProperyProcessor extends AbstractSinglePropertyProcessor {

	private static Log log = LogFactory.getLog(FileStoreProperyProcessor.class);
	
	private FileStore fileStore;
	
	public FileStoreProperyProcessor() {
	}

	public FileStoreProperyProcessor(String property, FileStore fileStore) {
		this.fileStore = fileStore;
		setProperty(property);
	}

	public void setFileStore(FileStore fileStore) {
		this.fileStore = fileStore;
	}

	protected String copy(String s) {
		if (s != null) {
			try {
				return fileStore.copy(s);
			}
			catch (IOException e) {
				log.error("Error copying file", e);
			}
		}
		return null;
	}

	protected void delete(String s) {
		if (s != null) {
			fileStore.delete(s);
		}
	}

	protected Object resolveString(String s) {
		return s;
	}
	
	protected String convertToString(Object object) {
		return (String) object;
	}

}
