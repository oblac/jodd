<%@ taglib prefix="j" uri="/jodd" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Madvoc Actions</title></head>
<body>

<h2>Registered Madvoc actions</h2>

<table border="1" cellpadding="2">
	<tr>
		<th>Initialized</th>
		<th>Action path</th>
		<th>Action</th>
	</tr>
<j:iter items="${actions}" var="cfg">
	<tr>
		<td><j:if test="${cfg.initialized}">init</j:if></td>
		<td><a href="${cfg.actionPath}">${cfg.actionPath}#${cfg.actionMethod}</a></td>
		<td>${cfg.actionString}</td>
	</tr>
</j:iter>
</table>

</body>
</html>
