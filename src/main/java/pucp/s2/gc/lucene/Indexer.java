package pucp.s2.gc.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

public class Indexer {

  private static Indexer instance = null;
  private String indexDir = "ir-dir";
  private Version version;
  private Directory directory;
  private Analyzer analyzer;
  private IndexWriter writer;
  private IndexWriterConfig config;

  private Indexer(String indexDir) {
    this.indexDir = indexDir;
  }

  public static Indexer getInstance(String indexDir) throws IOException {
    if (instance == null) {
      instance = new Indexer(indexDir);
      instance.crearEscritor();
    }
    return instance;
  }

  public void crearEscritor() throws IOException {
    version = Version.LUCENE_4_10_1;
    directory = FSDirectory.open(new File(indexDir));
    //analyzer = new StandardAnalyzer();
    analyzer = new SpanishAnalyzer();
    config = new IndexWriterConfig(version, analyzer);
    writer = new IndexWriter(directory, config);
    eliminarDocumentos();
  }

  public void cerrarEscritor() throws IOException {
    writer.close();
    directory.close();
  }

  public void eliminarDocumentos() throws IOException{
    writer.deleteAll();
  }


  public void indexar(String title, String text) throws IOException {
    Document doc = new Document();
    Field field = new Field("text", text, TextField.TYPE_STORED);
    doc.add(field);
    field = new Field("title", title, TextField.TYPE_STORED);
    doc.add(field);
    writer.addDocument(doc);
  }

  public void indexar(String title, String text, String clazz) throws IOException {
    Document doc = new Document();
    Field field = new Field("text", text, TextField.TYPE_STORED);
    doc.add(field);
    field = new Field("title", title, TextField.TYPE_STORED);
    doc.add(field);
    field = new Field("class", clazz, TextField.TYPE_STORED);
    doc.add(field);
    writer.addDocument(doc);
  }

  public Integer getDocumentsNumber() {
    return writer.numDocs();
  }

  void deleteDocuments() throws IOException {
    writer.deleteAll();
  }
}
