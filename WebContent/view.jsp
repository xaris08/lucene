<!DOCTYPE html>
<%@ page import="java.util.*"%>
<%@ page import="edu.papei.lucene.model.*"%>

<%
	CacmDocument doc = (CacmDocument) request.getAttribute("document");
%>

<span>Title:</span>
<div>
	<%=doc.getTitle()%>
</div>
<br />
<br />

<span>Author(s):</span>
<div>
	<%=doc.getAuthors()%>
</div>
<br />
<br />

<span>Synopsis:</span>
<div>
	<%=doc.getSynopsis()%>
</div>
<br />
<br />

<button type="button" onclick="window.history.back()">Back</button>