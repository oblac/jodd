<html><body>
TWO!<br>
value = ${value}
<hr>
Redirect:
<pre style="color:white; background-color:green;">
value =
</pre>
<pre style="color:white; background-color:darkblue;">

	----->/oneRedirect.html   [madvoc.OneRedirectAction#execute]
	redirect
	<----- /oneRedirect.html  (redirect:/%two%?value=\${value}) in 0ms.
	----->/two.html   [madvoc.TwoAction#view]
	TwoAction.invoke
	===> 173
	<----- /two.html  (null) in 0ms.

</pre>

<hr>
Move:
<pre style="color:white; background-color:green;">
value = 173
</pre>
<pre style="color:white; background-color:darkblue;">
	----->/oneMove.html   [madvoc.OneMoveAction#execute]
	move
	<----- /oneMove.html  (move:/%two%) in 15ms.
	----->/two.html   [madvoc.TwoAction#view]
	TwoAction.invoke
	===> 173
	<----- /two.html  (null) in 0ms.

</pre>


</body></html>