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

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.list.ListState;
import org.riotfamily.core.screen.list.TreeListScreen;
import org.riotfamily.core.screen.list.service.ListService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

public class ExportController {
	
	private ListService listService;

	public ExportController(ListService listService) {
		super();
		this.listService = listService;
	}

	@RequestMapping
	public void export(@PathVariable("commandId") String commandId,
			@PathVariable("screenId") String screenId,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		TreeListScreen screen = listService.getScreenRepository().getScreen(
				screenId, TreeListScreen.class);
		
		ExportCommand command = (ExportCommand) screen.getCommandMap().get(commandId);
		Exporter exporter = command.getExporter();
		
		String prefix = FormatUtils.toFilename(screen.getId());
		String suffix = exporter.getFileExtension();
		String fileName = String.format("%s-%tF.%s", prefix, new Date(), suffix);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		
		ListState listState = ListState.get(request, 
				screen.getListStateKey(ScreenContext.get(request)));
		
		Object parent = screen.getParentScreen();
		Collection<?> items = screen.getDao().list(parent, listState.getParams());
		exporter.export(null , items, parent, command.getProperties(), response);
	}
}
