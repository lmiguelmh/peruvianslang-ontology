/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pucp.s2.gc.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

/**
 * @author Guest
 */
public class Searcher {
  private String indexDir = "ir-dir";
  private Version version;
  private Directory directory;
  private DirectoryReader directoryReader;
  private Analyzer analyzer;
  private IndexSearcher searcher;

  private Searcher(String indexDir) {
    this.indexDir = indexDir;
  }

  private static Searcher instance = null;

  public static Searcher getInstance(String indexDir) throws IOException {
    if(indexDir == null) {
      return instance;
    }
    if(instance == null) {
      instance = new Searcher(indexDir);
      instance.crearBuscador();
    }
    return instance;
  }

  private void crearBuscador() throws IOException {
    directory = FSDirectory.open(new File(indexDir));
    directoryReader = DirectoryReader.open(directory);
    directory.close();
    searcher = new IndexSearcher(directoryReader);
    //analyzer = new StandardAnalyzer();
    analyzer = new SpanishAnalyzer();
  }

  public void cerrarBuscador() throws IOException {
    directoryReader.close();
  }

  public ScoreDoc[] buscar(String text) throws ParseException, IOException {
    ScoreDoc[] list;
    QueryParser parser = new QueryParser("text", analyzer);
    Query query = parser.parse(text);
    list = searcher.search(query, 100).scoreDocs;
    return list;
  }

  public ScoreDoc[] buscarPorClase(String clazz) throws ParseException, IOException {
    ScoreDoc[] list;
    QueryParser parser = new QueryParser("class", analyzer);
    Query query = parser.parse(clazz);
    list = searcher.search(query, 100).scoreDocs;
    return list;
  }

  public String visualizarDocumentos(ScoreDoc[] list) throws IOException {
    String results = "";
    for (ScoreDoc scoreDoc : list) {
      Document doc = searcher.doc(scoreDoc.doc);
      String title = doc.get("title");
      //System.out.println(scoreDoc.doc + "-" + scoreDoc.score + "-" + text);
      results += scoreDoc.doc + "-" + scoreDoc.score + "-" + title + "\n";
    }
    return results;
  }

  public String visualizarDocumentos2(ScoreDoc[] list) throws IOException {
    StringBuilder results = new StringBuilder();
      results.append("<table style=\"width:100%\" border=\"1\"><tr><th>ID</th><th>SCORE</th><th>DOCUMENT</th></tr>");
    for (ScoreDoc scoreDoc : list) {
      Document doc = searcher.doc(scoreDoc.doc);
      String title = doc.get("title");
      //System.out.println(scoreDoc.doc + "-" + scoreDoc.score + "-" + text);
      //results += "<p>"+scoreDoc.doc + "-" + scoreDoc.score + "- <a href=\""+new File(title).toURI().toURL()+"\">" + title + "</a></p>";
      results.append("<td>" +scoreDoc.doc+ "</td><td>" +scoreDoc.score+ "</td><td><a href=\""+new File(title).toURI().toURL()+"\">" + title + "</a></td></tr>");
    }
    results.append("</table>");
    return results.toString();
  }
}
