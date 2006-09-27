package org.riotfamily.riot.form.ui;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormRepository;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.FormChooserDefinition;
import org.riotfamily.riot.editor.FormDefinition;
import org.riotfamily.riot.list.ListRepository;
import org.riotfamily.riot.list.command.support.CommandExecutor;
import org.springframework.transaction.PlatformTransactionManager;

public class FormChooserController extends FormController {

	public FormChooserController(EditorRepository editorRepository, 
			FormRepository formRepository, 
			PlatformTransactionManager transactionManager,
			CommandExecutor commandExecutor, ListRepository listRepository) {
		
		super(editorRepository, formRepository, transactionManager, 
				commandExecutor, listRepository);
	}
	
	public Class getDefinitionClass() {
		return FormChooserDefinition.class;
	}
	
	protected String getFormId(HttpServletRequest request) {
		String formId = request.getParameter(getFormIdParam());
		if (formId == null) {
			formId = super.getFormId(request);
		}
		return formId;
	}
	
	protected Map createModel(Form form, FormDefinition formDefinition, 
			HttpServletRequest request, HttpServletResponse response) {
		
		Map model = super.createModel(form, formDefinition,request, response);
		FormChooserDefinition chooser =	(FormChooserDefinition) formDefinition;
		model.put("formId", form.getId());
		if (form.isNew()) {
			model.put("options", chooser.createOptions(
					FormUtils.getParentId(form), 
					form.getFormContext().getMessageResolver()));
		}
		return model;
	}
	

}
