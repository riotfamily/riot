/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Jan-Frederic Linde [jfl at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.list.export;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.support.ListParamsImpl;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class CsvExportController implements Controller {

	private EditorRepository editorRepository;

	private String encoding = "UTF-8";

	private String delimiter = ";";

	public CsvExportController(EditorRepository editorRepository) {
		this.editorRepository = editorRepository;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		generateCsv(request, response);
		return null;
	}

	public void generateCsv(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String listId = ServletRequestUtils.getStringParameter(request, "listId");
		String commandId = ServletRequestUtils.getStringParameter(request, "commandId");
		String parentId = ServletRequestUtils.getStringParameter(request, "parentId");

		CsvExportCommand command = (CsvExportCommand)
				editorRepository.getListRepository().getCommand(commandId);

		String fileEncoding = encoding;
		if (command.getEncoding() != null) {
			fileEncoding = command.getEncoding();
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

		String fileName = listId + '-' + sdf.format(new Date()) + ".csv";

		response.setContentType("text/csv; charset=" + fileEncoding);
		response.setCharacterEncoding(encoding);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

		PrintWriter out = response.getWriter();

		for (int i = 0; i < command.getProperties().size(); i++) {
			out.print(command.getProperties().get(i));
			out.print(delimiter);
		}
		out.println();

		ListDefinition listDefinition = editorRepository.getListDefinition(listId);
		ListConfig listConfig = listDefinition.getListConfig();

		Object parent = EditorDefinitionUtils.loadParent(listDefinition, parentId);

		ListParams params = new ListParamsImpl();
		Collection<?> beans = listConfig.getDao().list(parent, params);
		Iterator<?> beanIterator = beans.iterator();
		while (beanIterator.hasNext()) {
			BeanWrapper bean = new BeanWrapperImpl(beanIterator.next());
			for (int i = 0; i < command.getProperties().size(); i++) {
				String propertyName = (String) command.getProperties().get(i);
				Object value = bean.getPropertyValue(propertyName);
				if (value instanceof Collection) {
					Iterator<?> it = ((Collection<?>) value).iterator();
					while (it.hasNext()) {
						out.print(it.next());
						out.print(' ');
					}
				}
				else if (value != null) {
					out.print(value);
				}

				out.print(delimiter);
			}
			out.println();
		}
	}

}
