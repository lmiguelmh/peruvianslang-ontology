package pucp.s2.gc.ontology.examples.s3;

import java.io.InputStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.VCARD;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author amelgar
 */
public class Ejemplo01 {

    public static void main(String[] args) {
        System.out.println("Consultando RDF...");
        String inputFileName = "src/main/resources/vc-db-1.rdf";
        
        Model model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open(inputFileName);
        model.read(in, "");
        
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

        System.out.println("SimpleSelector");
        String personURI = "http://somewhere/MattJones/";
        Resource person = model.getResource(personURI);
        Selector selector = new SimpleSelector(person, null, (RDFNode)null);
        StmtIterator iter2 = model.listStatements(selector);
        while (iter2.hasNext()) {
            System.out.println(iter2.nextStatement().toString());
        }
    }

}
