function riotPageChooser(field_name, url, type, win) {
    tinyMCE.activeEditor.windowManager.open({
        file: riot.path + '/pages/chooser?mode=tinyMCE',
        width: 400, height: 500, resizable: 'yes', close_previous: 'no'
    }, {input: win.document.getElementById(field_name)});
    return false;
}

Resources.waitFor('riot.tinyMCEConfig', function() {
	riot.tinyMCEConfig.file_browser_callback = 'riotPageChooser';
});