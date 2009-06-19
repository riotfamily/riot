<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
      "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<@riot.script src="prototype/prototype.js" />
<style type="text/css">
body {
	background: url(${c.resolve(riot.resource('style/images/publish/toolbar_bg.gif'))}) repeat-x;
	-moz-user-select: none;
}

.button,
.toggle-button {
	display: -moz-inline-stack;
	display: inline-block;
    height: 31px;
	vertical-align: middle;
}
.toggle-button {
	width: 30px;
	background: url(${c.resolve(riot.resource('style/images/publish/mode_icons.png'))}) no-repeat;
	margin-right: 2px;
}
.button {
	font-family: Arial;
	font-size: 11px;
	background: url(${c.resolve(riot.resource('style/images/publish/button_bg.gif'))}) 0 -30px repeat-x;
	margin-left: 4px;
}
.button b {
	background: url(${c.resolve(riot.resource('style/images/publish/button_bg.gif'))}) repeat-x;
	display: block;
    _display: inline-block; /* for IE 6 */
    height: 31px;
	line-height: 31px;
	font-weight: normal;
	margin: 0 1px;
}

.button b b {
	padding-left: 20px;
	margin: 0 12px 0 10px;
	cursor: default;
}

#publish b b {
	background: url(${c.resolve(riot.resource('style/images/publish/action_icons.gif'))}) 0px 0 no-repeat;
}
#discard b b {
	background: url(${c.resolve(riot.resource('style/images/publish/action_icons.gif'))}) 0px -30px no-repeat;
}
#cancel b b {
	background: url(${c.resolve(riot.resource('style/images/publish/action_icons.gif'))}) 0px -60px no-repeat;
}

#publish.disabled b b,
#discard.disabled b b {
	opacity: 0.4;
}

#preview { background-position: -62px 0 }
#preview:hover {background-position: -31px 0 }
#preview.selected { background-position: 0 0 }
#preview.disabled { background-position: -93px 0 }

#live { background-position: -62px -31px }
#live:hover { background-position: -31px -31px }
#live.selected { background-position: 0 -31px }
#live.disabled { background-position: -93px -31px }

#both { background-position: -62px -62px }
#both:hover { background-position: -31px -62px }
#both.selected { background-position: 0 -62px }
#both.disabled { background-position: -93px -62px }

#view {
	float: left;
	display: block;
	padding: 1px 0 0 1px;
}
#actions {
	float: right;
	display: block;
	padding: 1px 2px 0 0;
}
</style>
<script type="text/javascript" language="JavaScript">
var ButtonGroup = Class.create({
	initialize: function(elements, handler) {
		this.handler = handler;
		var group = this;
		this.buttons = elements.map(function(el) { return new GroupButton(el, group) });
	},
	
	buttonSelected: function(button) {
		if (button != this.selectedButton) {
			if (this.selectedButton) {
				this.selectedButton.element.removeClassName('selected');
			}
			this.selectedButton = button;
			return true;
		}
		return false;
	},
	
	buttonClicked: function(button) {
		if (this.buttonSelected(button)) {
			button.element.addClassName('selected');
			this.handler[button.element.id]();
		}
	}
});

var Button = Class.create({
	initialize: function(el, handler) {
		this.element = el;
		this.handler = handler;
		el.observe('click', this.click.bind(this));
	},
	
	click: function() {
		if (!this.element.hasClassName('disabled')) {
			this.handler();
		}
	}
});

var GroupButton = Class.create(Button, {
	initialize: function($super, el, group) {
		$super(el, function() {
			this.group.buttonClicked(this);
		});
		this.group = group;
		if (el.hasClassName('selected')) {
			group.buttonSelected(this);
		}
	}
});

function enable() {
	$$('.disabled').invoke('removeClassName', 'disabled');
}

function disable() {
	$$('.button', '.toggle-button').invoke('addClassName', 'disabled');
}

window.onload = function() {
	viewMode = new ButtonGroup($$('.toggle-button'), {
		preview: function() {
			parent.show(true, false);
			$('publish').removeClassName('disabled');
		},
		live: function() {
			parent.show(false, true);
			$('publish').addClassName('disabled');
		},
		both: function() {
			parent.show(true, true);
			$('publish').removeClassName('disabled');
		}
	});
	
	function reset() {
		viewMode.buttons[0].click();
	}
	
	new Button($('publish'), function() { reset(); parent.publish() });
	new Button($('discard'), function() { reset(); parent.discard() });
	new Button($('cancel'), function() { reset(); parent.hide() });
}
</script>
</head>
<body>
	<span id="view">
		<span id="preview" class="toggle-button disabled selected" title="Preview"></span><#t/>
		<span id="live" class="toggle-button disabled" title="Live"></span><#t/>
		<span id="both" class="toggle-button disabled" title="Both"></span><#t/>
	</span>
	<span id="actions">
		<span id="publish" class="button disabled"><b><b>Publish</b></b></span><#t/>
		<span id="discard" class="button disabled"><b><b>Discard</b></b></span><#t/>
		<span id="cancel" class="button"><b><b>Cancel</b></b></span><#t/>
	</span>
</body>
</html>