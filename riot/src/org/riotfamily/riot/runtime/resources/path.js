function getPath() {
	if(top.path && top.path.path) {
		return top.path.path;
	}
	else {
		return null;
	}
}

function updatePath(editorId, objectId, parentId, parentEditorId) {
	var path = getPath();
	if (path != null) path.update(editorId, objectId, parentId, parentEditorId);
}

function subPage(title) {
	var path = getPath();
	if (path != null) path.append('subPage=' + title);
}
