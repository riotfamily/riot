var riot = {
	contextPath: '${contextPath}',
	path: '${contextPath}${riotServletPrefix}',
	language: '${language}' || 'en',
	instantPublish: window.riotInstantPublish || false
};

riot.componentEditorResource = riot.instantPublish
		? {src: 'dwr/interface/InstantComponentEditor.js', test: 'InstantComponentEditor', onload: function() {window.ComponentEditor = InstantComponentEditor}}
		: {src: 'dwr/interface/ComponentEditor.js', test: 'ComponentEditor'}

Resources.loadStyleSheet('style/toolbar.css');
Resources.loadStyleSheet('style/edit-mode.css');

Resources.loadScriptSequence([
	{src: 'dwr/engine.js', test: 'dwr.engine'},
	{src: 'dwr/util.js', test: 'dwr.util'},
	riot.componentEditorResource,
	{src: 'dwr/interface/EntityEditor.js', test: 'EntityEditor', onload: loadToolbarScripts}
]);

function loadToolbarScripts() {
	var scripts = Resources.getRequiredSources([	
		{src: 'prototype/prototype.js', test: 'Prototype'},
		{src: 'riot-js/util.js', test: 'RElement'},
		{src: 'scriptaculous/effects.js', test: 'Effect'},
		{src: 'toolbar.js'},
		{src: 'swfupload/swfupload.js', test: 'SWFUpload'},
		{src: 'scriptaculous/dragdrop.js', test: 'Droppables'},
		{src: 'riot-js/effects.js'},
		{src: 'riot-js/window-callback.js'},
		{src: 'inplace.js'},
		{src: 'components.js'}
	]);
	Resources.insertScript(riot.path + 
			'/joined/${riotVersion}/joined-script.js?files=' 
			+ scripts.join(',') + '&lang=' + riot.language); 
}
