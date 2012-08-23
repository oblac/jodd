<%@ page import="java.util.List" %>
<%@ page import="madvoc.item.Item" %>
<%@ page import="jodd.servlet.ServletUtil" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<small><a href="/">back</a></small>
All items:<br>

<%
	List<Item> items = (List<Item>) request.getAttribute("items");
%>

<%
	for (int i = 0; i < items.size(); i++) {
		Item g = items.get(i);
%>
	<%=i%>. (<%=g.getId()%>) <%=g.getName()%><br>
<%
	}
%>
<hr>
Add new item:<br>
<form action="item.add.html" method="post">
	<input type="text" name="item.name">
	<input type="submit" value="Create item">
</form>


<pre>
	<%=ServletUtil.debug(pageContext)%>
</pre>

</body>
</html>