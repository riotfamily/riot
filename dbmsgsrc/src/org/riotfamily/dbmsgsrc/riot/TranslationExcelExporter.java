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

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.riotfamily.dbmsgsrc.model.Message;
import org.riotfamily.pages.model.Site;

public class TranslationExcelExporter { //FIXME implements Exporter

	
	public String getFileExtension() {
		return "xls";
	}
	
	public void export(String objectId, Collection<?> items, Object parent, 
			List<String> properties, HttpServletResponse response) 
			throws IOException {
		
		Site site = (Site) parent;
		HSSFWorkbook wb = new WorkbookCreator().createWorkbook(items, site);
		wb.write(response.getOutputStream());
	}
	
	private final static class WorkbookCreator {
		
		HSSFWorkbook wb;
		
		HSSFSheet sheet; 
		
		HSSFCellStyle locked;
		
		HSSFCellStyle editable;
		
		HSSFCellStyle hidden;
		
		private HSSFWorkbook createWorkbook(Collection<?> items, Site site) {
			wb = new HSSFWorkbook();

			sheet = wb.createSheet("Translations");
			//sheet.protectSheet("");

			locked = wb.createCellStyle();
			locked.setLocked(true);
			locked.setWrapText(true);
			locked.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);

			editable = wb.createCellStyle();
			editable.setLocked(false);
			editable.setWrapText(true);
			editable.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
			
			hidden = wb.createCellStyle();
			hidden.setLocked(true);
			
			createHeadings("Category", "Code", "Default Message", "Translation", "Comment");
			createRows(items, site.getLocale());
			return wb;
		}
		
		private void createHeadings(String... labels) {
			HSSFCellStyle style = wb.createCellStyle();
		    HSSFFont font = wb.createFont();
		    font.setFontName("Trebuchet MS");
		    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		    style.setFont(font);
		    
			HSSFRow row = sheet.createRow(0);
			short col = 0;
			for (String label : labels) {
				 HSSFCell cell = row.createCell(col++);
				 cell.setCellStyle(style);
				 cell.setCellValue(new HSSFRichTextString(label));
			}
		}

		private void createRows(Collection<?> items, Locale locale) {
			int i = 1;
			for (Object item : items) {
				Message message = (Message) item;
				String translation = null;
				if (message.getLocale().equals(locale)) {
					translation = message.getText();
				}
				HSSFRow row = sheet.createRow(i++);
				addCell(row, 0, getCategory(message), editable);
				addCell(row, 1, message.getEntry().getCode(), locked);
				addCell(row, 2, message.getEntry().getDefaultText(), locked);
				addCell(row, 3, translation, editable);
				addCell(row, 4, message.getEntry().getComment(), editable);
				addCell(row, 5, message.getText(), hidden);
			}

			sheet.autoSizeColumn((short) 0);
			sheet.setColumnWidth((short) 1, (short) (25 * 256));
			sheet.setColumnWidth((short) 2, (short) (50 * 256));
			sheet.setColumnWidth((short) 3, (short) (50 * 256));
			sheet.setColumnWidth((short) 4, (short) (50 * 256));
			sheet.setColumnHidden((short) 5, true);
		}

		private String getCategory(Message message) {
			String category = message.getEntry().getCode();
			int i = category.indexOf('.');
			if (i != -1) {
				category = category.substring(0, i);
			}
			return category;
		}

		private void addCell(HSSFRow row, int i, String text, HSSFCellStyle style) {
			HSSFCell cell = row.createCell((short) i);
			cell.setCellStyle(style);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			if (text == null) {
				text = "";
			}
			cell.setCellValue(new HSSFRichTextString(text));
		}
		
	}
	
}
