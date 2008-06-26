var riot = {
	contextPath: '${contextPath}',
	resourcePath: '${contextPath}${resourcePath}',
	path: '${contextPath}${riotServletPrefix}',
	language: '${language}' || 'en'
};

Resources.basePath = riot.resourcePath;
Resources.loadStyleSheet('style/toolbar.css');
Resources.loadStyleSheet('style/edit-mode.css');

Resources.loadScriptSequence([
	{src: 'dwr/engine.js', test: 'dwr.engine'},
	{src: 'dwr/util.js', test: 'dwr.util'},
	{src: 'dwr/interface/ComponentEditor.js', test: 'ComponentEditor', onload: loadToolbarScripts}
]);

function loadToolbarScripts() {
	var scripts = Resources.getRequiredSources([	
		{src: 'prototype/prototype.js', test: 'Prototype'},
		{src: 'riot-js/util.js', test: 'RElement'},
		{src: 'scriptaculous/effects.js', test: 'Effect'},
		{src: 'swfobject/swfobject.js', test: 'deconcept.SWFObject'},
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
