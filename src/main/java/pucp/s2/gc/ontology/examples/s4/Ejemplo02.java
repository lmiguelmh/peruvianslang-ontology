package pucp.s2.gc.ontology.examples.s4;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.VCARD;

public class Ejemplo02 {

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
        
        //imprimir el modelo en formato RDF
        //model.write(System.out);
        //imprimir el modelo en formato de triplas
        model.write(System.out, "N-TRIPLES");
        //imprimir el modelo en formato RDF abreviado
        //model.write(System.out, "RDF/XML-ABBREV");
    }
}
