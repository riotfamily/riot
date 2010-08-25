(function() {

	riot.contextPath = '${contextPath}';
	riot.resourcePath = '${contextPath}${resourcePath}';
	riot.path = '${contextPath}${riotServletPrefix}';
	riot.language = '${language}' || 'en';
	
	riot.Resources.setBasePath(riot.resourcePath)
		.loadStyleSheet('style/toolbar.css')
		.loadStyleSheet('style/edit-mode.css')
		.loadStyleSheet('riot/window/dialog.css')
		.loadScriptSequence([
			{src: riot.path + '/engine.js', test: 'dwr.engine'},
			{src: riot.path + '/util.js', test: 'dwr.util'},
			{src: riot.path + '/interface/ComponentEditor.js', test: 'ComponentEditor', onload: loadToolbarScripts}
		]);
	
	function loadToolbarScripts() {
		var scripts = [	
   			{src: 'prototype/prototype.js', test: 'Prototype'},
			{src: 'riot/util.js', test: 'RElement'},
			{src: 'scriptaculous/effects.js', test: 'Effect'},
			{src: 'scriptaculous/dragdrop.js', test: 'Droppables'},
			{src: 'riot/cookiejar.js', test: 'CookieJar'},
			{src: 'toolbar.js', test: 'riot.toolbar'},
			{src: 'riot/effects.js', test: 'Effect.Remove'},
			{src: 'riot/window/dialog.js', test: 'riot.window'},
			{src: 'inplace.js', test: 'riot.InplaceEditor'},
			{src: 'components.js', test: 'riot.components', onload: activateToolbar}
		]; 
					
		if (location.href.indexOf('debug-scripts') != -1) {
			riot.Resources.loadScriptSequence(scripts);
		}
		else {
			var files = riot.Resources.getRequiredSources(scripts);
			var src = riot.resourcePath + 'joined.js?files=' + files.join(',') + '&lang=' + riot.language;
			riot.Resources.loadScript(src, 'riot.toolbar', activateToolbar);
		}
	}
	
	function activateToolbar() {
		riot.components.init();
	}
	
})();