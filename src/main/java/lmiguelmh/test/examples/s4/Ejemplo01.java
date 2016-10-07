package lmiguelmh.test.examples.s4;

import org.apache.jena.rdf.model.ModelFactory;

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

public class Ejemplo01 {

    public static void main(String[] args){
        System.out.println("Creando RDF...");
        String personURI = "http://somewhere/JonhSmith";
        String fullName = "Jonh Smith";
        
        //creamos el modelo para gestionar el RDF
        Model model = ModelFactory.createDefaultModel();
        
        //crear el recurso en el modelo
        Resource jonhSmith = model.createResource(personURI);
        jonhSmith.addProperty(VCARD.FN, fullName);
        
        //imprimir el modelo en formato RDF
        //model.write(System.out);
        //imprimir el modelo en formato de triplas
        model.write(System.out, "N-TRIPLES");
        //imprimir el modelo en formato RDF abreviado
        //model.write(System.out, "RDF/XML-ABBREV");
    }
}
