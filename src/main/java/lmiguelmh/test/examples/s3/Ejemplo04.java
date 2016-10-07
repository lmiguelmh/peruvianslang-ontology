/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmiguelmh.test.examples.s3;

import java.util.Iterator;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.util.FileManager;

/**
 *
 * @author amelgar
 */
public class Ejemplo04 {
    public static void main(String[] args) {
        //String inputFileName = "dttest1.nt";
        //String inputFileName = "dttest2.nt";
        String inputFileName = "dttest22.nt";
        Model data = FileManager.get().loadModel(inputFileName);

        InfModel infmodel = ModelFactory.createRDFSModel(data);
        ValidityReport validity = infmodel.validate();
        if (validity.isValid()) {
            System.out.println("OK");
        } else {
            System.out.println("Conflicts");
            for (Iterator i = validity.getReports(); i.hasNext();) {
                System.out.println(" - " + i.next());
            }
        }
    }
}
