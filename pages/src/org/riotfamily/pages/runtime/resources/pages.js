var riot = {};

riot.contextPath = '${contextPath}';
riot.path = riot.contextPath + '${riotServletPrefix}';

Resources.loadScriptSequence([
	{src: 'prototype/prototype.js', test: 'Prototype'},
	{src: 'scriptaculous/effects.js', test: 'Effect'},
	{src: 'scriptaculous/dragdrop.js', test: 'Droppables'},
	{src: 'dwr/interface/ComponentEditor.js'},
	{src: 'dwr/engine.js', test: 'DWREngine'},
	{src: 'riot-js/util.js'},
	{src: 'riot-js/effects.js'},
	{src: 'riot-js/browserinfo.js'},
	{src: 'riot-js/viewport.js'},
	{src: 'riot-js/window-callback.js'},
	{src: 'pages/messages.js'},
	{src: 'inplace.js'},
	{src: 'component.js'},
	{src: 'toolbar.js'}
]);

Resources.loadStyleSheet('toolbar.css');

if (riotUserStylesheet) {
	Resources.loadStyleSheet(riotUserStylesheet);
}
