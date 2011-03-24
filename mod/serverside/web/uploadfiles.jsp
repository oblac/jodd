<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Upload Example</title>
	<link rel="stylesheet" type="text/css" href="jss.css">
</head>
<body>
<h2>Upload Example</h2>
<a href="index.jsp">back</a><br><br>

This is a multipart request example.<br>

Request handler (i.e. action) will examine all parameter and what uploaded files are valid.<br>
Valid files will be stored in "java.io.tmpdir" folder (e.g. %tomcat%\temp).<br>
For example purposes, form also contains standard input fields.<br><br>


<table border=1 cellpadding="10px">
<tr>
<td align="top">
	<form name="form1" method="post" enctype="multipart/form-data" action="upload.exec.html" accept-charset="UTF-8">

		<input type="text" name="boo">

		<input type="text" name="foo.data1" size="40"><br>
		<input type="file" name="foo.file1" size="40"> (#1)<br>
		<input type="file" name="foo.file2" size="40"> (#2)<br>
		<input type="file" name="file3" size="40"> (#3)<br>
		<input type="file" name="file4" size="40"> (#4)<br>
		<input type="text" name="foo.data2" size="40"><br>
		<select name="foo.data3" multiple size="3">
			<option value="1">one</option>
			<option value="2">two</option>
			<option value="3">three</option>
		</select><br>
		<input type="submit" value="upload">
	</form>
</td>
</tr>
</table>

</body>
</html>