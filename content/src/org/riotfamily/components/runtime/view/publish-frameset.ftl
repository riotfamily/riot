<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<@riot.script src="prototype/prototype.js" />
<script>

var previewUrl;
var liveUrl;

var leftShown = true;
var rightShown = false;

/**
 * Loads the live and preview version of the page into the content frames.
 */
function init(ids) {
	var params = $H(parent.location.search.parseQuery());
	params.set('riotMode', 'live');
	liveUrl = parent.location.pathname + '?' + params.toQueryString();

	params.set('riotMode', 'preview');
	params.set('riotContainer', ids);
	previewUrl = parent.location.pathname + '?' + params.toQueryString();
	
	left.location.replace(previewUrl);
	right.location.replace(liveUrl);
	
	Event.observe('left', 'load', function() {
		parent.riot.components.showPreviewFrame();
		left.onscroll = function(e) {
			right.scrollTo(left.scrollX, left.scrollY)
		}
		if (!leftShown) {
			left.document.body.style.display = 'none';
		}
	});
	
	Event.observe('right', 'load', function() {
		right.onscroll = function(e) {
			left.scrollTo(right.scrollX, right.scrollY)
		}
		Event.observe('right', 'load', function() {
			right.document.body.style.display = 'none';
		});
		publishToolbar.enable();
	});
	
}

function show(showLeft, showRight) {
	var c = $('content');
	
	// Adjust visibility
	left.document.body.style.display = showLeft ? '' : 'none';
	right.document.body.style.display = showRight ? '' : 'none';
	
	if (showLeft && showRight) {
		c.cols = '*,*';
	}
	else {
		if (leftShown && rightShown) {
			// Was split-screen before; an intermediate step is required 
			// to work around rendering issues
			c.cols = '*,*';
			setTimeout(function() {c.cols = showLeft ? '*,0' : '0,*'}, 1);
		}
		else {
			c.cols = showLeft ? '*,0' : '0,*';
		}
	}
	leftShown = showLeft;
	rightShown = showRight;
}

function hide() {
	Event.stopObserving('right', 'load');
	Event.stopObserving('left', 'load');
	parent.riot.components.hidePreviewFrame();
	publishToolbar.disable();
	left.location.replace('about:blank');
}

function publish() {
	parent.riot.components.publish();
	hide();
}

function discard() {
	parent.riot.components.discard();
	hide();
}
</script>
<style type="text/css">
</style>
</head>
<frameset rows="49,*" border="0" frameborder="0" framespacing="0">
	<frame id="publishToolbar" name="publishToolbar" src="publish-toolbar" scrolling="no" noresize="noresize" />
	<frameset id="content" cols="*,0" border="0" frameborder="0" framespacing="0">
		<frame id="left" name="left" />
		<frame id="right" name="right" />
	</frameset>
</frameset>
</html>