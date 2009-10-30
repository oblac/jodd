<%@ page import="jodd.servlet.ServletUtil" %>
<html>
<body>

Hello world ${retv} and ${name}.<br>
<pre style="color:white; background-color:green;">
Hello world and Universe and JohnDoexxx.

PARAMETERS
----------
data=173
name=JohnDoe
</pre>

<pre>
	<%=ServletUtil.debug(pageContext)%>
</pre>



</body>
</html>