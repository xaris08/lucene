package edu.papei.lucene.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.papei.lucene.constant.Globals;
import edu.papei.lucene.model.CacmDocument;

/**
 * The searcher controller servlet @URL: /searcher
 */
public class SearcherController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private ServletContext servletContext;
	
	/**
	 * GET method controller to return the first page view
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		RequestDispatcher view = request.getRequestDispatcher("index.jsp");
		view.forward(request, response);
	}

	/**
	 * POST method controller to return the lucene's search results
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String fieldType = request.getParameter("type");
		String fieldText = request.getParameter("searchText");

		servletContext = this.getServletContext();
		
		Date start = new Date(); //start search time
		List<CacmDocument> docList = searchResults(fieldType, fieldText);
		Date end = new Date(); //end search time
		
		// pass attributes for the response
		request.setAttribute("elapsedTime", String.valueOf(end.getTime() - start.getTime()));
		request.setAttribute("total", String.valueOf(docList.size()));
		request.setAttribute("documents", docList);
		//return the results.jsp
		RequestDispatcher view = request.getRequestDispatcher("results.jsp");
		view.forward(request, response);
	}
	
	/**
	 * Search request on specific field and text to search for.
	 * 
	 * @param fieldType the field to search for (author, synopsis, title)
	 * @param fieldText the search text
	 * @return the search result (list of CacmDocument)
	 */
	private List<CacmDocument> searchResults(String fieldType, String fieldText) {
		try {
			// Read the index directory
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(servletContext.getRealPath(Globals.indexPath))));
			IndexSearcher searcher = new IndexSearcher(reader);
			// Use the same analyzer as in the index process
//			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46,
//					new FileReader(new File(servletContext.getRealPath(Globals.STOPWORDS))));

			// the result list
			List<CacmDocument> totalResults = new ArrayList<CacmDocument>();
			
			if ("all".equals(fieldType)) {
				String[] fields = {"author", "synopsis", "title"};
				MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_46, fields, new PorterStemFieldAnalyzer());
				Query query = parser.parse(fieldText.trim());
				totalResults.addAll(doSearch(searcher, query));
			} else {
				QueryParser parser = new QueryParser(Version.LUCENE_46, fieldType, new PorterStemFieldAnalyzer());
				Query query = parser.parse(fieldText.trim());
				totalResults.addAll(doSearch(searcher, query));
			}

			reader.close();
			return totalResults;

		} catch (IOException e) {
			System.out.println("No folder found");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null; //return null when exception occurred.
	}
	
	/**
	 * Actual search perform by providing the searcher and the query text
	 * @param searcher the searcher
	 * @param query the query text
	 * @return the search result list
	 */
	private List<CacmDocument> doSearch(IndexSearcher searcher, Query query){
		try {
			// hits returned by the search
			TopDocs topDocs = searcher.search(query, Globals.MAX_RESULTS);
			List<CacmDocument> docList = new ArrayList<CacmDocument>();			
			
			// iterate on all returned hits and costruct the response
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				CacmDocument cacmDoc = new CacmDocument();
				Document doc = searcher.doc(scoreDoc.doc);
				cacmDoc.setId(Integer.parseInt(doc.get("id")));

				String[] title = doc.get("title").split(",");
				cacmDoc.setTitle(Arrays.asList(title));
				String[] authors = doc.get("author").split(",");
				cacmDoc.setAuthors(Arrays.asList(authors));
				String[] synopsis = doc.get("synopsis").split(",");
				cacmDoc.setSynopsis(Arrays.asList(synopsis));
				docList.add(cacmDoc);
			}

			return docList;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
