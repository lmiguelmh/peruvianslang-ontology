package pucp.s2.gc.ontology.examples.s4;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.VCARD;

public class Ejemplo03 {

    public static void main(String[] args){
        System.out.println("Creando RDF...");
        String personURI = "http://somewhere/JonhSmith";
        String fullName = "Jonh Smith";
        String givenName = "Jonh";
        String familyName = "Smith";
        
        //creamos el modelo para gestionar el RDF
        Model model = ModelFactory.createDefaultModel();
        
        //crear el recurso en el modelo
        Resource jonhSmith = model.createResource(personURI);
        jonhSmith.addProperty(VCARD.FN, fullName);
        
        //agregar nodo en blanco
        Resource blankNode = model.createResource();
        
        //agregar propiedades al nodo en blanco
        blankNode.addProperty(VCARD.Given, givenName);
        blankNode.addProperty(VCARD.Family, familyName);
        
        //al recurso JonhSmith lo vinculamos con el nodo en blanco
        jonhSmith.addProperty(VCARD.N, blankNode);
        
        //recorrer todas las afirmaciones
        System.out.println("Recorriendo las afirmaciones...");
        StmtIterator iter = model.listStatements();
        while (iter.hasNext()){
            Statement stmt = iter.nextStatement();
            Resource subject = stmt.getSubject();
            Property predicate = stmt.getPredicate();
            RDFNode object = stmt.getObject();
            
            System.out.print(subject.toString());
            System.out.print(" - " + predicate.toString() + " - ");
            if (object instanceof Resource)
              System.out.println(object.toString());
            else
                System.out.println("\""+object.toString()+"\"");
        }
    }
}
