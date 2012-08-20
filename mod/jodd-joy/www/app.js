/**
 * Redirects to some page.
 */
function redirect(page) {
	window.location = page;
}

/**
 * Reloads current page.
 */
function reload() {
	window.location.reload(true);
}

/**
 * Jumps to some page, history not modified.
 */
function jump(page) {
	window.location.replace(page);
}

/**
 * Custom, friendly, alert that replaces native alert (still available with <code>alertalert</code>).
 */
var alertalert = window.alert;
var alertCount = 0;
window.alert = function(message) {
	var id = alertCount;
	alertCount += 1;
	var close = "<div class='alertMsgClose'><a href='#' onclick='$(\"#alert-" + id + "\").remove();'>[X]</a></div>";
	var divText = "<div class='alertBox' id='alert-" + id + "'>" + close + message + "</div>";
	$("body").prepend(divText);
};


/**
 * JS extends.
 */
extend = function(subClass, baseClass) {
	// Create a new class that has an empty constructor with the members of the baseClass
	function inheritance() {}
	inheritance.prototype = baseClass.prototype;
	// set prototype to new instance of baseClass _without_ the constructor
	subClass.prototype = new inheritance();
	subClass.prototype.constructor = subClass;
	subClass.baseConstructor = baseClass;
	// enable multiple inheritance
	if (baseClass.base) {
		baseClass.prototype.base = baseClass.base;
	}
	subClass.base = baseClass.protomatype;
};

/**
 * jQuery ajax.
 */
$.ajaxSetup({
	cache: false
});

$(document).ajaxError(function(event, xhr, settings) {
	var status = xhr.status;
	if (xhr.readyState === 0 || status === 0 || status == 200) { 
		return;  // it's not really an error
	}
	if (status == 403) {
		redirect('/login.html');
		return;
	}
	if (status === 404 || status === 500) {
		redirect('/error.' + status + '.html?url=' + encodeURI(settings.url));
		return;
	}
	alertalert('Ajax call failed: ' + status + '\n'+ settings.url);
});


/**
 * Shows or hides 'please wait' caption bar.
 */
var pwt;
function pleaseWait(show) {
	if (!show) {
		if (pwt != undefined) {
			clearTimer(pwt);
			pwt = undefined;
		}
		$("#wait").hide();
	} else {
		if (pwt == undefined) {
			pwt = setTimer(500, function() {
				$("#wait").show();
			});
		}
	}
}
/**
 * Shows or hides reload overlay over target element.
 * Using blockui.
 */
function showReload(target, show) {
	if (show === false) {
		target.unblock();
		return;
	}
	target.block({
		message: '<img src="/gfx/reload.gif">',
		overlayCSS: { backgroundColor: '#999', color: '#666' },
		css: { border: 'none', backgroundColor: 'transparent'}
	});
}

/**
 * Creates timer.
 */
function setTimer(time, func, callback) {
	var a = {timer:setTimeout(func, time), callback:null};
	if (callback) {a.callback = callback;}
	return a;
}

/**
 * Clears timer.
 */
function clearTimer(a) {
	clearTimeout(a.timer);
	if (a.callback) {a.callback();}
	return this;
}

/**
 * Unwraps tags in given text string.
 */
function unwrapTag(text) {

	if (text.startsWith('<')) {
		var ndx = text.indexOf('>');
		text = text.substr(ndx + 1);
	}

	if (text.endsWith('>')) {
		ndx = text.lastIndexOf("</");
		text = text.substr(0, ndx);
	}

	return text;
}