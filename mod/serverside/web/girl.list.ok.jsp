<%@ page import="java.util.List" %>
<%@ page import="madvoc.girl.Girl" %>
<%@ page import="jodd.servlet.ServletUtil" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<small><a href="/">back</a></small>
All girls:<br>

<%
	List<Girl> girls = (List<Girl>) request.getAttribute("girls");
%>

<%
	for (int i = 0; i < girls.size(); i++) {
		Girl g = girls.get(i);
%>
	<%=i%>. (<%=g.getId()%>) <%=g.getName()%><br>
<%
	}
%>
<hr>
Add new girl:<br>
<form action="girl.add.html" method="post">
	<input type="text" name="girl.name">
	<input type="submit" value="Create girl">
</form>


<pre>
	<%=ServletUtil.debug(pageContext)%>
</pre>

</body>
</html>