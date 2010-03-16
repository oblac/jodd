<%@ taglib prefix="j" uri="/jodd" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<html>
<body>
<pre>
<j:for start="1" end="5" status="s" modulus="3">${s} | </j:for>
<j:for start="1" end="5" step="2" status="s">${s} | </j:for>
<j:for start="5" end="5" status="s">${s.value} ${s.first} ${s.last} ${s.count} ${s.index} ${s.even} ${s.odd} ${s.modulus} ${s.indexModulus} | </j:for>
<j:for start="1" end="0" status="s">${s} | </j:for>(no iteration)
<j:for start="6" end="5" status="s">${s} | </j:for>(no iteration)
<j:for start="3" end="1" step="-1" status="s">${s} | </j:for>
<j:for start="1" end="3" step="0" status="s">${s} | </j:for>
<j:for start="3" end="1" step="0" status="s">${s} | </j:for>

<j:loop start="1" end="5" status="s">${s} | </j:loop>
<j:loop start="1" to="5" status="s">${s} | </j:loop>
<j:loop start="1" to="2" status="s">${s} | </j:loop>
<j:loop start="1" to="1" status="s">${s} | </j:loop>(no iteration)
<j:loop start="1" to="1" step="-1" status="s">${s} | </j:loop>(no iteration)
<j:loop start="1" to="-1" status="s">${s} | </j:loop>(no iteration)
<j:loop start="1" to="-1" step="-1" status="s">${s} | </j:loop>

<j:loop start="1" count="3" status="s">${s} | </j:loop>
<j:loop start="1" count="3" step="-1" status="s">${s} | </j:loop>
<j:loop start="1" count="1" step="1" status="s">${s} | </j:loop>
<j:loop start="1" count="1" step="-1" status="s">${s} | </j:loop>
<j:loop start="1" count="1" step="0" status="s">${s} | </j:loop>
<j:loop start="1" count="0" status="s">${s} | </j:loop>(no iteration)

<j:loop start="1" end="5" autoDirection="true" step="3" status="s">${s} | </j:loop>
<j:loop start="5" end="1" autoDirection="true" step="3" status="s">${s} | </j:loop>

<j:iter items="1,2,3" var="i" status="s">${i} ${s} | </j:iter>
<j:iter items="1" var="i" status="s">${i} ${s} | </j:iter>
<j:iter items="1,2,3" var="i" from="1" status="s">${i} ${s} | </j:iter>
<j:iter items="1,2,3" var="i" from="2" status="s">${i} ${s} | </j:iter>
<j:iter items="1,2,3" var="i" from="3" status="s">${i} ${s} | </j:iter>(no iteration)

</pre>
</body>
</html>