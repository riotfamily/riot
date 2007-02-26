var riot = {};

riot.contextPath = '${contextPath}';
riot.path = riot.contextPath + '${riotServletPrefix}';

Resources.loadScriptSequence([
	{src: 'prototype/prototype.js', test: 'Prototype'},
	{src: 'scriptaculous/effects.js', test: 'Effect'},
	{src: 'scriptaculous/dragdrop.js', test: 'Droppables'},
	{src: 'riot-js/util.js'},
	{src: 'riot-js/effects.js'},
	{src: 'riot-js/browserinfo.js'},
	{src: 'riot-js/viewport.js'},
	{src: 'riot-js/window-callback.js'},
	{src: 'dwr/engine.js', test: 'dwr.engine'},
	{src: 'dwr/util.js', test: 'dwr.util'},
	{src: 'dwr/interface/ComponentEditor.js', test: 'ComponentEditor'},
	{src: 'pages/messages.js'},
	{src: 'inplace.js', test: 'riot.InplaceEditor'},
	{src: 'component.js', test: 'riot.ComponentList'},
	{src: 'toolbar.js'}
]);

Resources.loadStyleSheet('style/toolbar.css');
Resources.loadStyleSheet('style/edit-mode.css');
