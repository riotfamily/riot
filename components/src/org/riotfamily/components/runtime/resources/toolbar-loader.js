var riot = {};

riot.contextPath = '${contextPath}';
riot.path = riot.contextPath + '${riotServletPrefix}';

Resources.loadStyleSheet('style/toolbar.css');
Resources.loadStyleSheet('style/edit-mode.css');

Resources.loadScriptSequence([
	{src: 'prototype/prototype.js', test: 'Prototype'},
	{src: 'riot-js/inheritance.js', test: 'Class.extend'},
	{src: 'riot-js/util.js', test: 'RElement'},
	{src: 'scriptaculous/effects.js', test: 'Effect'},
	{src: 'toolbar.js', test: 'riot.toolbar'},
	{src: 'dwr/engine.js', test: 'dwr.engine'},
	{src: 'dwr/util.js', test: 'dwr.util'},
	{src: 'dwr/interface/ComponentEditor.js', test: 'ComponentEditor'},
	{src: 'scriptaculous/dragdrop.js', test: 'Droppables'},
	{src: 'riot-js/effects.js'},
	{src: 'riot-js/viewport.js'},
	{src: 'riot-js/window-callback.js'},
	{src: 'pages/messages.js'},
	{src: 'inplace.js', test: 'riot.InplaceEditor'},
	{src: 'component.js'}
]);
