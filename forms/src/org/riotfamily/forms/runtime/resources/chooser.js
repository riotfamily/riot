function Chooser(id) {
	this.id = id;
	this.element = document.getElementById(id);
	var button = this.element.getElementsByTagName('button');
	for (var i = 0; i < button.length; i++) {
		button[i].chooser = this;
		if (button[i].className == 'choose') {
			button[i].onclick = function() {
				var c = this.chooser;
				c.popup = window.open('?_content=' + c.id, 'chooser', 'width=800,height=400,dependent=yes,toolbar=no,location=no,menubar=no,status=no');
				c.popup.chooser = c;
				return false;
			}
		}
		else if (button[i].className == 'unset') {
			button[i].onclick = function() {
				this.chooser.chosen(null);
				return false;
			}
		}
	}
}

Chooser.prototype.chosen = function(objectId) {
	this.value = objectId || '';
	var changeEvent = new ChangeEvent(this);
	// Defer execution - simultanious XmlHttpRequests seem to fail
	setTimeout(function() { submitEvent(changeEvent) }, 1);
	if (this.popup) this.popup.close();
}
