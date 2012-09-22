<%@ tag body-content="scriptless" %>
<%@ attribute name="pagerId" required="true" %>
<%@ attribute name="pagerAction" required="true" %>
<%
	request.setAttribute("pagerId", jspContext.getAttribute("pagerId"));
%>

<script type="text/javascript">
var pager_${pagerId};
$(function() {
	pager_${pagerId} = new RePager('${pagerId}');
});
</script>
<form id="pagerForm-${pagerId}" action="${pagerAction}" method="post"></form>
<table class="pagerBorder" cellspacing="0" cellpadding="0"><tr><td>
<table id="pagerTable-${pagerId}" cellspacing="0" cellpadding="0" class="pagerTable">
<thead><tr>
	<jsp:doBody/>
</tr></thead>
<tbody id="pagerBody-${pagerId}" class="pagerBody">