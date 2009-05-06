<%@ taglib prefix="jodd" uri="/jodd" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<html>
<body>
<pre>
<jodd:for start="1" end="5" status="s" modulus="3">${s} | </jodd:for>
<jodd:for start="1" end="5" step="2" status="s">${s} | </jodd:for>
<jodd:for start="5" end="5" status="s">${s.value} ${s.first} ${s.last} ${s.count} ${s.index} ${s.even} ${s.odd} ${s.modulus} ${s.indexModulus} | </jodd:for>
<jodd:for start="1" end="0" status="s">${s} | </jodd:for>(no iteration)
<jodd:for start="6" end="5" status="s">${s} | </jodd:for>(no iteration)
<jodd:for start="3" end="1" step="-1" status="s">${s} | </jodd:for>
<jodd:for start="1" end="3" step="0" status="s">${s} | </jodd:for>
<jodd:for start="3" end="1" step="0" status="s">${s} | </jodd:for>

<jodd:loop start="1" end="5" status="s">${s} | </jodd:loop>
<jodd:loop start="1" to="5" status="s">${s} | </jodd:loop>
<jodd:loop start="1" to="2" status="s">${s} | </jodd:loop>
<jodd:loop start="1" to="1" status="s">${s} | </jodd:loop>(no iteration)
<jodd:loop start="1" to="1" step="-1" status="s">${s} | </jodd:loop>(no iteration)
<jodd:loop start="1" to="-1" status="s">${s} | </jodd:loop>(no iteration)
<jodd:loop start="1" to="-1" step="-1" status="s">${s} | </jodd:loop>

<jodd:loop start="1" count="3" status="s">${s} | </jodd:loop>
<jodd:loop start="1" count="3" step="-1" status="s">${s} | </jodd:loop>
<jodd:loop start="1" count="1" step="1" status="s">${s} | </jodd:loop>
<jodd:loop start="1" count="1" step="-1" status="s">${s} | </jodd:loop>
<jodd:loop start="1" count="1" step="0" status="s">${s} | </jodd:loop>
<jodd:loop start="1" count="0" status="s">${s} | </jodd:loop>(no iteration)

<jodd:loop start="1" end="5" autoDirection="true" step="3" status="s">${s} | </jodd:loop>
<jodd:loop start="5" end="1" autoDirection="true" step="3" status="s">${s} | </jodd:loop>

<jodd:iter items="1,2,3" var="i" status="s">${i} ${s} | </jodd:iter>
<jodd:iter items="1" var="i" status="s">${i} ${s} | </jodd:iter>
<jodd:iter items="1,2,3" var="i" from="1" status="s">${i} ${s} | </jodd:iter>
<jodd:iter items="1,2,3" var="i" from="2" status="s">${i} ${s} | </jodd:iter>
<jodd:iter items="1,2,3" var="i" from="3" status="s">${i} ${s} | </jodd:iter>(no iteration)

</pre>
</body>
</html>