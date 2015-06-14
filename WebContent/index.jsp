<!DOCTYPE html>
<html>
<title>Lucene CACM Search</title>
<head>
<!-- <script type="text/javascript" src="jquery-1.11.0.min.js"></script> -->
<script src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
<script type="text/javascript">
	function getResults() {
		var type = $("#form select").val();
		var searchText = $("#searchText").val();

		var errorMsg = validateErrorMsg(type, searchText);
		if (errorMsg.length > 0) {
			alert(errorMsg);
			return false;
		}
		
		$.ajax({
			type : "post",
			url : "searcher",
			data : { type : type , searchText: searchText},
			success : function(html) {
				resetForm();
				$("#resultsDiv").show();
				$("#tableResultsDiv").append(html);
			}
		});
	}

	function validateErrorMsg(type, searchText) {
		var msg = "";
		if (type == "" && searchText.length == 0) {
			msg = "Please select values first.";
		}
		else if (type == "") {
			msg = "Please select field type.";
		}
		else if (searchText.length == 0) {
			msg = "Please input search text";
		}
		return msg;
	}
	
	function resetForm() {
// 		$("#form").trigger("reset");
		$("#resultsDiv").hide();
		$("#tableResultsDiv").children().remove();
	}
	
	function index() {
		var URL = "indexer";
		NewWindow = window.open(URL,"_blank");
		NewWindow.location = URL;
	}
	
	$(document).ready(function() {
		$("#searchText").focus();
		$('body').on('keypress', function(e) {
			if(e.keyCode==13){
				getResults();
				return false;
			}
		});
	});
</script>

<style type="text/css">
	/* div container containing the form  */
	#searchContainer {
		margin:20px;
		align:center;
	}
	
	/* Style the search input field. */
	#searchText {
/* 		float:left;  */
		width:500px; 
		height:27px; 
		line-height:27px;
		text-indent:10px; 
		font-family:arial, sans-serif; 
		font-size:1em; 
		color:#333; 
		background: #fff; 
		border:solid 1px #d9d9d9; 
		border-top:solid 1px #c0c0c0; 
/* 		border-right:none; */
	}
	/* Syle the search button. Settings of line-height, font-size, text-indent used to hide submit value in IE */
	#submit, #indexBtn {
		cursor:pointer; 
		width:110px; 
		height: 40px; 
		font-size: 15px;
		line-height:0;
		background-color: #F2F4F5;
		border: 1px solid #8CA8D5; 
		-moz-border-radius: 2px; 
		-webkit-border-radius: 2px; 
	}
	#indexBtn {
		position: absolute;
		right: 1.5em;
		font-size: 13px;'
		top: 1.5em;
		width:60px; 
		height: 25px; 
	}
	/* Style the search button hover state */
	#submit:hover, #indexBtn:hover  {
		border: 1px solid #2F5BB7;
	}
	/* Clear floats */
	.fclear {clear:both}
	
	#dropdown {
		width: 180px;
		font-size: medium;
	}
</style>
</head>
<body>
	<button id="indexBtn" type="button" onclick="index()">Index</button>
	<div align="center">
		<h1>PA.PEI.</h1>
		<h3>The ultimate search engine</h3>
		<form id="form">
			<div id="searchContainer">
				<input name="searchText" id="searchText" type="text" />
				<div style="margin-top:1em;">
					<select name="type" id="dropdown">
						<option value="all" selected>All</option>
						<option value="author">Author - (A)</option>
						<option value="synopsis">Synopsis - (W)</option>
						<option value="title">Title - (T)</option>
					</select>
				</div>
			</div>
			<button id="submit" name="submit" type="button" onclick="getResults();">Search</button>
		</form>
	</div>

	<div id="resultsDiv" style="display: none;">
		<h2>Results:</h2>
		<div id="tableResultsDiv"></div>
	</div>
</body>
</html>