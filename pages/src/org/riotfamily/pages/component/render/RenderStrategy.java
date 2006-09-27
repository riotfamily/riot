package org.riotfamily.pages.component.render;

import java.io.IOException;

import org.riotfamily.pages.component.ComponentList;

public interface RenderStrategy {

	public void render(String path, String key) throws IOException;
	
	public void render(ComponentList list) throws IOException;
	
}
