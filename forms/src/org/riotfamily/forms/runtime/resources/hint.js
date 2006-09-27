function toggleHint(id) {
	var el = document.getElementById(id);
	if (el.offsetWidth > 0) {
		el.style.display = 'none';
	}
	else {
		el.style.display = 'block';
		if (!el.onclick) {
			el.onclick = function() {
				this.style.display = 'none';
			}
		}
	}
}