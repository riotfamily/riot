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
package org.riotfamily.dbmsgsrc.riot;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.riotfamily.dbmsgsrc.dao.DbMessageSourceDao;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.riotfamily.dbmsgsrc.support.DbMessageSource;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.element.upload.FileUpload;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.dialog.DialogCommand;
import org.riotfamily.riot.list.ui.ListSession;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

public class ImportMessageEntriesCommand extends DialogCommand {

public static final String ACTION_IMPORT = "import";

	private static final Log log = LogFactory.getLog(ImportMessageEntriesCommand.class);

	private DbMessageSourceDao dao;
	
	private String bundle = DbMessageSource.DEFAULT_BUNDLE;
	
	public ImportMessageEntriesCommand(DbMessageSourceDao dao) {
		this.dao = dao;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}
	
	@Override
	protected String getAction(CommandContext context) {
		return ACTION_IMPORT;
	}
	
	@Override
	public Form createForm(Object bean) {
		Form form = new Form(Upload.class);
		FileUpload fileUpload = new FileUpload();
		fileUpload.setRequired(true);
		form.addElement(fileUpload, "data");
		return form;
	}
	
	@Override
	public ModelAndView handleInput(Object input, ListSession listSession) {		
		Upload upload = (Upload) input;
		try {
			updateMessages(upload.getData());
		} catch (IOException e) {			
		}
		return null;
	}
	
	private void updateMessages(byte[] data) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook(new ByteArrayInputStream(data));
		HSSFSheet sheet = wb.getSheet("Translations");
		
		if (isValid(sheet)) {
			for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
				HSSFRow row = sheet.getRow(i);
				String code = row.getCell(1).getRichStringCellValue().getString();
				String defaultMessage = row.getCell(2).getRichStringCellValue().getString();
				String comment = row.getCell(3).getRichStringCellValue().getString();
				if (StringUtils.hasText(defaultMessage) || StringUtils.hasText(comment)) {
					MessageBundleEntry entry = dao.findEntry(bundle, code);
					if (entry != null) {
						entry.getDefaultMessage().setText(defaultMessage);
						entry.setComment(comment);
						dao.saveEntry(entry);					
					}
					else {
						log.info("Message Code does not exist - " + code);
					}
				}				
			}
		}
	}
	
	private boolean isValid(HSSFSheet sheet) {
		if (sheet == null) {
			return false;
		}
		HSSFRow headings = sheet.getRow(0);
		String code = headings.getCell(1).getRichStringCellValue().getString();
		String defaultMessage = headings.getCell(2).getRichStringCellValue().getString();
		String comment = headings.getCell(3).getRichStringCellValue().getString();
		return "Code".equals(code) && "Default Message".equals(defaultMessage)
				&& "Comment".equals(comment);
		
	}
	
	public static class Upload {
		
		private byte[] data;

		public byte[] getData() {
			return data;
		}

		public void setData(byte[] data) {
			this.data = data;
		}
		
	}
}
