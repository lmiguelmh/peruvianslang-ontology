/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmiguelmh.test.examples.s3;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * @author amelgar
 */
public class Ejemplo03 {

    public static void main(String[] args) {
        String NS = "urn:x-hp-jena:eg/";
        Model rdfsExample = ModelFactory.createDefaultModel();
        Property p = rdfsExample.createProperty(NS, "p");
        Property q = rdfsExample.createProperty(NS, "q");
        rdfsExample.add(p, RDFS.subPropertyOf, q);
        rdfsExample.createResource(NS + "a").addProperty(p, "foo");
        rdfsExample.write(System.out);
        //http://www.w3.org/RDF/Validator/

        Resource a = rdfsExample.getResource(NS + "a");
        System.out.println("Statement p: " + a.getProperty(p));
        System.out.println("Statement q: " + a.getProperty(q));

        InfModel inf = ModelFactory.createRDFSModel(rdfsExample);
        a = inf.getResource(NS + "a");
        System.out.println("Statement q: " + a.getProperty(q));

    }
}
