/**
 * Re:Form - Jodd form tool.
 *
 * Features:
 * + live ajax validation
 * + ajax form submission
 * + ajax validation before submission
 */

// default options
ReForm.prototype.defaults = {
	validationUrl:					undefined,			// validation url, if not specified will be built from forms action path and validationUrlSuffix
	validationUrlSuffix:			'Validate.json',	// live validation suffix
	validationUrlAppend:			true,				// if true, validation url will be appended; otherwise extension will be replaced
	liveValidation: 				false,				// enable live form validation, on blur
	ajaxValidationOnSubmit:			false,				// use ajax validation of form before the submit
	ajaxPost:						false,				// use ajax post instead of regular submit
	activateOnAjaxSubmitSuccess:	true,				// if true, form div selector will be reloaded with returned content and form will be reactivated
	onAjaxSubmitSuccess:			undefined,			// callback invoked on successful ajax submit, after the form was re-activated
	formDivSelector:				'div.form',			// form's (inner) div selector of element that will be reloaded on ajax submit with returned context; should not contain buttons, just fields
	submitClass:					'submit',			// class of submit button or link
	usedFieldsParamName:			'usedFieldNames',	// name of HTTP request parameter for used field names
	errorClass:						'error',			// name of error class
	errorMsgClass:					'error_msg'			// name of error message class
};

function ReForm(formid, options) {

	// options
	this.opts = $.extend({}, ReForm.prototype.defaults, options || {});

	// vars
	this.formId = formid;
	this.form = $('form#' + this.formId);
	if (this.form[0] === undefined) {
		alert("ERROR: Form '" + this.formId + "' does not exist.");
	}
	if (this.opts.formDivSelector.indexOf('#') != -1) {
		this.formDiv = $(this.opts.formDivSelector);
	} else {
		this.formDiv = $(this.opts.formDivSelector, this.form);
	}
	this.formIdPrefix = this.formId + '_';
	this.visited = [];
	this.enabled = true;
	this.formAction = this.form.attr('action');

	// validation url
	if (!this.opts.validationUrl) {
		var url = this.formAction;
		if (!this.opts.validationUrlAppend) {
			var ndx = url.lastIndexOf('.');
			if (ndx != -1) {
				url = url.substring(0, ndx);
			}
		}
		this.opts.validationUrl = url + this.opts.validationUrlSuffix;
	}

	// prepare submit buttons and links
	var _this = this;
	$('.' + this.opts.submitClass, this.form).click(function () {
		_this.submitForm();
		return false;
	});

	this.activate();
}

/**
 * toString.
 */
ReForm.prototype.toString = function() {
	return "re:form#" + this.formId;
};

/**
 * Activates form fields. Called when form content is reloaded, after submit!
 */
ReForm.prototype.activate = function() {
	var _this = this;

	// collect all readable input fields
	this.fields = {};
	this.allFields = '';
	var count = 0;
	$('input,select,textarea', this.form).each(function() {
		var field = $(this);
		var readonly = field.attr("readonly");
		if (readonly !== true) {
			if (this.name.length > 0) {
				_this.fields[this.name] = field;
				if (count > 0) {
					this.allFields += ',';
				}
				this.allFields += this.name;
				count += 1;
			}
		}
	});

	if (this.opts.liveValidation === true) {
		for (var name in this.fields) {
			var field = this.fields[name];
			field.blur(function() {
				var name = $(this).attr('name');
				_this.visited.addUnique(name);
				_this.validateForm(true);
			});
		}
	}
};

/**
 * Activates errors in the form.
 * + remove previous errors
 * + for each error field name find form field
 * + add errorClass to field
 * + shows error message div, if exist
 * + set error message div content from error message, if exist.
 * + returns true if there was error applied on the form.
 */
