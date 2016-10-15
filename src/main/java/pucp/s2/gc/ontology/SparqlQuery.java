package pucp.s2.gc.ontology;

/**
 * Created by adun on 08/10/2016.
 */
public class SparqlQuery {

  public static final String SELECT_ALL = "" +
      "SELECT * { ?s ?p ?o }";

  public static final String SELECT_SLANG_SYN = "" +
      "\nSELECT ?word ?slang ?slang_word " +
      "\nWHERE {" +
      "\n?s <http://pucp.s2.gc.ontology/slang#value> \"piña\" ." +
      "\n?word <http://pucp.s2.gc.ontology/slang#hasSlang> ?s ." +
      "\n?word <http://pucp.s2.gc.ontology/slang#hasSlang> ?slang ." +
      "\n?slang <http://pucp.s2.gc.ontology/slang#value> ?slang_word ." +
      "\n} " +
      "";

  // very important: isAntonymOf property must be: TRANSITIVE, SYMMETRIC
  public static final String SELECT_WORD_SYN = "" +
      "\nSELECT *" +
      "\nWHERE {" +
      "\n?word <http://pucp.s2.gc.ontology/slang#value> \"enojado\" ." +
      "\n?synonym <http://pucp.s2.gc.ontology/slang#isSynonymOf> ?word ." +
      "\n?synonym <http://pucp.s2.gc.ontology/slang#value> ?synonym_word ." +
      "\n} " +
      "";

  public static final String SELECT_WORD_SYN_SLANG = "" +
      "\nSELECT *" +
      "\nWHERE {" +
      "\n?word <http://pucp.s2.gc.ontology/slang#value> \"molesto\" ." +
      "\n?synonym <http://pucp.s2.gc.ontology/slang#isSynonymOf> ?word ." +
      "\n?synonym <http://pucp.s2.gc.ontology/slang#hasSlang> ?slang ." +
      "\n?slang <http://pucp.s2.gc.ontology/slang#value> ?slang_word ." +
      "\n} " +
      "";

  // very important: isAntonymOf property must be: TRANSITIVE, SYMMETRIC, REFLEXIVE
  public static final String SELECT_WORD_ANT = "" +
      "\nSELECT *" +
      "\nWHERE {" +
      "\n?word <http://pucp.s2.gc.ontology/slang#value> \"suertudo\" ." +
      "\n?antonym <http://pucp.s2.gc.ontology/slang#isAntonymOf> ?word ." +
      "\n?antonym <http://pucp.s2.gc.ontology/slang#value> ?antonym_word ." +
      "\n} " +
      "";

  public static final String SELECT_WORD_ANT_SLANG = "" +
      "\nSELECT *" +
      "\nWHERE {" +
      "\n?slang <http://pucp.s2.gc.ontology/slang#value> \"lechero\" ." +
      "\n?word <http://pucp.s2.gc.ontology/slang#hasSlang> ?slang ." +
      "\n?antonym <http://pucp.s2.gc.ontology/slang#isAntonymOf> ?word ." +
      "\n?antonym <http://pucp.s2.gc.ontology/slang#hasSlang> ?antonym_slang ." +
      "\n} " +
      "";

  public static final String SELECT_SLANG_MEANING = "" +
      "\nSELECT *" +
      "\nWHERE {" +
      "\n?slang <http://pucp.s2.gc.ontology/slang#value> \"cana\" ." +
      "\n?word <http://pucp.s2.gc.ontology/slang#hasSlang> ?slang ." +
      "\n?word <http://pucp.s2.gc.ontology/slang#meaning> ?meaning ." +
      "\n} " +
      "";

}
