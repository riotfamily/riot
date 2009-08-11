package org.riotfamily.media.processing;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 * @deprecated
 */
public class FileProcessingException extends RuntimeException {

	public FileProcessingException() {
	}

	public FileProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileProcessingException(String message) {
		super(message);
	}

	public FileProcessingException(Throwable cause) {
		super(cause);
	}

}
