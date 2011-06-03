<%@ page import="jodd.servlet.DispatcherUtil" %>
<html>
<body>
<pre>
full url: <%=DispatcherUtil.getFullUrl(request)%>
url: <%=DispatcherUtil.getUrl(request)%>
----
context path: <%=DispatcherUtil.getContextPath(request)%>
forward context path: <%=DispatcherUtil.getForwardContextPath(request)%>
path info: <%=DispatcherUtil.getPathInfo(request)%>
query string: <%=DispatcherUtil.getQueryString(request)%>
request uri: <%=DispatcherUtil.getRequestUri(request)%>
servlet path: <%=DispatcherUtil.getServletPath(request)%>
</pre>
</body>
</html>