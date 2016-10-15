/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pucp.s2.gc.ontology.examples.s4;

import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.VCARD;

import java.io.InputStream;

/**
 *
 * @author amelgar
 */
public class Ejemplo06 {
    
    public static void main(String[] args){
        System.out.println("Leyendo RDF");
        
        String inputFileName = "vc-db-1.rdf";
        Model model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open(inputFileName);
        model.read(in, "");
        
        System.out.println("listStatement");
        StmtIterator iter = model.listStatements();
        while (iter.hasNext()){
            System.out.println(iter.nextStatement().toString());
        }
        
        System.out.println("listSubjects");
        ResIterator resIter = model.listSubjects();
        while (resIter.hasNext()){
            System.out.println(resIter.nextResource().toString());
        }
        
        System.out.println("listSubjectsWithProperty");
        resIter = model.listSubjectsWithProperty(VCARD.FN);
        while (resIter.hasNext()){
            System.out.println(resIter.nextResource().toString());
        }
    }
    
}
