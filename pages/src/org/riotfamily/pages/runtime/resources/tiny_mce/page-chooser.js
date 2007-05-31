function riotPageChooser(field_name, url, type, win) {
    tinyMCE.openWindow({
        file: riot.path + '/pages/chooser?mode=tinyMCE',
        width: 400, height: 500, close_previous: 'no'
    }, {
        window: win, input: field_name, resizable: 'yes',
        editor_id: tinyMCE.getWindowArg('editor_id')
    });
    return false;
}

Resources.waitFor('riot.tinyMCEConfig', function() {
	riot.tinyMCEConfig.file_browser_callback = 'riotPageChooser';
});