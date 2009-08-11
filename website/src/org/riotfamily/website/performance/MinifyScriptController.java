package org.riotfamily.website.performance;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class MinifyScriptController extends AbstractMinifyController {

	private YUIJavaScriptCompressor compressor;

	public MinifyScriptController(YUIJavaScriptCompressor compressor) {
		this.compressor = compressor;
	}
	
	protected Compressor getCompressor() {
		return compressor;
	}
	
	protected String getContentType() {
		return "text/javascript";
	}
	
}
