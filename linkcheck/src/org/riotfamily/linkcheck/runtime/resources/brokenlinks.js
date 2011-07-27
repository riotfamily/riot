(function() {
	function markBrokenLinks(brokenLinks) {
		$$('a[href]').each(function(link) {
			if (brokenLinks.include(link.href)) {
				var span  = new Element('span').setStyle({position: 'relative', paddingTop: '1px'});
				var marker = new Element('div').addClassName('broken-link-marker').setStyle({position: 'absolute', left: '0px', visibility: 'hidden'});
				span.insert(marker);
				if (link.firstChild) {
					link.insertBefore(span, link.firstChild);
				}
				else {
					link.appendChild(span);
				}
				marker.style.top = -marker.getHeight() + 'px';
				marker.style.visibility = 'visible';
				Element.addClassName(link, 'broken-link');
			}
		});
	}
	BrokenLinkService.getBrokenLinks(window.location.href, markBrokenLinks);
})();