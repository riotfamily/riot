var browserInfo = new function() {
	var agt = navigator.userAgent.toLowerCase();
	this.safari = agt.indexOf('safari') != -1 && agt.indexOf('mac') != -1;
	this.konqueror = agt.indexOf('konqueror') != -1;
	this.khtml = this.safari || this.konqueror;
	this.gecko = !this.khtml && navigator.product && navigator.product.toLowerCase() == 'gecko';
	this.opera = agt.indexOf('opera') != -1;
	this.ie = navigator.appVersion.toLowerCase().indexOf('msie') != -1 && !this.opera && !this.khtml;
}