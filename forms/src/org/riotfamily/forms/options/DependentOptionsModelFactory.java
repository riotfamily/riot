package org.riotfamily.forms.options;

import java.util.Collection;

import org.riotfamily.forms.Editor;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.OptionsModelFactory;
import org.riotfamily.forms.element.select.SelectElement;
import org.riotfamily.forms.event.ChangeEvent;
import org.riotfamily.forms.event.ChangeListener;
import org.springframework.util.Assert;

public class DependentOptionsModelFactory implements OptionsModelFactory {

	public boolean supports(Object model) {
		return model instanceof DependentOptionsModel;
	}
	
	@SuppressWarnings("unchecked")
	public OptionsModel createOptionsModel(Object model, Element element) {
		Assert.isInstanceOf(SelectElement.class, element);
		DependentOptionsModel dop = (DependentOptionsModel) model;
		Editor parent = element.getForm().getEditor(dop.getParentProperty());
		return new ChildOptionsModel(parent, (SelectElement) element, dop);
	}

	private static class ChildOptionsModel implements OptionsModel, ChangeListener {

		private Editor parent;
		
		private SelectElement child;
		
		private DependentOptionsModel<Object> dop;
		
		public ChildOptionsModel(Editor parent, SelectElement child, 
				DependentOptionsModel<Object> dop) {
			
			this.parent = parent;
			this.child = child;
			this.dop = dop;
			parent.addChangeListener(this);
		}

		public void valueChanged(ChangeEvent event) {
			child.reset();
		}
		
		public Collection<?> getOptionValues(Element element) {
			return dop.getOptionValues(parent.getValue()); 
		}
		
	}
}
