<%@ tag import="jodd.joy.page.PageRequest" %>
<%@ tag import="jodd.joy.page.PageData" %>
<%@ tag import="jodd.joy.page.PageNav" %>
<%@ tag body-content="scriptless" pageEncoding="UTF-8" %>
<%@ attribute name="cols" required="true" %>
<%@ attribute name="itemName" required="true" type="java.lang.String" rtexprvalue="false" %>
<%@ variable name-from-attribute="itemName" alias="item" %>
<%@ variable name-given="s" %>
<%@ taglib prefix="j" uri="/jodd" %>

<%
	if (request.getAttribute("pagerId") == null) {
		PageRequest pageRequest = (PageRequest) request.getAttribute("pageRequest");
		request.setAttribute("pagerId", pageRequest.getPagerId());
	}
	PageData pageData = (PageData) request.getAttribute("page");
	PageNav pageNav = (PageNav) request.getAttribute("nav");
%>
<j:iter items="${page.items}" var="item" status="s">
	<tr class="row row${s.modulus}">
		<jsp:doBody/>
	</tr>
</j:iter>

<% for (int i = pageData.getPageItemsCount(); i < pageData.getPageSize(); i++) { %>
	<tr><td colspan="${cols}"><div class="emptyRow">&nbsp;</div></td></tr>
<% } %>

<tr class="pagerBar"><td colspan="${cols}">
	<div class="pagerBarCtx">
	<ul>
		<% if (pageData.isFirstPage()) { %>
			<li class="nav btnFirstPage"></li>
			<li class="nav btnPreviousPage"></li>
		<% } else { %>
			<li class="nav btnFirstPageActive"><a href="#" onclick="pager_${pagerId}.goto(1); return false;"></a></li>
			<li class="nav btnPreviousPageActive"><a href="#" onclick="pager_${pagerId}.goto(${page.currentPage - 1}); return false;"></a></li>
		<% } %>
		<% if (pageData.isLastPage()) { %>
			<li class="nav btnNextPage"></li>
			<li class="nav btnLastPage"></li>
		<% } else { %>
			<li class="nav btnNextPageActive"><a href="#" onclick="pager_${pagerId}.goto(${page.currentPage + 1}); return false;"></a></li>
			<li class="nav btnLastPageActive"><a href="#" onclick="pager_${pagerId}.goto(${page.totalPages}); return false;"></a></li>
		<% } %>
	</ul>
	<div class="pagerLinks">
		<%
			if (pageData.getTotalItems() != 0) {
		    	for (int i = pageNav.getFrom(); i <= pageNav.getTo(); i++) {
					if (i == pageData.getCurrentPage()) {
		%>
						<span><%=i%></span>
		<%
					} else {
		%>
						<span><a href="#" onclick="pager_${pagerId}.goto(<%=i%>); return false;"><%=i%></a></span>
		<%
					}
				}
			}
		%>
	</div>
	<div class="pagerReport">
		<% if (pageData.getTotalItems() != 0) { %>
			Showing: ${page.firstIndex + 1} - ${page.lastIndex + 1} of ${page.totalItems}
		<% } else { %>
			No results.
		<% } %>
	</div>
	</div>
</td></tr>