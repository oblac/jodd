<%@ page import="madvoc.FooFormBean"%>
<%@ page import="static jodd.servlet.HtmlFormUtil.*" %>
<%@ page import="static jodd.util.ValueLookup.*" %>
<%@ page import="static jodd.servlet.HtmlEncoder.*" %>
<%@ page import="jodd.servlet.JspValueResolver" %>
<%@ taglib prefix="jodd" uri="/jodd" %>
<%@ taglib prefix="joddfn" uri="/joddfn" %>
<html>
<head>
	<title>Big Form test</title>
	<link rel="stylesheet" type="text/css" href="jss.css">
</head>

<body>
<%
	JspValueResolver $ = new JspValueResolver(request);
	FooFormBean ffb = (FooFormBean) request.getAttribute("foo");
	if (ffb == null) {
		ffb = new FooFormBean();
	}
%>

<h1>Form</h1>
<small><a href="index.html">back</a></small><br>


<div style="float:left">
<h2>Manual values set using scriptlets</h2>
<form name="form" method="post" action="form.post.html">

<table border=1 cellpadding=3 cellspacing=0>

	<!-- CHECKBOXES -->
	<tr><td>
		Checkbox <small><i>(has value, String)</i></small>
	</td><td>
		<input type="checkbox" name="foo.check" value="<%=checkedValue(ffb.check, "check_value")%>">
	</td></tr>

	<tr><td>
		Checkbox <small><i>(no value, boolean)</i></small>
	</td><td>
		<input type="checkbox" name="foo.check1" value="true" <%=checked(ffb.check1)%>>
	</td></tr>

	<tr><td>
		Checkboxes <small><i>(Boolean[], unknown size)</i></small>
	</td><td>
		<input type="checkbox" name="foo.check2[0]" value="true" <%=checked(array(ffb.check2, 0))%>>
		<input type="checkbox" name="foo.check2[1]" value="true" <%=checked(array(ffb.check2, 1))%>>
		<input type="checkbox" name="foo.check2[2]" value="true" <%=checked(array(ffb.check2, 2))%>>
	</td></tr>


	<tr><td>
		Checkboxes <small><i>(MutableInteger[], known size)</i></small>
	</td><td>
		<input type="checkbox" name="foo.check3[0]" value="1" <%=checkedExist(ffb.check3[0])%>>
		<input type="checkbox" name="foo.check3[1]" value="7" <%=checkedExist($.property("foo.check3[1]"))%>>
		<input type="checkbox" name="foo.check3[2]" value="3" <%=checkedExist(ffb.check3[2])%>>
	</td></tr>



	<tr><td>
		Hidden field <small><i>(String)</i></small>
	</td><td>
		&nbsp;<input type="hidden" name="foo.hidden" value="<%=text(ffb.hidden)%>">
	</td></tr>

	<tr><td>
		Password field <small><i>(String)</i></small>
	</td><td>
		<input type="password" name="foo.password" value="<%=text(ffb.password)%>">
	</td></tr>

	<tr><td>
		Radio buttons <small><i>(String)</i></small>
	</td><td>
		<input type="radio" name="foo.radio" value="<%=checkedValue(ffb.radio, "radio_value1")%>"><br>
		<input type="radio" name="foo.radio" value="radio_value2" <%=checked(ffb.radio, "radio_value2")%>>
	</td></tr>

	<tr><td>
		Text field <small><i>(String)</i></small>
	</td><td>
		<input type="text" name="foo.text" value="<%=text(ffb.text)%>">
	</td></tr>

	<tr><td>
		Text field <small><i>(int)</i></small>
	</td><td>
		<input type="text" name="foo.text1" value="<%=ffb.text1%>">
	</td></tr>

	<tr><td>
		Text field <small><i>(Long)</i></small>
	</td><td>
		<input type="text" name="foo.text2" value="<%=text(ffb.text2)%>">
	</td></tr>

	<tr><td>
		Textarea <small><i>(String, trimmed)</i></small>
	</td><td>
		<textarea cols="30" name="foo.textarea" rows="10"><%=text(ffb.textarea)%></textarea>
	</td></tr>

	<tr><td>
		Select menu <small><i>(String)</i></small>
	</td><td>
		<select name="foo.select">
			<option value="option1" <%=selected(ffb.select, "option1")%>>option #1</option>
			<option value="<%=selectedValue(ffb.select, "option2")%>">option #2</option>
			<option value="<%=selectedValue(ffb.select, "option3")%>">option #3</option>
		</select>
	</td></tr>

	<tr><td>
		Multiple select list <small><i>(String[])</i></small>
	</td><td>
		<select name="foo.sarr" size="3" multiple>
			<option value="option1" <%=multiSelected(ffb.sarr, "option1")%>>option #1</option>
			<option value="<%=multiSelectedValue(ffb.sarr, "option2")%>">option #2</option>
			<option value="option3" <%=multiSelected(ffb.sarr, "option3")%>>option #3</option>
		</select>
	</td></tr>

	<tr><td>
		Multiple Texts <small><i>(List&lt;String&gt;)</i></small>
	</td><td>
		<input type="text" name="foo.slist[0]" value="<%=text(list(ffb.slist, 0))%>" size="10">
		<input type="text" name="foo.slist[1]" value="<%=text(list(ffb.slist, 1))%>" size="10">
		<input type="text" name="foo.slist[2]" value="<%=text(list(ffb.slist, 2))%>" size="10">
	</td></tr>


	<tr><td>
		Multiple Texts <small><i>(Map&lt;String, String&gt;)</i></small>
	</td><td>
		<input type="text" name="foo.smap[one]" value="<%=text(map(ffb.smap, "one"))%>" size="10">
		<input type="text" name="foo.smap[two]" value="<%=text(map(ffb.smap, "two"))%>" size="10">
		<input type="text" name="foo.smap[tree]" value="<%=text(map(ffb.smap, "tree"))%>" size="10">
	</td></tr>


	<tr><td>
		Multiple select list <small><i>(int[])</i></small>
	</td><td>
		<select name="foo.iarr" size="3" multiple>
			<option value="1" <%=multiSelected(ffb.iarr, "1")%>>option #1</option>
			<option value="<%=multiSelectedValue(ffb.iarr, "2")%>">option #2</option>
			<option value="3" <%=multiSelected(ffb.iarr, "3")%>>option #3</option>
		</select>
	</td></tr>


	<tr><td>
		<b>Submit</b>
	</td><td>
		<input type="submit" value="submit_value">
	</td></tr>
