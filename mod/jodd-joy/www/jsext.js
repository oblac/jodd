// JavaScript extensions

/*** Array methods ***/

Array.prototype.sum = function() {
	for (var i = 0, sum = 0; i < this.length; sum += this[i++]);
	return sum;
};
Array.prototype.max = function() {
	return Math.max.apply({}, this);
};
Array.prototype.min = function() {
	return Math.min.apply({}, this);
};
Array.prototype.remove = function(item) {
	for (var i = 0; i < this.length; i++) {
		if (item == this[i]) this.splice(i, 1);
	}
};
Array.prototype.removeRange = function(from, to) {
	var rest = this.slice((to || from) + 1 || this.length);
	this.length = from < 0 ? this.length + from : from;
	return this.push.apply(this, rest);
};
Array.prototype.contains = function(element) {
	for (var i = 0; i < this.length; i++) {
		if (this[i] == element) return true;
	}
	return false;
};
Array.prototype.last = function() {
	return this[this.length - 1];
};
Array.prototype.clear = function() {
	this.length = 0;
};
Array.prototype.lastIndexOf = function(n) {
	var i = this.length;
	while (i--) {
		if (this[i] === n) return i;
	}
	return -1;
};
Array.prototype.indexOf = function(o, i) {
	for (var j = this.length, i = i < 0 ? i + j < 0 ? 0 : i + j : i || 0 ; i < j && this[i] !== o; i++);
	return j <= i ? - 1 : i
};
Array.prototype.addUnique = function(item) {
	if (!this.contains(item)) {
		this.push(item);
	}
};
Array.prototype.clone = function() {
	var arr = new Array(this.length);
	for (var i = 0; i < this.length; i++) {
		arr[i] = this[i];
	}
	return arr;
};
String.prototype.contains = function (A) {
	return (this.indexOf(A) > -1);
};
String.prototype.equals = function () {
	for (var i = 0; i < arguments.length; i++) {
		if (this == arguments[i]) {
			return true;
		}
	}
	return false;
};
String.prototype.startsWith = function (A) {
	return (this.substr(0, A.length) == A);
};
String.prototype.endsWith = function (A, B) {
	var C = this.length;
	var D = A.length;
	if (D > C) {
		return false;
	}
	if (B) {
		var E = new RegExp(A + "$", "i");
		return E.test(this);
	} else {
		return (D === 0 || this.substr(C - D, D) == A);
	}
};
String.prototype.remove = function (A, B) {
	var s = "";
	if (A > 0) {
		s = this.substring(0, A);
	}
	if (A + B < this.length) {
		s += this.substring(A + B, this.length);
	}
	return s;
};
String.prototype.trim = function () {
	return this.replace(/(^\s*)|(\s*$)/g, "");
};
String.prototype.ltrim = function () {
	return this.replace(/^\s*/g, "");
};
String.prototype.rtrim = function () {
	return this.replace(/\s*$/g, "");
};
String.prototype.replaceNewLineChars = function (A) {
	return this.replace(/\n/g, A);
};
String.prototype.replaceAll = function(pcFrom, pcTo) {
	var i = this.indexOf(pcFrom);
	var c = this;
	while (i > -1){
		c = c.replace(pcFrom, pcTo);
		i = c.indexOf(pcFrom);
	}
	return c;
};



/*** String functions ***/

String.prototype.contains = function (A) {
	return (this.indexOf(A) > -1);
};
String.prototype.equals = function () {
	for (var i = 0; i < arguments.length; i++) {
		if (this == arguments[i]) {
			return true;
		}
	}
	return false;
};
String.prototype.startsWith = function (A) {
	return (this.substr(0, A.length) == A);
};
String.prototype.endsWith = function (A, B) {
	var C = this.length;
	var D = A.length;
	if (D > C) {
		return false;
	}
	if (B) {
		var E = new RegExp(A + "$", "i");
		return E.test(this);
	} else {
		return (D === 0 || this.substr(C - D, D) == A);
	}
};
String.prototype.remove = function (A, B) {
	var s = "";
	if (A > 0) {
		s = this.substring(0, A);
	}
	if (A + B < this.length) {
		s += this.substring(A + B, this.length);
	}
	return s;
};
String.prototype.trim = function () {
	return this.replace(/(^\s*)|(\s*$)/g, "");
};
String.prototype.ltrim = function () {
	return this.replace(/^\s*/g, "");
};
String.prototype.rtrim = function () {
	return this.replace(/\s*$/g, "");
};
String.prototype.replaceNewLineChars = function (A) {
	return this.replace(/\n/g, A);
};
String.prototype.replaceAll = function(pcFrom, pcTo) {
	var i = this.indexOf(pcFrom);
	var c = this;
	while (i > -1){
		c = c.replace(pcFrom, pcTo);
		i = c.indexOf(pcFrom);
	}
	return c;
};