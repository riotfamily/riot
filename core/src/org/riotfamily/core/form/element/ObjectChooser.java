package org.riotfamily.core.form.element;

import java.io.PrintWriter;

import org.riotfamily.common.mapping.HandlerUrlUtils;
import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.ScreenRepository;
import org.riotfamily.forms.element.select.AbstractChooser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ObjectChooser extends AbstractChooser 
		implements ApplicationContextAware {

	private String rootId;
	
	private String targetId;
	
	private ScreenRepository screenRepository;
	
	private ListScreen rootList;
	
	private ListScreen targetList;

	private ApplicationContext applicationContext;
	
	public ObjectChooser(ScreenRepository screenRepository) {
		this.screenRepository = screenRepository;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	
	public void setRootId(String rootId) {
		this.rootId = rootId;
	}
	
	@Override
	protected void afterFormSet() {
		targetList = screenRepository.getScreen(targetId, ListScreen.class);
		if (rootId != null) {
			rootList = screenRepository.getScreen(rootId, ListScreen.class);
		}
	}
	
	@Override
	protected String getChooserUrl() {
		return ServletUtils.addParameter(
				HandlerUrlUtils.getUrlResolver(applicationContext)
				.getUrlForHandler(rootList.getId(),
				new ScreenContext(null, null, null, null, false)),
				"choose", targetId);
	}

	@Override
	protected Object loadBean(String objectId) {
		return targetList.getDao().load(objectId);
	}

	@Override
	protected void renderLabel(Object object, PrintWriter writer) {
		writer.print(targetList.getItemLabel(object));
	}

}
