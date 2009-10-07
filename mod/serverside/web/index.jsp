<html>
<head>
	<title>Jodd Madvoc</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="jss.css">
</head>

<body>

<h2>Some Jodd Madvoc quick-and-dirty examples</h2>

<img src="gfx/duke.png">

<p>
<a href="hello.world.html?name=JohnDoe&data=173">Hello world</a> is always the first example:)<br>
<a href="hello.all.html?p.name=JohnDoe&p.data=13">Hello all</a> again and aliased.<br>
<a href="hello.again.html?ppp[0].name=Aaa&ppp[0].data=1&ppp[1].name=Bbb&ppp[1].data=2&ppp[2].name=Ccc&ppp[2].data=3">Hello again</a> but no results.<br>
<br>
<a href="default.html">Default</a> action name.<br>
<a href="hello.bigchange.html">BIG change</a>.<br>
<a href="hello.noresult.html">404</a> - action exist, but result doesn't.<br>
<a href="hello.chain.html">chains</a>.<br>
<a href="raw.html">raw</a> <a href="raw.text.html">access</a>.<br>
<br>
<a href="foo/hello">Hello</a>, no extension, different and explicitly defined action path.<br>
<a href="foo/boo.zoo/again.exec.html">Hello</a> no default extension, another different location (no result).<br>
<a href="incognito.html">Manual</a> configuration.<br>
<a href="one.html">One, Two</a>, redirect or move!<br>
<br>
<a href="i_n_d_e_x.html">Url rewrite</a><br>
<br>
<a href="mapped.foo.html">mapped.foo.html</a> | <a href="mapped.foo.txt">mapped.foo.txt</a> | <a href="mapped.html">mapped.html</a> |
<a href="mapped.txt">mapped.txt</a> | <a href="mapped">mapped</a><br>
<br>
<a href="hello.defint1.html?foo=173">default interceptors</a><br>
<a href="hello.defint2.html?foo=173&foo2=">parameters are copied</a><br>
<a href="misc.html">misc</a><br>
<a href="misc.post.html?girl.id=1&girl.name=requestName">inject id, prepare and execute</a><br>

<br>
<a href="search?query=%C5%A1aran">URI encoded link</a>
</p>


<p>
<a href="form.html">Form</a> example.<br>
<a href="girl.list.html">Session scope</a> example.<br>
<a href="uploadfiles.html">Upload</a> example.<br>
</p>

<p>
List of all <a href="madvoc-listAllActions.html">Action configurations</a>.<br>
</p>


</body>
</html>
