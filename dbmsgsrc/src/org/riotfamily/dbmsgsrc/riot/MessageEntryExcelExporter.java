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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.riotfamily.riot.list.command.export.Exporter;

public class MessageEntryExcelExporter implements Exporter {

	
	public String getFileExtension() {
		return "xls";
	}
	
	public void export(Collection<?> items, Object parent, 
			List<String> properties, HttpServletResponse response) 
			throws IOException {
				
		HSSFWorkbook wb = new WorkbookCreator().createWorkbook(items);
		wb.write(response.getOutputStream());
	}
	
	private final static class WorkbookCreator {
		
		HSSFWorkbook wb;
		
		HSSFSheet sheet; 
		
		HSSFCellStyle locked;
		
		HSSFCellStyle editable;
		
		HSSFCellStyle hidden;
		
		private HSSFWorkbook createWorkbook(Collection<?> items) {
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
			
			createHeadings("Category", "Code", "Default Message", "Comment");
			createRows(items);
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

		private void createRows(Collection<?> items) {
			int i = 1;
			for (Object item : items) {
				MessageBundleEntry entry = (MessageBundleEntry) item;				
				HSSFRow row = sheet.createRow(i++);
				addCell(row, 0, getCategory(entry), editable);
				addCell(row, 1, entry.getCode(), locked);
				addCell(row, 2, entry.getDefaultText(), locked);				
				addCell(row, 3, entry.getComment(), editable);				
			}

			sheet.autoSizeColumn((short) 0);
			sheet.setColumnWidth((short) 1, (short) (25 * 256));
			sheet.setColumnWidth((short) 2, (short) (50 * 256));
			sheet.setColumnWidth((short) 3, (short) (50 * 256));			
		}

		private String getCategory(MessageBundleEntry entry) {
			String category = entry.getCode();
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
