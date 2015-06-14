package edu.papei.lucene.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import edu.papei.lucene.constant.Globals;
import edu.papei.lucene.constant.Globals.Fields;
import edu.papei.lucene.model.CacmDocument;

/**
 * The indexer controller servlet @URL: /indexer
 */
public class IndexerController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String docsPath = "input/cacm/cacm.all";
	private static final String indexPath = "index";
	
	/**
	 * GET request to '/indexer' URL in order to start the index process.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String result = parseCacm(this.getServletContext());
		request.setAttribute("result", result);
		RequestDispatcher view = request.getRequestDispatcher("indexer.jsp");
		view.forward(request, response);
	}

	/**
	 * This method parses line by line the cacm.all file and index the appropriate 
	 * fields (author, title, synopsis).
	 * 
	 * @param servletContext the servlet context
	 * @return the response to be returned to the client (browser).
	 */
	private static String parseCacm(ServletContext servletContext) {
		File file = new File(servletContext.getRealPath(docsPath));
		BufferedReader br;
		String response = "", line;
		Date start = new Date(); // set start time of index process

		try {
			br = new BufferedReader(new FileReader(file));
			char field = 0;
			CacmDocument document = null;
			ArrayList<CacmDocument> docList = new ArrayList<CacmDocument>();
			// Read cacm.all file line by line
			while ((line = br.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}
				// if line starts with '.'
				if (line.charAt(0) == Fields.PREFIX) {
					field = line.charAt(1);
					// if line starts with '.I'
					if (field == Fields.ID) {
						if (document != null) {
							docList.add(document);
						}
						// create new doc instance and pass the id value
						document = new CacmDocument();
						document.setId(Integer.parseInt(line.substring(2)
								.trim()));
					}
				} else {
					if (field == Fields.AUTHORS) { // line starts with '.A'
						document.addAuthor(line);
					} else if (field == Fields.TITLE) { // line starts with '.T'
						document.addTitle(line);
					} else if (field == Fields.SYNOPSIS) { // line starts with '.W'
						document.addSynopsis(line);
					}
				}
			}
			docList.add(document); // add last document
			indexDocuments(docList, servletContext);
			Date end = new Date(); // set end time of index process
			response = "Indexing finished successfully at " + (end.getTime() - start.getTime()) + " total milliseconds";
			br.close();
		} catch (FileNotFoundException e) {
			response = "File not found.";
		} catch (IOException e) {
			response = "File error on read.";
		}
		return response;
	}

	/**
     * Indexes the given list of CacmDocument.
   	 * 
	 * @param docList the list of documents on cacm.all file
	 * @param servletContext the servlet context
	 * @throws IOException the IO exception
	 */
	private static void indexDocuments(ArrayList<CacmDocument> docList,
			ServletContext servletContext) throws IOException {
		
		// Using the standardAnalyzer filtering out stop-words by using the stopWords provided
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46,
				new FileReader(new File(servletContext.getRealPath(Globals.STOPWORDS))));
		
		// IndexWriterConfig instance holds all configuration for IndexWriter. 
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46, analyzer);

		// set the OpenMode as 'create' that creates a new index or overwrites an existing one.
		iwc.setOpenMode(OpenMode.CREATE);

		// create a directory to store the index
		Directory directory = FSDirectory.open(new File(servletContext.getRealPath(indexPath)));
		
		// Instantiate an IndexWriter which is used to create the index and to 
		// add new index entries (i.e., Documents) to this index.
		IndexWriter writer = new IndexWriter(directory, iwc);

		// Add each document's field to index
		for (CacmDocument cacmDoc : docList) {
			// make a new, empty document
			Document doc = new Document();

			// Add the id, title, synopsis, author of the document as fields named "id", "title"
			// "synopsins" and "author" accordingly. In addition store the values to the index.
			doc.add(new TextField("id", String.valueOf(cacmDoc.getId()), Field.Store.YES)); //use standardAnalyzer for 'id' field
			doc.add(createField("title", cacmDoc.getTitle())); //use porterStem algorithm
			doc.add(createField("synopsis", cacmDoc.getSynopsis()));
			doc.add(createField("author", cacmDoc.getAuthors()));

			// add the document to the writer
			writer.addDocument(doc);
		}
		writer.close();
	}
	
	/**
	 * Index fields by the use of the Porter Stem algorithm.
	 * Helper method used to create a {@link TextField} for the provided parameters.
	 *
	 * @param name the name of the field
	 * @param value the value to place in the field
	 * @return the generated {@link TextField} instance
	 */
	private static TextField createField(String name, String value) {
		TextField field = new TextField(name, value, Field.Store.YES);
		try {
			field.setTokenStream(new PorterStemFieldAnalyzer().tokenStream(name, new StringReader(value)));
		} catch (IOException ioe) {
			System.out.println("Error occurred while trying to analyse field with porter stem algorithm: \n" + ioe);
		}
		return field;
	}

}
