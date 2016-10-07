package lmiguelmh.test.examples.github;

import java.util.Iterator;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

public class ExampleONT_01 {

  public static void main(String[] args) {
    String sourceURL = "http://www.eswc2006.org/technologies/ontology";
    String namespace = sourceURL + "#";
    OntModel base = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
    base.read(sourceURL, "RDF/XML");

    OntModel inf = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, base);

    OntClass paperClass = base.getOntClass(namespace + "Paper");
    Individual paper = base.createIndividual(namespace + "paper1", paperClass);

    System.out.println("---- Assertions in the data ----");
    for (Iterator<Resource> i = paper.listRDFTypes(false); i.hasNext(); ) {
      System.out.println(paper.getURI() + " is a " + i.next());
    }

    System.out.println("\n---- Inferred assertions ----");
    paper = inf.getIndividual(namespace + "paper1");
    for (Iterator<Resource> i = paper.listRDFTypes(false); i.hasNext(); ) {
      System.out.println(paper.getURI() + " is a " + i.next());
    }
  }
}