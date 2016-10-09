package lmiguelmh.test;

/**
 * Created by adun on 08/10/2016.
 */
public class SparqlQuery {

  public static final String SELECT_ALL = "" +
      "SELECT * { ?s ?p ?o }";

  public static final String SELECT_SLANG_SYN = "" +
      "\nSELECT ?word ?slang ?slang_word " +
      "\nWHERE {" +
      "\n?s <http://lmiguelmh/slang#value> \"piña\" ." +
      "\n?word <http://lmiguelmh/slang#hasSlang> ?s ." +
      "\n?word <http://lmiguelmh/slang#hasSlang> ?slang ." +
      "\n?slang <http://lmiguelmh/slang#value> ?slang_word ." +
      "\n} " +
      "";

  // very important: isAntonymOf property must be: TRANSITIVE, SYMMETRIC
  public static final String SELECT_WORD_SYN = "" +
      "\nSELECT *" +
      "\nWHERE {" +
      "\n?word <http://lmiguelmh/slang#value> \"enojado\" ." +
      "\n?synonym <http://lmiguelmh/slang#isSynonymOf> ?word ." +
      "\n?synonym <http://lmiguelmh/slang#value> ?synonym_word ." +
      "\n} " +
      "";

  public static final String SELECT_WORD_SYN_SLANG = "" +
      "\nSELECT *" +
      "\nWHERE {" +
      "\n?word <http://lmiguelmh/slang#value> \"molesto\" ." +
      "\n?synonym <http://lmiguelmh/slang#isSynonymOf> ?word ." +
      "\n?synonym <http://lmiguelmh/slang#hasSlang> ?slang ." +
      "\n?slang <http://lmiguelmh/slang#value> ?slang_word ." +
      "\n} " +
      "";

  // very important: isAntonymOf property must be: TRANSITIVE, SYMMETRIC, REFLEXIVE
  public static final String SELECT_WORD_ANT = "" +
      "\nSELECT *" +
      "\nWHERE {" +
      "\n?word <http://lmiguelmh/slang#value> \"suertudo\" ." +
      "\n?antonym <http://lmiguelmh/slang#isAntonymOf> ?word ." +
      "\n?antonym <http://lmiguelmh/slang#value> ?antonym_word ." +
      "\n} " +
      "";

  public static final String SELECT_WORD_ANT_SLANG = "" +
      "\nSELECT *" +
      "\nWHERE {" +
      "\n?slang <http://lmiguelmh/slang#value> \"lechero\" ." +
      "\n?word <http://lmiguelmh/slang#hasSlang> ?slang ." +
      "\n?antonym <http://lmiguelmh/slang#isAntonymOf> ?word ." +
      "\n?antonym <http://lmiguelmh/slang#hasSlang> ?antonym_slang ." +
      "\n} " +
      "";

  public static final String SELECT_SLANG_MEANING = "" +
      "\nSELECT *" +
      "\nWHERE {" +
      "\n?slang <http://lmiguelmh/slang#value> \"cana\" ." +
      "\n?word <http://lmiguelmh/slang#hasSlang> ?slang ." +
      "\n?word <http://lmiguelmh/slang#meaning> ?meaning ." +
      "\n} " +
      "";

}
