package org.riotfamily.pages.component.editor;

import java.util.List;
import java.util.Map;

import org.riotfamily.pages.component.context.RequestContextExpiredException;

public interface ComponentEditor {

	public static final String INSTANT_PUBLISH_ATTRIBUTE =
			ComponentEditor.class.getName() + ".instantPublish";
	
	public static final String POSTION_CLASS_PREFIX = "component-";

	public static final String LAST_COMPONENT_CLASS = "last-component";

	public String getText(Long containerId, String property);

	public String updateText(String controllerId, Long containerId,
			String property, String text)
			throws RequestContextExpiredException;

	public ComponentInfo[] updateTextChunks(String controllerId, 
			Long containerId, String property, String[] chunks) 
			throws RequestContextExpiredException;

	public List getValidTypes(String controllerId);

	public ComponentInfo insertComponent(String controllerId, Long listId,
			int position, String type, Map properties)
			throws RequestContextExpiredException;

	public ComponentInfo setType(String controllerId, Long containerId,
			String type) throws RequestContextExpiredException;

	public String getHtml(String controllerId, Long containerId)
			throws RequestContextExpiredException;

	public String getLiveListHtml(String controllerId, Long listId);

	public String getPreviewListHtml(String controllerId, Long listId);

	public void moveComponent(Long containerId, Long nextContainerId);

	public void deleteComponent(Long containerId);

	public List getDirtyListIds(Long[] listIds);

	public void publishLists(Long[] listIds);

	public void discardLists(Long[] listIds);

	public void discardList(Long listId);

	public String discardListAndGetPreviewHtml(String controllerId,
			Long listId);

	public void publishList(Long listId);

	public void keepAlive();
	
	public boolean isInstantPublishMode() throws RequestContextExpiredException;

	public void logout();

}