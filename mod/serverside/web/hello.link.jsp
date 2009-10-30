<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
	hello link<br>
Console for chain:
<pre style="color:white; background-color:green;">
	----->/hello.chain.html   [madvoc.HelloAction#chain]
	HelloAction.chain 173
	<----- /hello.chain.html  (chain:/hello.link.html) in 0ms.
	----->/hello.link.html   [madvoc.HelloAction#link]
	HelloAction.link 137
	<----- /hello.link.html  (null) in 0ms.
</pre>
</body>
</html>