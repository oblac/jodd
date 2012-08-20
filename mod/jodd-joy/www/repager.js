/**
 * Re:Pager - Jodd Joy pager.
 */

function RePager(pagerId) {
	this.pagerId = pagerId;
	this.page = 1;
	this.body = $('#pagerBody-' + pagerId);
	this.form = $('form#pagerForm-' + pagerId);

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
				'pageRequest.pagerId' : pager.pagerId
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
