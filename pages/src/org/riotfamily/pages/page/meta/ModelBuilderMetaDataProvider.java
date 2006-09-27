package org.riotfamily.pages.page.meta;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.pages.mvc.ModelBuilder;
import org.riotfamily.pages.page.Page;

public class ModelBuilderMetaDataProvider extends InheritingMetaDataProvider {

	private Log log = LogFactory.getLog(ModelBuilderMetaDataProvider.class);
	
	private ModelBuilder modelBuilder;
	
	private String modelKey;
	
	private String title;
	
	public void setModelBuilder(ModelBuilder modelBuilder) {
		this.modelBuilder = modelBuilder;
	}

	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public MetaData createMetaData(Page page, HttpServletRequest request) {
		MetaData meta = new MetaData();
		try {
			Map model = modelBuilder.buildModel(request);
			if (model != null) {
				Object object = model.get(modelKey);
				meta.setTitle(PropertyUtils.getPropertyAsString(object, title));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			log.error("Error building page meta data.", e);
		}
		return meta;
	}

}
