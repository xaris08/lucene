<%@ page import="java.util.*"%>
<html>
<body>
	<h1 align="center">Indexing Results</h1>
	<%
		String result = (String) request.getAttribute("result");
	%>
	<h3><%=result%></h3>
</body>
</html>