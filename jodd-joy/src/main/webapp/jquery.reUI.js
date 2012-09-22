/**
 * CSS and java-friendly checkbox and radios.
 */
jQuery.fn.reCheckbox = function() {
	var _this = this;

	$(":checkbox", this).each(function() {

		var check = this;
		var $check = $(this);
		var checkId = $check.attr("id");
		var checkName = $check.attr("name");
		var checkValue = $check.attr("value");
		var checkUnvalue = $check.attr("unvalue");
		if (!checkUnvalue) {
			checkUnvalue = 'off';
		}

		// add hidden field
		$check.after("<input type='hidden' name='" + checkName +"' value='" + check.checked + "' id='" + checkId + "_checkbox' checkValue='" + checkValue + "' checkUnvalue='" + checkUnvalue + "'/>");
		var $checkInput = $("input#" + checkId + "_checkbox", _this);

		// finds a label
		var label = $("label[for='" + checkId + "']", _this);
		$.fn._reCheckboxCheck(check.checked ? true : false, label, $checkInput, checkValue, checkUnvalue);
		label.addClass("checkboxLabel");

		// remove the checkbox, add label click handler
		$check.remove();
		label.click(function() {
			$.fn._reCheckboxCheck(!label.hasClass("checked"), label, $checkInput, checkValue, checkUnvalue);
			return false;
		});
	});
};

/**
 * Sets checkbox value.
 */
jQuery.fn.reCheckboxVal = function(id, value) {
	var _this = this;
	$('#' + id + "_checkbox:hidden", this).each(function() {
		var $checkInput = $(this);
		var checkValue = $checkInput.attr("checkValue");
		var checkUnvalue = $checkInput.attr("checkUnvalue");
		var label = $("label[for='" + id + "']", _this);
		$.fn._reCheckboxCheck(value, label, $checkInput, checkValue, checkUnvalue);
	});
};

jQuery.fn._reCheckboxCheck = function(value, label, checkInput, checkValue, checkUnvalue) {
	if (value === true) {
		label.addClass("checked");
		checkInput.attr('value', checkValue ? checkValue : true);
	} else if (value === false) {
		label.removeClass("checked");
		checkInput.attr('value', checkValue ? checkUnvalue : false);
	}
};




/**
 * CSS radio button.
 */
jQuery.fn.reRadio = function() { 
	var _this = this;

	$(":radio", this)
			.hide()				// hide native radios
			.each(function() {	// find related labels and add all the fancy stuff

		var radio = this;
		var $radio = $(radio);
		var radioId = $radio.attr("id");
		var label = $("label[for='" + radioId + "']", _this);
		var name = $radio.attr('name');

		if (radio.checked) {
			label.addClass("radioChecked");
		}
		label.addClass("radioLabel");

		// label click state
		label.click(function() {
			$.fn._reRadioCheck(name, radioId, _this);
/*
			$(":radio[name=" + name + "]", _this).each(function() {
				var radio2 = $(this);
				var label2For = radio2.attr("id");
				var label2 = $("label[for='" + label2For + "']", _this);

				if (label2For == radioId) {
					label2.addClass("radioChecked");
					this.checked = true;
				} else {
					label2.removeClass("radioChecked");
					this.checked = false;
				}
			});
*/
			return false;
		});
	});
};

/**
 * Sets radio value.
 */
jQuery.fn.reRadioVal = function(radioId) {
	var r = $('#' + radioId, this);
	var radioName = r.attr("name");
	$.fn._reRadioCheck(radioName, radioId, this);
};

jQuery.fn._reRadioCheck = function(radioName, radioId, _this) {
	$(":radio[name=" + radioName + "]", _this).each(function() {
		var radio2 = $(this);
		var label2For = radio2.attr("id");
		var label2 = $("label[for='" + label2For + "']", _this);

		if (label2For == radioId) {
			label2.addClass("radioChecked");
			this.checked = true;
		} else {
			label2.removeClass("radioChecked");
			this.checked = false;
		}
	});
};
