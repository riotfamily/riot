package org.riotfamily.forms.element.collection;

import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.element.TemplateElement;
import org.riotfamily.forms.event.Button;
import org.riotfamily.forms.event.ClickEvent;
import org.riotfamily.forms.event.ClickListener;

public class ListItem extends TemplateElement implements DHTMLElement, ClickListener {
	
	private ListEditor list;

	private Editor editor;
	
	private Button removeButton;
	
	private CollectionItemEditorBinding binding;
			
	public ListItem(ListEditor list) {
		super("item");
		this.list = list;
		binding = new CollectionItemEditorBinding(list.getEditorBinding());
		setWrap(false);
		setTemplate(TemplateUtils.getTemplatePath(ListItem.class));
		removeButton = new Button();
		removeButton.setLabelKey("label.form.list.remove");
		removeButton.setLabel("Remove");
		removeButton.setTabIndex(2);
		removeButton.addClickListener(this);	
		addComponent("removeButton", removeButton);
	}
	
	public ListEditor getList() {
		return list;
	}
	
	public void clicked(ClickEvent event) {					
		list.removeItem(this);
	}
	
	public void setEditor(Editor editor) {
		this.editor = editor;
		editor.setRequired(true);
		editor.setEditorBinding(binding);
		binding.setEditor(editor);
		addComponent("editor", editor);
	}
	
	public Editor getEditor() {
		return editor;
	}
	
	public void setValue(Object value, boolean newItem) {
		binding.setExistingItem(!newItem);
		binding.setValue(value);
		editor.setValue(value);
	}
	
	boolean isNew() {
		return !binding.isEditingExistingBean();
	}
	
	public Object getBackingObject() {
		return binding.getValue();
	}
		
	public Object getValue() {
		return editor.getValue();
	}
	
	public String getInitScript() {
		if (getForm().isRendering()) {
			return null;
		}
		return list.getInitScript();
	}
	
	public void focus() {
		editor.focus();
	}
	
}
