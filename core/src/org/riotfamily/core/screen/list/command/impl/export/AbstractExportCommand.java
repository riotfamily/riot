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
package org.riotfamily.core.screen.list.command.impl.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.directwebremoting.io.FileTransfer;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.screen.list.ListParamsImpl;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.DownloadResult;

public abstract class AbstractExportCommand extends AbstractCommand {
	
	@Override
	protected String getIcon() {
		return "table_row_delete";
	}

	public CommandResult execute(CommandContext context, Selection selection) 
			throws IOException {
		
		return new DownloadResult(createFileTransfer(context, selection));
	}

	protected FileTransfer createFileTransfer(
			CommandContext context, Selection selection)
			throws IOException {
		
		return new FileTransfer(
				getFileName(context), 
				getMimeType(),
				getBytes(context, selection));
	}

	private byte[] getBytes(CommandContext context, Selection selection) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		export(context, selection, out);
		return out.toByteArray();
	}

	protected void export(CommandContext context, Selection selection,
			OutputStream out) throws IOException {
		
		export(context, getItems(context, selection), out);
	}
	
	protected void export(CommandContext context, Collection<?> items, OutputStream out) throws IOException {
	}
	
	protected Collection<?> getItems(CommandContext context, Selection selection) {
		ListParams params = new ListParamsImpl(context.getParams());
		return context.getScreen().getDao().list(context.getParent(), params);
	}

	protected String getFileName(CommandContext context) {
		return FormatUtils.toFilename(
				context.getScreenContext().getLink().getTitle() 
				+ "." + getFileExtension());
	}
	
	protected String getFileExtension() {
		return "bin";
	}

	protected String getMimeType() {
		return "application/octet-stream";
	}

}
