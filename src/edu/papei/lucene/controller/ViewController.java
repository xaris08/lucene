package edu.papei.lucene.controller;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import edu.papei.lucene.constant.Globals;
import edu.papei.lucene.model.CacmDocument;

/**
 *	View Controller class to search for a specific document by its ID.
 */
public class ViewController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private ServletContext servletContext;

	/**
	 * GET request to '/view?id=' URL in order to find a document by its id.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		servletContext = this.getServletContext();

		String id = request.getParameter("id");
		CacmDocument doc = null;
		try {
			// search the index to get the document for specific id
			doc = findDocument(id);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// return view to client (browser)
		RequestDispatcher view = request.getRequestDispatcher("view.jsp");
		request.setAttribute("document", doc);
		view.forward(request, response);
	}
	
	/**
	 * Document search in lucene index by the document ID.
	 * @param id the document ID to search for
	 * @return the CacmDocume object
	 * @throws IOException lucene index directory not found
	 * @throws ParseException query parse error exception
	 */
	private CacmDocument findDocument(String id) throws IOException, ParseException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(servletContext.getRealPath(Globals.indexPath))));
		IndexSearcher searcher = new IndexSearcher(reader);

		// query that matches documents containing a term
		TermQuery query = new TermQuery(new Term("id", id.trim()));

		// document results from searching
		TopDocs topDocs = searcher.search(query, Globals.MAX_RESULTS);

		// fill the CacmDocument object to return it to the View
		CacmDocument cacmDoc = new CacmDocument();
		Document doc = searcher.doc(topDocs.scoreDocs[0].doc);
		cacmDoc.setId(Integer.parseInt(doc.get("id")));

		String[] title = doc.get("title").split(",");
		cacmDoc.setTitle(Arrays.asList(title));
		String[] authors = doc.get("author").split(",");
		cacmDoc.setAuthors(Arrays.asList(authors));
		String[] synopsis = doc.get("synopsis").split(",");
		cacmDoc.setSynopsis(Arrays.asList(synopsis));
		return cacmDoc;
	}
}