ReForm.prototype.activateErrors = function(errorData, onlyVisited) {
	this.removeAllValidationErrors();
	errorData = eval(errorData);
	var hasErrors = false;
	var i;
	if (errorData) {
		for (i = 0; i < errorData.length; i++) {
			var err = errorData[i];

			var fieldName = err.name;
			if (onlyVisited === true) {
				if (!this.visited.contains(fieldName)) {
					continue;
				}
			}
			var field = this.fields[fieldName];
			if (!field) {
				continue;
			}
			if (field.hasClass(this.opts.errorClass)) {
				continue;
			}
			hasErrors = true;
			field.addClass(this.opts.errorClass);
			var errMsgField = $('#' + field.attr('id') + '_error');
			if (errMsgField[0]) {
				errMsgField.show();
				if (err.msg) {
					errMsgField.html(err.msg);
				}
			}
		}
	}
	return hasErrors;
};

/**
 * Validate form using ajax and performs onValid if form is valid.
 */
ReForm.prototype.validateForm = function(onlyVisited, onValidCallback) {
	if (!onlyVisited) {
		onlyVisited = false;
	}
	var _this = this;
	var options = {
		iframe:		false,
		url:		_this.opts.validationUrl,
		success:	function(response) {
						if (_this.activateErrors(response, onlyVisited) === false) {
							if (onValidCallback) {onValidCallback();}
						}
					}
	};
	this.form.ajaxSubmit(options);
};


/**
 * Submits the form. It uses either conventional or ajax submit.
 * In both cases, helper field (usedFieldNames) is added to the post request.
 */
ReForm.prototype.submitForm = function() {
	if (this.enabled === false) {
		return;
	}
	if (this.opts.ajaxValidationOnSubmit) {
		var _this = this;
		// ajax validation before submit; whole form is submitted
		this.visitAllFields();
		this.validateForm(true, function() {
			_this._submitFormNow(_this);
		});
		return;
	}
	this._submitFormNow(this);
};

/**
 * Submits form. Private method, should not be called directly.
 */
ReForm.prototype._submitFormNow = function(_this) {
	_this.disableForm();
	if (!_this.opts.ajaxPost) {
		// regular submit
		_this.setFormParameter(this.opts.usedFieldsParamName, this.allFields);
		_this.form.submit();
		return;
	}
	// ajax submit
	_this.visited = [];
	var submitData = {};
	submitData[this.opts.usedFieldsParamName] = _this.allFields;
	var options = {
		data:	submitData,
		success:
				function(responseText) {
					if (responseText.startsWith('[{')) {
						_this.visitAllFields();
						_this.activateErrors(responseText, false);
					} else {
						if (_this.opts.activateOnAjaxSubmitSuccess) {
							_this.formDiv.html(responseText);
							_this.activate();
						}
						if (_this.opts.onAjaxSubmitSuccess) {
							_this.opts.onAjaxSubmitSuccess(responseText);
						}
					}
					_this.enableForm();
				}
	};
	_this.form.ajaxSubmit(options);
};

// form utilities

/**
 * Sets forms parameter. Hidden parameter will be added if not exist.
 */
ReForm.prototype.setFormParameter = function(name, value) {
	var field = $("input[name=" + name + "]", this.form);
	if (field.length === 0) {
		this.form.append("<input type='hidden' name='" + name + "'>");
		field = $("input:hidden[name=" + name + "]", this.form);
	}
	field.val(value);
};

/**
 * Removes form parameter.
 */
ReForm.prototype.removeFormParameter = function(name) {
	this.form.find("input[name=" + name + "]").remove();
};


/**
 * Disable all submits.
 */
ReForm.prototype.disableForm = function() {
	this.enabled = false;
	$('.' + this.opts.submitClass, this.form).attr('disabled', 'disabled');
};

/**
 * Enable all submit buttons.
 */
ReForm.prototype.enableForm = function() {
	this.enabled = true;
	$('.' + this.opts.submitClass, this.form).removeAttr('disabled');
};

/**
 * Removes all validation errors.
 * + hides all errorMsgClass divs in the form
 * + remove errorClass from all fields
 */
ReForm.prototype.removeAllValidationErrors = function() {
	this.hasErrors = false;
	$('.' + this.opts.errorMsgClass, this.form).hide();
	for (var name in this.fields) {
		this.fields[name].removeClass(this.opts.errorClass);
	}
};

/**
 * Visit all fields. Used when form is submitted to mark all fields as visited.
 */
ReForm.prototype.visitAllFields = function() {
	this.visited = [];
	for (var name in this.fields) {
		this.visited.push(name);
	}
};
