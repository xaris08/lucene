<!DOCTYPE html>
<%@ page import="java.util.*"%>
<%@ page import="edu.papei.lucene.model.*"%>

<script type="text/javascript">
function viewDocument(documentId) {
	var contextPath = window.location.pathname.split('/')[1];
	var URL = 'http://' + window.location.host + '/' + contextPath + '/view?id=' + documentId;
	window.location.href = URL;
}
</script>

<%
	String total = (String) request.getAttribute("total");
	String timeElapsed = (String) request.getAttribute("elapsedTime");
	List<CacmDocument> docs = (List<CacmDocument>) request.getAttribute("documents");
%>

<% if (docs == null || docs.size() == 0) { %>
	<h3>No results found.</h3>
<% } else { %>
	<p align="left">Total Results: <%=total %></p>
	<p align="left">Time Elapsed: <%=timeElapsed %></p>
	<br/><br/>
	<table border="0" width="100%">
		<tr>
			<th>#</th>
			<th>ID</th>
			<th>Title</th>
			<th>Author</th>
		</tr>
		<%
			for (int i = 0; i < docs.size(); i++) {
		%>
		<tr>
			<td id="idDoc"><%=i+1%></td>
			<td><a style="cursor: pointer;" href="javascript:void(0)" onclick="viewDocument(<%=docs.get(i).getId()%>)"><%=docs.get(i).getId()%></a></td>
			<td><%=docs.get(i).getTitle()%></td>
			<td><%=docs.get(i).getAuthors()%></td>
		</tr>
		<%
			}
		%>
	</table>
<% } %>