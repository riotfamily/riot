/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.dbmsgsrc.riot;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.riotfamily.core.dao.InvalidPropertyValueException;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.dialog.DialogCommand;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.dbmsgsrc.DbMessageSource;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.element.upload.FileUpload;
import org.riotfamily.pages.model.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class ImportMessagesCommand extends DialogCommand {

	private static final Logger log = LoggerFactory.getLogger(ImportMessagesCommand.class);
	
	private String bundle = DbMessageSource.DEFAULT_BUNDLE;
	
	public void setBundle(String bundle) {
		this.bundle = bundle;
	}
	
	@Override
	protected String getIcon() {
		return "table_row_insert";
	}
	
	@Override
	public Form createForm(CommandContext context, Selection selection) {
		Form form = new Form(Upload.class);
		form.setId("importMessagesForm");
		FileUpload fileUpload = new FileUpload();
		fileUpload.setRequired(true);
		form.addElement(fileUpload, "data");
		addButton(form, "import");
		return form;
	}
	
	@Override
	public CommandResult handleInput(CommandContext context,
			Selection selection, Object input, String button) {

		Site site = (Site) context.getParent();		
		Upload upload = (Upload) input;
		try {
			RiotUser user = AccessController.getCurrentUser();
			log.info("Local messages uploaded for site {} by {}", site, user.getUserId());
			updateMessages(upload.getData(), site);			
		} 
		catch (OfficeXmlFileException e) {
			throw new InvalidPropertyValueException("data","error.dbmsgsrc.unsupportedExcelVersion", "The Excel version is not support. Please save Excel file as version 97 - 2003");
		}
		catch (IOException e) {			
		}
		return new RefreshListResult();
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
						MessageBundleEntry entry = MessageBundleEntry.loadByBundleAndCode(bundle, code);
						if (entry != null) {
							entry.addTranslation(site.getLocale(), translation);
							entry.save();					
						}
						else {
							log.info("Message Code does not exist - " + code);
						}
					}
				}
				else {
					log.info("Skipping invalid row {}" , i);
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
