
jQuery.fn.cssCheckbox = function() {
	var _this = this;

	/* checkbox */
	$(":checkbox", this).each(function() {

		var check = this;
		var $check = $(this);
		var checkName = $check.attr("name");
		var checkId = $check.attr("id");
		var checkValue = $check.attr("value");
		var checkUnvalue = $check.attr("unvalue");
		if (!checkUnvalue) {
			checkUnvalue = 'off';
		}

		// add hidden field
		$check.after("<input type='hidden' name='" + checkName +"' value='" + check.checked + "' id='" + checkId + "_checkbox'/>");
		var $checkInput = $("input#" + checkId + "_checkbox", _this);

		// finds a label
		var label = $("label[for='" + checkId + "']", _this);
		if (check.checked) {
			label.addClass("checked");
			$checkInput.attr('value', checkValue ? checkValue : true);
		} else {
			$checkInput.attr('value', checkValue ? checkUnvalue : false);
		}
		label.addClass("checkboxLabel");

		// remove checkbox, add label click handler
		$check.remove();
		label.click(function() {
			if (label.hasClass("checked")) {
				label.removeClass("checked");
				$checkInput.attr('value', checkValue ? checkUnvalue : false);
			} else {
				label.addClass("checked");
				$checkInput.attr('value', checkValue ? checkValue : true);
			}
			return false;
		});
	});

	/* radio */
	$(":radio", this)
			.hide()			// hide native radioboxes
			.each(function() {		// find related labels and add all the fancy stuff

		var radio = this;
		var $radio = $(radio);
		var labelFor = $radio.attr("id");
		var label = $("label[for='" + labelFor + "']", _this);
		var name = $radio.attr('name');


		if (radio.checked) {
			label.addClass("radioChecked");
		}
		label.addClass("radioLabel");

		// label click state
		label.click(function() {
			$(":radio[name=" + name + "]", _this).each(function() {
				var radio2 = $(this);
				var label2For = radio2.attr("id");
				var label2 = $("label[for='" + label2For + "']", _this);
				
				if (label2For == labelFor) {
					label2.addClass("radioChecked");
					this.checked = true;
				} else {
					label2.removeClass("radioChecked");
					this.checked = false;
				}
			});
			return false;
		});
	});

};