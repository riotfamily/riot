var TinyMCE_SmartQuotesPlugin = {

	getInfo : function() {
		return {
			longname : 'SmartQuotes Plugin',
			author : 'Felix Gnass',
			authorurl : 'http://neteye.de',
			infourl : 'http://neteye.de',
			version : "0.1"
		};
	},

	_quoteChars: {
		ldquo: unescape('%u201C'),
		rdquo: unescape('%u201D'),
		bdquo: unescape('%u201E'),
		lsquo: unescape('%u2018'),
		rsquo: unescape('%u2019'),
		sbquo: unescape('%u201A'),
		laquo: unescape('%AB'),
		raquo: unescape('%BB'),
		lsaquo: unescape('%u2039'),
		rsaquo: unescape('%u203A')
	},

	_quoteStyles: {
		en: {
			left: 'ldquo',
			right: 'rdquo',
			secondaryLeft: 'lsquo',
			secondaryRight: 'rsquo'
		},
		de: {
			left: 'bdquo',
			right: 'ldquo',
			secondaryLeft: 'sbquo',
			secondaryRight: 'lsquo'
		},
		de_alternative: {
			left: 'raquo',
			right: 'laquo',
			secondaryLeft: 'lsaquo',
			secondaryRight: 'rsaquo'
		},
		fr: {
			left: 'laquo',
			right: 'raquo',
			secondaryLeft: 'lsaquo',
			secondaryRight: 'rsaquo'
		}
	},

	/**
	 * Gets executed when a TinyMCE editor instance is initialized.
	 */
	initInstance : function(inst) {
		var plugin = TinyMCE_SmartQuotesPlugin;
		var style = tinyMCE.getParam("smartquotes_quoteStyle", "en");
		if (typeof style == 'string') {
				style = plugin._quoteStyles[style];
		}
		plugin._quoteStyle = style;
		plugin._cleanup = tinyMCE.getParam("smartquotes_cleanup", true);
	},

	/**
	 * Returns the HTML code for a specific control or empty string if this plugin doesn't have that control.
	 */
	getControlHTML : function(cn) {
		return "";
	},

	/**
	 * Executes a specific command, this function handles plugin commands.
	 */
	execCommand : function(editor_id, element, command, user_interface, value) {
		return false;
	},

	/**
	 * Gets called ones the cursor/selection in a TinyMCE instance changes. This is useful to enable/disable
	 * button controls depending on where the user are and what they have selected. This method gets executed
	 * alot and should be as performance tuned as possible.
	 */
	handleNodeChange : function(editor_id, node, undo_index, undo_levels, visual_aid, any_selection) {
	},

	/**
	 * Gets called when a TinyMCE editor instance gets filled with content on startup.
	 */
	setupContent : function(editor_id, body, doc) {
	},

	/**
	 * Gets called when the contents of a TinyMCE area is modified.
	 */
	onChange: function(inst) {
	},

	/**
	 * Gets called when TinyMCE handles events such as keydown, mousedown etc.
	 */
	handleEvent : function(e) {
		if (e.type == "keypress") {
			var code = e.keyCode ? e.keyCode : e.which;		
			var c = String.fromCharCode(code);
			var plugin = TinyMCE_SmartQuotesPlugin;
			if (c == '"') {
				var rng = tinyMCE.selectedInstance.getRng();
				var afterWhitespace = /(^$|\s+)/.test(plugin._getLastCharInRange(rng));
				var style = plugin._quoteStyle;
				var quote = plugin._quoteChars[afterWhitespace ? style.left : style.right];
				plugin._insertText(rng, quote);
				tinyMCE.cancelEvent(e);
				return false;
			}
		}
		return true; // Pass to next handler
	},

	/**
	 * Gets called when HTML contents is inserted or retrived from a TinyMCE editor instance.
	 * The type parameter contains what type of event that was performed and what format the content is in.
	 * Possible valuses for type is get_from_editor, insert_to_editor, get_from_editor_dom, insert_to_editor_dom.
	 */
	cleanup : function(type, content, inst) {
		if (this._cleanup) {
			if (type == "insert_to_editor") {
				content = this._convertQuotes(content);
			}
		}
		return content;
	},

	_getLastCharInRange: function(rng) {
		if (rng.commonAncestorContainer) {
			tinyMCE.selectedInstance.getSel().removeRange(rng);
			rng.setStart(rng.commonAncestorContainer, 0);
			var text = rng.toString();
			rng.collapse(false);
			return text.substring(text.length - 1);
		}
		else if (rng.moveStart) {
			rng.moveStart('character', -1);
			var text = rng.text;
			rng.moveStart('character', 1);
			return text.charAt(0);
		}
		return '';
	},

	_insertText: function(rng, text) {
		if (rng.insertNode) {
			var sel = tinyMCE.selectedInstance.getSel();
			var doc = tinyMCE.selectedInstance.getDoc();
			var n = doc.createTextNode(text);
			var r2 = rng.cloneRange();
			
			// Insert text at cursor position
			sel.removeAllRanges();
			rng.deleteContents();
			rng.insertNode(n);

			// Move the cursor to the end of text
			r2.selectNode(n);
			r2.collapse(false);
			sel.removeAllRanges();
			sel.addRange(r2);
		}
		else {
			rng.text = text;
		}
	},

	_convertQuotes: function(content) {
		content = content.replace(/(^|\s)"/g, '$1' + this._quoteStyle.left);
		content = content.replace(/"([\s.,;!?]|$)/g, this._quoteStyle.left + '$1');
		return content;
	}

};

// Adds the plugin class to the list of available TinyMCE plugins
tinyMCE.addPlugin("smartquotes", TinyMCE_SmartQuotesPlugin);
