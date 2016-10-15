/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pucp.s2.gc.ontology.examples.s3;

import java.util.Iterator;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;

/**
 *
 * @author amelgar
 */
public class Ejemplo06 {
    public static void main(String[] args) {
        String inputSchemaName = "rdfsDemoSchema.rdf";
        String inputDataName = "rdfsDemoData.rdf";
        Model schema = FileManager.get().loadModel(inputSchemaName);
        Model data = FileManager.get().loadModel(inputDataName);
        InfModel infmodel = ModelFactory.createRDFSModel(schema, data);

        Resource colin = infmodel.getResource("urn:x-hp:eg/colin");
        System.out.println("colin has types:");
        printStatements(infmodel, colin, RDF.type, null);

        Resource Person = infmodel.getResource("urn:x-hp:eg/Person");
        System.out.println("\nPerson has types:");
        printStatements(infmodel, Person, RDF.type, null);

        ValidityReport validity = infmodel.validate();
        if (validity.isValid()) {
            System.out.println("\nOK");
        } else {
            System.out.println("\nConflicts");
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
