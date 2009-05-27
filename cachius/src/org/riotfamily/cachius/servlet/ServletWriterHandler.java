package org.riotfamily.cachius.servlet;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.CacheItem;

public abstract class ServletWriterHandler extends SessionIdCacheHandler {

	private Writer out;
	
	public ServletWriterHandler(HttpServletRequest request, Writer out, 
			CacheKeyAugmentor cacheKeyAugmentor) {
		
		super(request, cacheKeyAugmentor);
		this.out = out;
	}

	public void handleUncached() throws Exception {
		render(out);
	}
	
	@Override
	protected boolean updateCacheItemInternal(CacheItem cacheItem)
			throws Exception {
		
		Writer itemWriter = cacheItem.getWriter();
		if (getSessionIdEncoder().urlsNeedEncoding()) {
           	itemWriter = getSessionIdEncoder().createIdRemovingWriter(itemWriter);
        }
		render(itemWriter);
		itemWriter.close();
		return true;
	}
	
	protected abstract void render(Writer out) throws Exception;

	public final void writeCacheItem(CacheItem cacheItem) throws IOException {
		Writer writer = out;
		if (getSessionIdEncoder().urlsNeedEncoding()) {
           	writer = getSessionIdEncoder().createIdInsertingWriter(out);
        }
		cacheItem.writeTo(writer);
	}

}
