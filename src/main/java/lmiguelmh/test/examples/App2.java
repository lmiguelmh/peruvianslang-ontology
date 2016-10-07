package lmiguelmh.test.examples;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Created by adun on 07/10/2016.
 */
public class App2 {
  public static void main(String[] args) throws IOException {
    InputStream slangIs = App2.class.getClassLoader().getResourceAsStream("slang.owl");
    Model model = readModelFromFactory(slangIs);
    //listStatements(model);

    // Create an OWL reasoner
    Reasoner owlReasoner = ReasonerRegistry.getOWLReasoner();
    Reasoner wnReasoner = owlReasoner.bindSchema(model);
    InfModel infModel = ModelFactory.createInfModel(wnReasoner, model);
    //validate(infModel);

//    String queryString = "SELECT * { ?s ?p ?o }";
//    String queryString = "" +
//        " SELECT " +
//        " * " +
//        " WHERE " +
//        " {" +
//        " ?slang  <http://lmiguelmh/slang#meaning> \"piña\"." +
//        " ?slang  <http://lmiguelmh/slang#value> ?v" +
//        " } " +
//        "";
    String queryString = "" +
        " SELECT " +
        " ?v " +
        " WHERE " +
        " {" +
        " ?slang <http://lmiguelmh/slang#meaning> \"piña\" ." +
        " ?word <http://lmiguelmh/slang#hasSlang> ?slang ." +
        " ?word <http://lmiguelmh/slang#hasSlang> ?s ." +
        " ?s <http://lmiguelmh/slang#value> ?v ." +
        " } " +
        "";
    query(infModel, queryString);


    /*
    Resource piña = model.getResource("http://lmiguelmh/slang#piña");
    Property isSlangOf = model.getProperty("http://lmiguelmh/slang#isSlangOf");
    Iterator<Derivation> derivation = infModel.getDerivation(piña.getProperty(isSlangOf));
    System.out.println("derivation");
    System.out.println(derivation);
    */
  }

  // http://scis.athabascau.ca/html/lo/repos/comp492/jena/ProgrammingRDQL.htm
  //https://jena.apache.org/tutorials/sparql_query1.html/
  public static void query(Model model, String queryString) {
    System.out.println("Executing query: " + queryString);
    QueryExecution qexec = QueryExecutionFactory.create(queryString, model);
    try {
      ResultSetRewindable results = ResultSetFactory.makeRewindable(qexec.execSelect());

      /*
      System.out.println("---- XML ----");
      ResultSetFormatter.outputAsXML(System.out, results);
      results.reset();
      */

//      System.out.println("---- Text ----");
      ResultSetFormatter.out(System.out, results);
      results.reset();

      /*
      System.out.println("\n---- CSV ----");
      ResultSetFormatter.outputAsCSV(System.out, results);
      results.reset();

      System.out.println("\n---- TSV ----");
      ResultSetFormatter.outputAsTSV(System.out, results);
      results.reset();

      System.out.println("\n---- JSON ----");
      ResultSetFormatter.outputAsJSON(System.out, results);
      results.reset();
      */
    } finally {
      qexec.close();
    }
  }

  public static String query(Model model, String queryString, String type) {
    System.out.println("Executing query: " + queryString);
    QueryExecution qexec = QueryExecutionFactory.create(queryString, model);
    try {
      ResultSetRewindable results = ResultSetFactory.makeRewindable(qexec.execSelect());

      /*
      System.out.println("---- XML ----");
      ResultSetFormatter.outputAsXML(System.out, results);
      results.reset();
      */

//      System.out.println("---- Text ----");
      if("txt".equalsIgnoreCase(type)) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ResultSetFormatter.out(os, results);
        results.reset();
        return new String(os.toByteArray(), Charset.defaultCharset());
      }
      return "";

      /*
      System.out.println("\n---- CSV ----");
      ResultSetFormatter.outputAsCSV(System.out, results);
      results.reset();

      System.out.println("\n---- TSV ----");
      ResultSetFormatter.outputAsTSV(System.out, results);
      results.reset();

      System.out.println("\n---- JSON ----");
      ResultSetFormatter.outputAsJSON(System.out, results);
      results.reset();
      */
    } finally {
      qexec.close();
    }
  }

  public static void validate(InfModel infModel) {
    ValidityReport validity = infModel.validate();
    if (validity.isValid()) {
      System.out.println("OK");
    } else {
      System.out.println("Conflicts");
      for (Iterator i = validity.getReports(); i.hasNext(); ) {
        ValidityReport.Report report = (ValidityReport.Report) i.next();
        System.out.println(" - " + report);
      }
    }
  }

  public static Model readModelFromFactory(InputStream in) throws IOException {
    Model model = ModelFactory.createDefaultModel();
    model.read(in, null);
    in.close();
    return model;
  }

  public static void listStatements(Model model) {
    System.out.println("listStatements");
    StmtIterator iter = model.listStatements();
    while (iter.hasNext()) {
      System.out.println(iter.nextStatement().toString());
    }
    System.out.println();
  }
}
