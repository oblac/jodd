<%@ page import="jodd.datetime.JStopWatch" %>
<%@ taglib prefix="j" uri="/jodd" %>
<%@ taglib prefix="jfn" uri="/joddfn" %>
<html>
<body>

${jfn:storePageContext(pageContext)}

<j:url _="/hello.jsp"/> 6 sec
${jfn:url("/hello.jsp", pageContext)} 0.9s

<%
	long l = 100000;
	JStopWatch jsw = new JStopWatch();
	while (l-- > 0) {
%>

${jfn:url1("/hello.jsp")}

<%
	}
	jsw.stop();
	System.out.println(jsw);
%>

</body>
</html>