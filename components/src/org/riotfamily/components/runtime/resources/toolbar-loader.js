var riot = {
	contextPath: '${contextPath}',
	resourcePath: '${contextPath}${resourcePath}',
	path: '${contextPath}${riotServletPrefix}',
	language: '${language}' || 'en'
};

Resources.basePath = riot.resourcePath;
Resources.loadStyleSheet('style/toolbar.css');
Resources.loadStyleSheet('style/edit-mode.css');
Resources.loadStyleSheet('riot-js/window/dialog.css');

Resources.loadScriptSequence([
	{src: riot.path + '/engine.js', test: 'dwr.engine'},
	{src: riot.path + '/util.js', test: 'dwr.util'},
	{src: riot.path + '/interface/ComponentEditor.js', test: 'ComponentEditor', onload: loadToolbarScripts}
]);

function loadToolbarScripts() {
	var scripts = Resources.getRequiredSources([	
		{src: 'prototype/prototype.js', test: 'Prototype'},
		{src: 'riot-js/util.js', test: 'RElement'},
		{src: 'scriptaculous/effects.js', test: 'Effect'},
		{src: 'scriptaculous/dragdrop.js', test: 'Droppables'},
		{src: 'toolbar.js'},
		{src: 'riot-js/effects.js'},
		{src: 'riot-js/window/dialog.js'},
		{src: 'inplace.js'},
		{src: 'components.js'}
	]);
	var src = riot.resourcePath + 'joined.js?files=' + scripts.join(',') + '&lang=' + riot.language;
	Resources.loadScript(src, 'riot.toolbar', toolbarScriptsLoaded);
}

function toolbarScriptsLoaded() {
    if (window.onToolbarLoaded) {
    	onToolbarLoaded()
    }
    riot.toolbar.activate();
}