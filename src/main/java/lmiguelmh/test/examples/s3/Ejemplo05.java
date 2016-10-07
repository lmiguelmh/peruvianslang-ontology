/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmiguelmh.test.examples.s3;

import java.io.PrintWriter;
import java.util.Iterator;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Derivation;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;

/**
 *
 * @author amelgar
 */
public class Ejemplo05 {
    public static void main(String[] args) {
        String NS = "urn:x-hp:eg/";
        
        Model rdfsExample = ModelFactory.createDefaultModel();

        Property p = rdfsExample.createProperty(NS, "p");
        Resource a = rdfsExample.createResource(NS + "A");
        Resource b = rdfsExample.createResource(NS + "B");
        Resource c = rdfsExample.createResource(NS + "C");
        Resource d = rdfsExample.createResource(NS + "D");

        //eg:A eg:p eg:B .
        //eg:B eg:p eg:C .
        //eg:C eg:p eg:D .
        a.addProperty(p, b);
        b.addProperty(p, c);
        c.addProperty(p, d);
        rdfsExample.write(System.out);
        //http://www.w3.org/RDF/Validator/

        String rules = "[rule1: (?a eg:p ?b) (?b eg:p ?c) -> (?a eg:p ?c)]";
        Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));
        reasoner.setDerivationLogging(true);
        
        InfModel inf = ModelFactory.createInfModel(reasoner, rdfsExample);

        PrintWriter out = new PrintWriter(System.out);
        for (StmtIterator i = inf.listStatements(a, p, d); i.hasNext();) {
        //for (StmtIterator i = inf.listStatements(a, p, c); i.hasNext();) {
            Statement s = i.nextStatement();
            System.out.println("Statement is " + s);
            for (Iterator id = inf.getDerivation(s); id.hasNext();) {
                Derivation deriv = (Derivation) id.next();
                deriv.printTrace(out, true);
            }
        }
        out.flush();
    }
}
