/**
 * Re:Pager - Jodd Joy pager.
 */

function RePager(pagerId) {
	this.pagerId = pagerId;
	this.page = 1;
	this.sort = 0;
	this.body = $('#pagerBody-' + pagerId);
	this.form = $('form#pagerForm-' + pagerId);
	this.table = $('table#pagerTable-' + pagerId);

	var pager = this;

	$('thead td.sort', this.table).click(function() {
		var td = $(this);
		var s = 0;

		$('thead td.sort', pager.table).each(function(index, element) {
			if (element != td[0]) {
				$(element).removeClass('sort_asc').removeClass('sort_desc');
			} else {
				s = index + 1;
			}
		});

		if (td.hasClass('sort_asc')) {
			td.removeClass('sort_asc');
			td.addClass('sort_desc');
			s = -s;
		} else {
			td.addClass('sort_asc');
			td.removeClass('sort_desc');
		}
		pager.sort = s;
		pager.goto();
	});

	// initialize
	$('thead td.sort', this.table).each(function(index, element) {
		if ($(element).hasClass('sort_asc')) {
			pager.sort = index + 1;
		} else if ($(element).hasClass('sort_desc')) {
			pager.sort = -(index + 1);
		}
	});

	this.goto = function(p) {
		if (!p) {
			p = this.page;
		} else {
			this.page = p;
		}
		pleaseWait();

		var pager = this;
		var options = {
			data: {
				'pageRequest.page': pager.page,
				'pageRequest.pagerId' : pager.pagerId,
				'pageRequest.sort' : pager.sort
			},
			success: function(response) {
				if (response.length == 0) {		// fix dialog escape bug
					 pager.goto(1);
					 return;
				}
				pleaseWait(false);
				pager.body.html(response);
			}
		};
		pager.form.ajaxSubmit(options);
	}
}