</table>
</form>
</div>












<div style="float:left; margin-left:20px;">
<h2>Automagic values set using jodd:form tag</h2>
<jodd:form>
<form id="form2" method="post" action="form.post.html">

<table border=1 cellpadding=3 cellspacing=0>

	<!-- CHECKBOXES -->
	<tr><td>
		Checkbox <small><i>(has value, String)</i></small>
	</td><td>
		<input type="checkbox" name="foo.check" value="check_value">
	</td></tr>

	<tr><td>
		Checkbox <small><i>(no value, boolean)</i></small>
	</td><td>
		<input type="checkbox" name="foo.check1">
	</td></tr>

	<tr><td>
		Checkboxes <small><i>(Boolean[], unknown size)</i></small>
	</td><td>
		<input type="checkbox" name="foo.check2[0]">
		<input type="checkbox" name="foo.check2[1]">
		<input type="checkbox" name="foo.check2[2]">
	</td></tr>


	<tr><td>
		Checkboxes <small><i>(MutableInteger[], known size)</i></small>
	</td><td>
		<input type="checkbox" name="foo.check3[0]" value="1">
		<input type="checkbox" name="foo.check3[1]" value="7">
		<input type="checkbox" name="foo.check3[2]" value="3">
	</td></tr>



	<tr><td>
		Hidden field <small><i>(String)</i></small>
	</td><td>
		&nbsp;<input type="hidden" name="foo.hidden">
	</td></tr>

	<tr><td>
		Password field <small><i>(String)</i></small>
	</td><td>
		<input type="password" name="foo.password">
	</td></tr>

	<tr><td>
		Radio buttons <small><i>(String)</i></small>
	</td><td>
		<input type="radio" name="foo.radio" value="radio_value1"><br>
		<input type="radio" name="foo.radio" value="radio_value2">
	</td></tr>

	<tr><td>
		Text field <small><i>(String)</i></small>
	</td><td>
		<input type="text" name="foo.text">
	</td></tr>

	<tr><td>
		Text field <small><i>(int)</i></small>
	</td><td>
		<input type="text" name="foo.text1">
	</td></tr>

	<tr><td>
		Text field <small><i>(Long)</i></small>
	</td><td>
		<input type="text" name="foo.text2">
	</td></tr>

	<tr><td>
		Textarea <small><i>(String, trimmed)</i></small>
	</td><td>
		<textarea cols="30" name="foo.textarea" rows="10"></textarea>
	</td></tr>

	<tr><td>
		Select menu <small><i>(String)</i></small>
	</td><td>
		<select name="foo.select">
			<option value="option1">option #1</option>
			<option value="option2">option #2</option>
			<option value="option3">option #3</option>
		</select>
	</td></tr>

	<tr><td>
		Multiple select list <small><i>(String[])</i></small>
	</td><td>
		<select name="foo.sarr" size="3" multiple>
			<option value="option1">option #1</option>
			<option value="option2">option #2</option>
			<option value="option3">option #3</option>
		</select>
	</td></tr>

	<tr><td>
		Multiple Texts <small><i>(List&lt;String&gt;)</i></small>
	</td><td>
		<input type="text" name="foo.slist[0]" size="10">
		<input type="text" name="foo.slist[1]" size="10">
		<input type="text" name="foo.slist[2]" size="10">
	</td></tr>


	<tr><td>
		Multiple Texts <small><i>(Map&lt;String, String&gt;)</i></small>
	</td><td>
		<input type="text" name="foo.smap[one]" size="10">
		<input type="text" name="foo.smap[two]" size="10">
		<input type="text" name="foo.smap[tree]" size="10">
	</td></tr>


	<tr><td>
		Multiple select list <small><i>(int[])</i></small>
	</td><td>
		<select name="foo.iarr" size="3" multiple>
			<option value="1">option #1</option>
			<option value="2">option #2</option>
			<option value="3">option #3</option>
		</select>
	</td></tr>


	<tr><td>
		<b>Submit</b>
	</td><td>
		<input type="submit" value="submit_value">
	</td></tr>
</table>
</form>
</div>
</jodd:form>

<br clear="all">

<hr>
<pre>
<%= jodd.servlet.ServletUtil.debug(pageContext) %>
</pre>

</body>
</html>