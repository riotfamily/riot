package org.riotfamily.website.performance;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.filter.FilterPlugin;
import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.RiotLog;

/**
 * Filter plugin that sets a far future Expires header for request URLs that 
 * contain a timestamp. URLs are considered as 'stamped' if they match the 
 * configured pattern.
 * <p>
 * The default pattern is <code>(.*\\/\\d{14}/.+)|(.+\\?[0-9]+$)</code>, this
 * matches URLs created by the DefaultFileStore and URLs printed via the
 * <code>common.resource()</code> FreeMarker function.
 * </p>
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class ExpiresHeaderFilterPlugin extends FilterPlugin {

	private RiotLog log = RiotLog.get(
			ExpiresHeaderFilterPlugin.class);
	
	public static final String DEFAULT_EXPIRATION = "10Y";

	private static final String EXPIRES_HEADER = "Expires";

	private String expiresAfter = DEFAULT_EXPIRATION;

	private long expires;
	
	private ResourceStamper stamper;

	public void setExpiresAfter(String expiresAfter) {
		this.expiresAfter = expiresAfter;
	}
	
	public void setStamper(ResourceStamper stamper) {
		this.stamper = stamper;
	}

	@Override
	protected void initPlugin() {
		if (stamper == null) {
			stamper = new ResourceStamper();
		}
		expires = System.currentTimeMillis()
				+ FormatUtils.parseMillis(expiresAfter);
	}

	@Override
	public void doFilter(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		
		if (isStamped(request)) {
			if (log.isDebugEnabled()) {
				log.debug("Setting Expires header for " 
						+ request.getRequestURI());
			}
			response.setDateHeader(EXPIRES_HEADER, expires);
		}
		filterChain.doFilter(request, response);
	}
	
	protected boolean isStamped(HttpServletRequest request) {
		String url = ServletUtils.getRequestUrlWithQueryString(request);
		return stamper.isStamped(url);
	}
}
