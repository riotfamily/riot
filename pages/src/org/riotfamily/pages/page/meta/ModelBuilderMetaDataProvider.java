package org.riotfamily.pages.page.meta;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.pages.mvc.ModelBuilder;
import org.riotfamily.pages.mvc.cache.CacheableModelBuilder;
import org.springframework.util.Assert;

public class ModelBuilderMetaDataProvider implements MetaDataProvider {

	private ModelBuilder modelBuilder;
	
	private String modelKey;
	
	private String titleKey;
	
	private String titleProperty;
	
	private String keywordsKey;
	
	private String keywordsProperty;
	
	private String descriptionKey;
	
	private String descriptionProperty;
	
	private CacheableModelBuilder cacheableModelBuilder;
	
	public ModelBuilderMetaDataProvider(ModelBuilder modelBuilder) {
		this.modelBuilder = modelBuilder;
		if (modelBuilder instanceof CacheableModelBuilder) {
			cacheableModelBuilder = (CacheableModelBuilder) modelBuilder;
		}
	}
	
	public void setDescriptionKey(String descriptionKey) {
		this.descriptionKey = descriptionKey;
	}

	public void setKeywordsKey(String keywordsKey) {
		this.keywordsKey = keywordsKey;
	}

	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}

	public void setTitleKey(String titleKey) {
		this.titleKey = titleKey;
	}

	public void setDescriptionProperty(String descriptionProperty) {
		this.descriptionProperty = descriptionProperty;
	}

	public void setKeywordsProperty(String keywordsProperty) {
		this.keywordsProperty = keywordsProperty;
	}

	public void setTitleProperty(String titleProperty) {
		this.titleProperty = titleProperty;
	}

	public MetaData getMetaData(HttpServletRequest request) 
			throws Exception {
		
		Map model = modelBuilder.buildModel(request);
		Object bean;
		
		MetaData metaData = new MetaData();
		
		if (titleKey != null) {
			bean = model.get(titleKey);
			if (titleProperty == null) {
				Assert.isInstanceOf(String.class, bean);
				metaData.setTitle((String) bean);
			}
		}
		else {
			Assert.notNull(modelKey);
			bean = model.get(modelKey);
		}
		if (titleProperty != null) {
			metaData.setTitle(PropertyUtils.getPropertyAsString(
					bean, titleProperty));
		}
		
		if (keywordsKey != null) {
			bean = model.get(keywordsKey);
			if (keywordsProperty == null) {
				Assert.isInstanceOf(String.class, bean);
				metaData.setKeywords((String) bean);
			}
		}
		else {
			Assert.notNull(modelKey);
			bean = model.get(modelKey);
		}
		if (keywordsProperty != null) {
			metaData.setKeywords(PropertyUtils.getPropertyAsString(
					bean, keywordsProperty));
		}
		
		if (descriptionKey != null) {
			bean = model.get(descriptionKey);
			if (descriptionProperty == null) {
				Assert.isInstanceOf(String.class, bean);
				metaData.setDescription((String) bean);
			}
		}
		else {
			Assert.notNull(modelKey);
			bean = model.get(modelKey);
		}
		if (descriptionProperty != null) {
			metaData.setDescription(PropertyUtils.getPropertyAsString(
					bean, descriptionProperty));
		}
		
		return metaData;
	}
	
	public void appendCacheKey(StringBuffer key, 
			HttpServletRequest request) {
		
		if (cacheableModelBuilder != null) {
			cacheableModelBuilder.appendCacheKey(key, request);
		}
	}
	
	public long getLastModified(HttpServletRequest request) {
		if (cacheableModelBuilder != null) {
			return cacheableModelBuilder.getLastModified(request);
		}
		else {
			return -1;
		}
	}
}
