package org.riotfamily.components.config;

public class ContentFormUrlServiceDelegate implements ContentFormUrlService {

	private ContentFormRepository repository;
	
	public void setContentFormRepository(ContentFormRepository repository) {
		this.repository = repository;
	}
	
	public String getContentFormUrl(String formId, Long containerId, Long contentId) {
		return repository.getContentFormUrl(formId, containerId, contentId);
	}
	
}
