package org.riotfamily.media.processing;

import org.riotfamily.media.model.RiotFile;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 * @deprecated
 */
public abstract class AbstractFileProcessor implements FileProcessor, 
		BeanNameAware, InitializingBean {

	private String beanName;
	
	private String variant;

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}
	
	public void afterPropertiesSet() {
		if (variant == null) {
			variant = beanName;
		}
	}
	
	public void process(RiotFile data) throws FileProcessingException {
		try {
			data.addVariant(variant, createVariant(data));
		}
		catch (Exception e) {
			throw new FileProcessingException(e);
		}
	}
	
	protected abstract RiotFile createVariant(RiotFile data) throws Exception;

}
