/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmiguelmh.test.examples.s3;

import java.net.URISyntaxException;
import java.util.Iterator;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * @author amelgar
 */
public class Ejemplo08 {
  public static void main(String[] args) throws URISyntaxException {
    // create the base model
//        String nmArchivo = "/Users/amelgar/documents/2006-09-21.rdf";
    OntModel base = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
//        base.read(getURIFile(nmArchivo));
    base.read(Ejemplo08.class.getClassLoader().getResource("2006-09-21.rdf").toURI().toString());

    //obtenemos el modelo leido
    //Model baseModel = base.getBaseModel();
    //baseModel.write(System.out);
    //se puede imprimir directamente tambien
    //base.write(System.out);

    String NS = "http://www.eswc2006.org/technologies/ontology#";
    String uriDocumento = "http://xmlns.com/wordnet/1.6/Document";
    OntClass document = base.getOntClass(uriDocumento);
    System.out.println("Documento: uri = " + document.getURI());

    Iterator<OntClass> iter = document.listSubClasses(false);
    while (iter.hasNext()) {
      OntClass claseOWL = iter.next();
      System.out.println("clases hijas: " + claseOWL.getURI());
    }

    // create the reasoning model using the base
    OntModel inf = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, base);
    document = inf.getOntClass(uriDocumento);
    System.out.println("Documento: uri = " + document.getURI());
    iter = document.listSubClasses(); //probar cambiando

    while (iter.hasNext()) {
      OntClass claseOWL = iter.next();
      System.out.println("clases hijas 2: " + claseOWL.getURI());
    }

    OntClass poster = base.getOntClass(NS + "Poster");
    iter = poster.listSuperClasses();
    while (iter.hasNext()) {
      OntClass claseOWL = iter.next();
      System.out.println("clases padres de Poster: " + claseOWL.getURI());
    }

    poster = inf.getOntClass(NS + "Poster");
    iter = poster.listSuperClasses(true);
    while (iter.hasNext()) {
      OntClass claseOWL = iter.next();
      System.out.println("clases padres: " + claseOWL.getURI());
    }

    OntClass person = base.getOntClass("http://xmlns.com/foaf/0.1/Person");
    StmtIterator iterStmt = person.listProperties();
    while (iterStmt.hasNext()) {
      System.out.println("  propiedades de la persona: " + iterStmt.nextStatement().toString());
    }

    OntClass paper = base.getOntClass(NS + "Paper");
    Individual p1 = base.createIndividual(NS + "paper1", paper);

    // list the asserted types
    Iterator<Resource> i = p1.listRDFTypes(false);
    while (i.hasNext()) {
      System.out.println(p1.getURI() + " is asserted in class " + i.next());
    }

    // list the inferred types
    p1 = inf.getIndividual(NS + "paper1");
    i = p1.listRDFTypes(false);
    while (i.hasNext()) {
      System.out.println(p1.getURI() + " is asserted in class " + i.next());
    }

    OntClass artefact = base.getOntClass(NS + "Artefact");
    for (Iterator<OntClass> j = artefact.listSubClasses(); j.hasNext(); ) {
      OntClass c = j.next();
      System.out.println(c.getURI());
    }
  }

  private static String getURIFile(String nmArchivo) {
    return "file:///" + nmArchivo;
  }
}
