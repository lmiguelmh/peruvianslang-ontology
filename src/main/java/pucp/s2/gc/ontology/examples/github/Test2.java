package pucp.s2.gc.ontology.examples.github;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;

/**
 * Created by adun on 07/10/2016.
 */
public class Test2 {

  private static final String BASE = "http://example.org/";

  public static void main(String[] args) {
    Model model = ModelFactory.createDefaultModel();

    Resource subject = r("s");

    model.addLiteral (subject, p("p1"), 10);
    model.addLiteral (subject, p("p2"), 0.5);
    model.addLiteral (subject, p("p3"), (float)0.5);
    model.addLiteral (subject, p("p4"), l(20));
    model.addLiteral (subject, p("p5"), l(0.99));
    model.addLiteral (subject, p("p6"), true);
    model.add (subject, p("p7"), l("2012-03-11", XSDDatatype.XSDdate));
    model.add (subject, p("p8"), l("P2Y", XSDDatatype.XSDduration));

    model.setNsPrefix("example", BASE);
    model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");

    model.write(System.out, "TURTLE");
    model.write(System.out, "RDF/XML");
  }

  private static Resource r ( String localname ) {
    return ResourceFactory.createResource ( BASE + localname );
  }

  private static Property p (String localname ) {
    return ResourceFactory.createProperty ( BASE, localname );
  }

  private static Literal l ( Object value ) {
    return ResourceFactory.createTypedLiteral ( value );
  }

  private static Literal l ( String lexicalform, RDFDatatype datatype ) {
    return ResourceFactory.createTypedLiteral ( lexicalform, datatype );
  }

}
