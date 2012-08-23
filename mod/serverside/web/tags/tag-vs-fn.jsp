<%@ page import="jodd.datetime.JStopWatch" %>
<%@ taglib prefix="j" uri="/jodd" %>
<%@ taglib prefix="jfn" uri="/joddfn" %>
<html>
<body>

${jfn:storePageContextInThread(pageContext)}
${jfn:storeContextPath(pageContext, "CTX")}

<j:url _="/hello.jsp"/> 5.243 sec
${jfn:url("/hello.jsp", pageContext)} 0.632
${jfn:url1("/hello.jsp")} 00.592
${CTX}/hello.jsp total: 00.151

<%
	long l = 100000;
	JStopWatch jsw = new JStopWatch();
	while (l-- > 0) {
%>

${CTX}/hello.jsp

<%
	}
	jsw.stop();
	System.out.println(jsw);
%>

</body>
</html>