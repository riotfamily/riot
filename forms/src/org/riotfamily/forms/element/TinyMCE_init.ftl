tinyMCE.init({
	mode: 'none',
	language: '${element.language}',
	add_unload_trigger: false,
	strict_loading_mode: true,
	plugins: 'autocleanup',
	valid_elements: '+a[href|target|name],-strong/b,-em/i,h3/h2/h1,h4/h5/h6,p,br,hr,ul,ol,li,blockquote',
	theme: 'advanced',
	theme_advanced_layout_manager: 'RowLayout',
	theme_advanced_containers_default_align: 'left',
	theme_advanced_container_buttons1: 'formatselect,bold,italic,sup,bullist,numlist,outdent,indent,hr,link,unlink,anchor,code,undo,redo,charmap,fullscreen',
	theme_advanced_containers: 'buttons1, mceEditor',
	theme_advanced_blockformats: 'p,h3,h4',
	forced_root_block: 'p'
});

tinyMCE.addControl('${element.id}');
