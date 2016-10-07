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
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;

/**
 *
 * @author amelgar
 */
public class Ejemplo07 {
    
    public static void main(String[] args) {
        String inputSchemaName = "owlDemoData.xml";
        String inputDataName = "owlDemoSchema.xml";
        Model schema = FileManager.get().loadModel(inputSchemaName);
        Model data = FileManager.get().loadModel(inputDataName);

        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        reasoner = reasoner.bindSchema(schema);
        InfModel infmodel = ModelFactory.createInfModel(reasoner, data);

        //conocer las clases de una instancia
        Resource resource = infmodel.getResource("urn:x-hp:eg/bigName42");
        System.out.println("bigName42 *:");
        printStatements(infmodel, resource, null, null);
        
        resource = infmodel.getResource("urn:x-hp:eg/whiteBoxZX");
        System.out.println("whiteBoxZX *:");
        printStatements(infmodel, resource, null, null);

        //verificar si un individuo pertenece a una clase
        Resource gamingComputer = infmodel.getResource("urn:x-hp:eg/GamingComputer");
        Resource whiteBox = infmodel.getResource("urn:x-hp:eg/whiteBoxZX");
        if (infmodel.contains(whiteBox, RDF.type, gamingComputer)) {
            System.out.println("White box recognized as gaming computer");
        } else {
            System.out.println("Failed to recognize white box correctly");
        }

        //validar la consistencia
        ValidityReport validity = infmodel.validate();
        if (validity.isValid()) {
            System.out.println("OK");
        } else {
            System.out.println("Conflicts");
            for (Iterator i = validity.getReports(); i.hasNext();) {
                ValidityReport.Report report = (ValidityReport.Report) i.next();
                System.out.println(" - " + report);
            }
        }
    }

    public static void printStatements(Model m, Resource s, Property p, Resource o) {
        for (StmtIterator i = m.listStatements(s, p, o); i.hasNext();) {
            Statement stmt = i.nextStatement();
            System.out.println(" - " + PrintUtil.print(stmt));
        }
    }
}
