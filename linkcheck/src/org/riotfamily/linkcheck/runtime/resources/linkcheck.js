riot.path = '${contextPath}${riotServletPrefix}';
var scripts = [	
   	{src: riot.path + '/engine.js', test: 'dwr.engine'},
	{src: riot.path + '/util.js', test: 'dwr.util'},
	{src: riot.path + '/interface/BrokenLinkService.js', test: 'BrokenLinkService'},
	{src: 'riot-js/util.js'},
	{src: 'brokenlinks.js'}
];   					
riot.Resources.loadScriptSequence(scripts);
riot.Resources.loadStyleSheet('style/linkcheck.css');