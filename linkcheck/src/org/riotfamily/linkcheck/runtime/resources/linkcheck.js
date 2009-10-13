Resources.loadScriptSequence([
	{src: 'dwr/engine.js', test: 'dwr.engine'},
	{src: 'dwr/util.js', test: 'dwr.util'},
	{src: 'dwr/interface/BrokenLinkService.js', test: 'BrokenLinkService'},
	{src: 'riot-js/util.js'},
	{src: 'brokenlinks.js'}
]);

Resources.loadStyleSheet('style/linkcheck.css');