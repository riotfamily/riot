function markBrokenLinks(brokenLinks) {
	$$('a[href]').each(function(link) {
		if (brokenLinks.include(link.href)) {
			var span  = RBuilder.node('span', {style: {position: 'relative', paddingTop: '1px'}});
			var marker = RBuilder.node('div', {className: 'broken-link-marker', parent: span, style: {position: 'absolute', left: '0px', visibility: 'hidden'}});
			RElement.prependChild(link, span);
			marker.style.top = -marker.getHeight() + 'px';
			marker.style.visibility = 'visible';
			Element.addClassName(link, 'broken-link');
		}
	});
}

BrokenLinkService.getBrokenLinks(window.location.href, markBrokenLinks);