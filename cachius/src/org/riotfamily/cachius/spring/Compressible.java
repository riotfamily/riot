package org.riotfamily.cachius.spring;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.CacheService;

/**
 * Interface that can be implemented by controllers that deliver compressible
 * content.
 * 
 * @see <a href="http://developer.yahoo.com/performance/rules.html#gzip">Best Practices for Speeding Up Your Web Site</a>
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public interface Compressible {

	/**
	 * Returns whether the response should be gzipped. Implementors will 
	 * usually return <code>true</code>, unless they serve multiple content
	 * types which and not all of them are eligible for compression. They 
	 * <strong>don't have to</strong> check whether the client supports gzip 
	 * compression as all compatibility checks are done by Cachius 
	 * {@link CacheService#responseCanBeZipped(HttpServletRequest) internally}. 
	 */
	public boolean gzipResponse(HttpServletRequest request);

}
