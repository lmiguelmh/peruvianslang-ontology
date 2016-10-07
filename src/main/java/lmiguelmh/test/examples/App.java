package lmiguelmh.test.examples;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Derivation;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.util.PrintUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

/**
 * Hello world!
 */
public class App {
  public static void main(String[] args) throws URISyntaxException, IOException {
    OntModel base = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
    base.read(App.class.getClassLoader().getResource("slang.owl").toURI().toString());

    Model model = base.getBaseModel();

    /*
    System.out.println("listStatements");
    StmtIterator iter = model.listStatements();
    while (iter.hasNext()) {
      System.out.println(iter.nextStatement().toString());
    }

    System.out.println("listSubjects");
    ResIterator resIter = model.listSubjects();
    while (resIter.hasNext()) {
      Resource res = resIter.nextResource();
      System.out.println(res);
    }

    System.out.println("listSubjectsWithProperty");
    resIter = model.listSubjectsWithProperty(VCARD.FN);
    while (resIter.hasNext()) {
      Resource res = resIter.nextResource();
      System.out.print(res);
      String fullName = res.getProperty(VCARD.FN).getString();
      System.out.println(" " + fullName);
    }
    */

    System.out.println("SimpleSelector");
//    String personURI = "http://lmiguelmh.test/slang/peruvian.owl#desafortunado";
//    String personURI = "http://lmiguelmh.test/slang/peruvian.owl#piña";
    String personURI = "http://lmiguelmh/slang#piña";
    Resource person = model.getResource(personURI);
    Selector selector = new SimpleSelector(person, null, (RDFNode) null);
    StmtIterator iter2 = model.listStatements(selector);
    while (iter2.hasNext()) {
      System.out.println(iter2.nextStatement().toString());
    }

    Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
    InfModel infmodel = ModelFactory.createInfModel(reasoner, base.getBaseModel());
    //validar la consistencia
    ValidityReport validity = infmodel.validate();
    if (validity.isValid()) {
      System.out.println("OK");
    } else {
      System.out.println("Conflicts");
      for (Iterator i = validity.getReports(); i.hasNext(); ) {
        ValidityReport.Report report = (ValidityReport.Report) i.next();
        System.out.println(" - " + report);
      }
    }

//    InfModel inf = ModelFactory.createRDFSModel(rdfsExample);
    Resource a = infmodel.getResource("http://lmiguelmh/slang#piña");

    for (StmtIterator it = a.listProperties(); it.hasNext(); ) {
      Statement s = it.nextStatement();
      System.out.println(s);
    }
//    Statement statement =  a.getProperty()
    Property isSlangOf = model.getProperty("http://lmiguelmh/slang#isSlangOf");
    System.out.println(a.getProperty(isSlangOf));
    System.out.println(a.getProperty(isSlangOf).getSubject());
    System.out.println(a.getProperty(isSlangOf).getPredicate());
    System.out.println(a.getProperty(isSlangOf).getObject());

    Resource b = infmodel.getResource(a.getProperty(isSlangOf).getObject().toString());
    for (StmtIterator it = b.listProperties(); it.hasNext(); ) {
      Statement s = it.nextStatement();
      System.out.println(s);
    }

    Iterator<Derivation> derivation = infmodel.getDerivation(a.getProperty(isSlangOf));
    System.out.println("derivation");
    System.out.println(derivation);
//    for(;derivation.hasNext();) {
//      Derivation d = derivation.next();
//      System.out.println(d);
//    }

//    System.out.println(": " + a);


    {
      String queryString = "SELECT * { ?s ?p ?o }";
      Query query = QueryFactory.create(queryString);
      QueryExecution qexec = QueryExecutionFactory.create(query, model);
      try {
        ResultSetRewindable results = ResultSetFactory.makeRewindable(qexec.execSelect());

        System.out.println("---- XML ----");
        ResultSetFormatter.outputAsXML(System.out, results);
        results.reset();

        System.out.println("---- Text ----");
        ResultSetFormatter.out(System.out, results);
        results.reset();

        System.out.println("\n---- CSV ----");
        ResultSetFormatter.outputAsCSV(System.out, results);
        results.reset();

        System.out.println("\n---- TSV ----");
        ResultSetFormatter.outputAsTSV(System.out, results);
        results.reset();

        System.out.println("\n---- JSON ----");
        ResultSetFormatter.outputAsJSON(System.out, results);
        results.reset();
      } finally {
        qexec.close();
      }
    }


    /*
    {
      Query query = QueryFactory.create("SELECT * WHERE { ?s ?p ?o }");
      QueryExecution qexec = QueryExecutionFactory.create(query, model);

      FileOutputStream out = new FileOutputStream("sxssf.xlsx");
      Workbook wb = new SXSSFWorkbook(100);
      Sheet sh = wb.createSheet();

      int rows = 0;
      int columns = 0;
      try {
        ResultSet resultSet = qexec.execSelect();
        List<String> varNames = resultSet.getResultVars();
        List<Var> vars = new ArrayList<Var>(varNames.size());

        // first row with var names
        Row row = sh.createRow(rows++);
        for (String varName : varNames) {
          Var var = Var.alloc(varName);
          Cell cell = row.createCell(columns++);
          cell.setCellValue(var.toString());
          vars.add(var);
        }

        // other rows with bindings
        while (resultSet.hasNext()) {
          Binding bindings = resultSet.nextBinding();
          row = sh.createRow(rows++);
          columns = 0;
          for (Var var : vars) {
            Node n = bindings.get(var);
            if (n != null) {
              Cell cell = row.createCell(columns++);
              String value = FmtUtils.stringForNode(n, (SerializationContext) null);
              cell.setCellValue(value);
            }
          }
        }
      } finally {
        qexec.close();
      }

      wb.write(out);
      out.close();
    }
    */

  }

  public static void printStatements(Model m, Resource s, Property p, Resource o) {
    for (StmtIterator i = m.listStatements(s, p, o); i.hasNext(); ) {
      Statement stmt = i.nextStatement();
      System.out.println(" - " + PrintUtil.print(stmt));
    }
  }

}
