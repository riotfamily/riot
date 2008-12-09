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
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.dbmsgsrc.riot;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.riotfamily.common.log.RiotLog;
import org.riotfamily.dbmsgsrc.dao.DbMessageSourceDao;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.riotfamily.dbmsgsrc.support.DbMessageSource;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.element.upload.FileUpload;
import org.riotfamily.pages.model.Site;
import org.riotfamily.riot.dao.InvalidPropertyValueException;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.list.command.dialog.DialogCommand;
import org.riotfamily.riot.list.ui.ListSession;
import org.riotfamily.riot.security.AccessController;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

public class ImportMessagesCommand extends DialogCommand {

	private static final RiotLog log = RiotLog.get(ImportMessagesCommand.class);
	
	public static final String ACTION_IMPORT = "import";
	
	private DbMessageSourceDao dao;
	
	private String bundle = DbMessageSource.DEFAULT_BUNDLE;
	
	public ImportMessagesCommand(DbMessageSourceDao dao) {
		this.dao = dao;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}
	
	@Override
	public String getAction() {
		return ACTION_IMPORT;
	}
	
	@Override
	public Form createForm(Object bean) {
		Form form = new Form(Upload.class);
		form.setId("importMessagesForm");
		FileUpload fileUpload = new FileUpload();
		fileUpload.setRequired(true);
		form.addElement(fileUpload, "data");
		return form;
	}
	
	@Override
	public ModelAndView handleInput(Object input, Object bean, ListSession listSession) {
		Site site = (Site) EditorDefinitionUtils.loadParent(
					listSession.getListDefinition(), listSession.getParentId());		
		Upload upload = (Upload) input;
		try {
			log.info("Local messages uploaded for site %s by %s", site, AccessController.getCurrentUser().getUserId());
			updateMessages(upload.getData(), site);			
		} 
		catch (OfficeXmlFileException e) {
			throw new InvalidPropertyValueException("data","error.dbmsgsrc.unsupportedExcelVersion", "The Excel version is not support. Please save Excel file as version 97 - 2003");
		}
		catch (IOException e) {			
		}
		return null;
	}
	
	private void updateMessages(byte[] data, Site site) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook(new ByteArrayInputStream(data));
		HSSFSheet sheet = wb.getSheet("Translations");
		
		if (isValid(sheet)) {
			for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
				HSSFRow row = sheet.getRow(i);
				if (row.getCell(1) != null && row.getCell(3) != null) {
					String code = row.getCell(1).getRichStringCellValue().getString();
					String translation = row.getCell(3).getRichStringCellValue().getString();
					if (StringUtils.hasText(translation)) {
						MessageBundleEntry entry = dao.findEntry(bundle, code);
						if (entry != null) {
							entry.addTranslation(site.getLocale(), translation);
							dao.saveEntry(entry);					
						}
						else {
							log.info("Message Code does not exist - " + code);
						}
					}
				}
				else {
					log.info("Skipping invalid row %s" + i);
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
		String translation = headings.getCell(3).getRichStringCellValue().getString();
		return "Code".equals(code) && "Translation".equals(translation);
		
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
