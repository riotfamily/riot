package org.riotfamily.riot.editor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.collection.TypeDifferenceComparator;
import org.riotfamily.common.i18n.AdvancedMessageCodesResolver;
import org.riotfamily.forms.FormRepository;
import org.riotfamily.riot.editor.ui.EditorController;
import org.riotfamily.riot.list.ListRepository;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.util.Assert;

/**
 *
 */
public class EditorRepository extends ApplicationObjectSupport {

	private static Log log = LogFactory.getLog(EditorRepository.class);
	
	private Map editorDefinitions = new HashMap();

	private GroupDefinition rootGroupDefinition;
	
	private Collection editorControllers;
	
	private HashMap editorControllersByDefinitionClass = new HashMap();
	
	private ListRepository listRepository;
	
	private FormRepository formRepository;

	private AdvancedMessageCodesResolver messageCodesResolver;

	
	public GroupDefinition getRootGroupDefinition() {
		return this.rootGroupDefinition;
	}

	public void setRootGroupDefinition(GroupDefinition rootGroupDefinition) {
		this.rootGroupDefinition = rootGroupDefinition;
	}

	protected Map getEditorDefinitions() {
		return this.editorDefinitions;
	}

	public void addEditorDefinition(EditorDefinition editorDefinition) {
		String id = editorDefinition.getId();
		Assert.notNull(id, "Editor must have an id.");
		EditorDefinition existingEditor = getEditorDefinition(id);
		if (existingEditor != null) {
			log.info("Overwriting editor " + id);
			if (existingEditor.getParentEditorDefinition() instanceof GroupDefinition) {
				GroupDefinition oldGroup = (GroupDefinition) existingEditor.getParentEditorDefinition();
				oldGroup.getEditorDefinitions().remove(existingEditor);
			}
		}
		editorDefinitions.put(id, editorDefinition);
	}

	public EditorDefinition getEditorDefinition(String editorId) {
		if (editorId == null) {
			return rootGroupDefinition;
		}
		return (EditorDefinition) editorDefinitions.get(editorId);
	}

	public ListDefinition getListDefinition(String editorId) {
		return (ListDefinition) getEditorDefinition(editorId);
	}

	public FormDefinition getFormDefinition(String editorId) {
		return (FormDefinition) getEditorDefinition(editorId);
	}
	
	public GroupDefinition getGroupDefinition(String editorId) {
		return (GroupDefinition) getEditorDefinition(editorId);
	}

	/**
	 * @return Returns the listRepository.
	 */
	public ListRepository getListRepository() {
		return listRepository;
	}

	/**
	 * @param listRepository The listRepository to set.
	 */
	public void setListRepository(ListRepository listRepository) {
		this.listRepository = listRepository;
	}
	
	public FormRepository getFormRepository() {
		return this.formRepository;
	}

	public void setFormRepository(FormRepository formRepository) {
		this.formRepository = formRepository;
	}

	public AdvancedMessageCodesResolver getMessageCodesResolver() {
		return this.messageCodesResolver;
	}

	public void setMessageCodesResolver(AdvancedMessageCodesResolver messageKeyResolver) {
		this.messageCodesResolver = messageKeyResolver;
	}

	public void initApplicationContext() {
		Map map = getApplicationContext().getBeansOfType(EditorController.class);
		editorControllers = map.values();
	}
	
	protected EditorController getEditorController(
			EditorDefinition editorDefinition) {
		
		Class definitionClass = editorDefinition.getClass();
		EditorController controller = (EditorController) 
				editorControllersByDefinitionClass.get(definitionClass);
		
		if (controller == null) {
			EditorControllerComparator comp = 
					new EditorControllerComparator(definitionClass);
			
			TreeSet controllers = new TreeSet(comp);
			controllers.addAll(editorControllers);
			controller = (EditorController) controllers.first();
			editorControllersByDefinitionClass.put(definitionClass, controller);
		}
		
		return controller;
	}
	
	public String getEditorUrl(EditorDefinition editorDefinition, 
			String objectId, String parentId) {
		
		EditorController controller = getEditorController(editorDefinition);
		return controller.getUrl(editorDefinition.getId(), objectId, parentId);
	}
	
	protected static class EditorControllerComparator 
			extends TypeDifferenceComparator {
		
		public EditorControllerComparator(Class definitionClass) {
			super(definitionClass);
		}
		
		public int compare(Object o1, Object o2) {
			EditorController ec1 = (EditorController) o1;
			EditorController ec2 = (EditorController) o2;
			return super.compare(ec1.getDefinitionClass(), 
					ec2.getDefinitionClass());
		}
	}
	
}
