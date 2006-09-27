function getPath() {
	if(parent && parent.path && parent.path.path) {
		return parent.path.path;
	}
	else {
		return null;
	}
}

function updatePath(editorId, objectId, parentId) {
	var path = getPath();
	if (path != null) path.update(editorId, objectId, parentId);
}

function subPage(title) {
	var path = getPath();
	if (path != null) path.append('subPage=' + title);
}
