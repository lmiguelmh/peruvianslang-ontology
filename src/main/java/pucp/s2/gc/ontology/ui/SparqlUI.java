package pucp.s2.gc.ontology.ui;

import org.apache.commons.io.Charsets;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Derivation;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import pucp.s2.gc.lucene.Indexer;
import pucp.s2.gc.lucene.Searcher;
import pucp.s2.gc.ontology.SparqlQuery;
import pucp.s2.gc.ontology.examples.App2;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Iterator;

import static pucp.s2.gc.ontology.examples.App2.query;
import static pucp.s2.gc.ontology.examples.App2.readModelFromFactory;

public class SparqlUI extends JDialog implements HyperlinkListener {
  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JTextField owlFileTxt;
  private JButton owlLoadBtn;
  private JTextArea queryTxt;
  private JTextArea resultTxt;
  private JButton queryBtn;
  private JRadioButton query1RadioButton;
  private JRadioButton query2RadioButton;
  private JRadioButton query3RadioButton;
  private JRadioButton query4RadioButton;
  private JRadioButton query5RadioButton;
  private JCheckBox withInferencesCheckBox;
  private JRadioButton query6RadioButton;
  private JRadioButton query7RadioButton;
  private JButton loadFromFileButton;
  private JTabbedPane tabbedPane1;
  private JTextArea explainTxt;
  private JTextField subjectTxt;
  private JTextField propertyTxt;
  private JTextField objectTxt;
  private JButton explainBtn;
  private JCheckBox explainInferencesChk;
  private JButton indexBtn;
  private JTextField corpusPathTxt;
  private JEditorPane luceneEditorPane;
  private JButton resolveBtn;
  private JTextArea oneColumnQueryTxt;
  private JTextField luceneQueryTxt;
  private JButton searchBtn;

  private Model model;

  public SparqlUI() {
    setContentPane(contentPane);
    setModal(true);
    setTitle("PUCP - Gestión del Conocimiento");
    getRootPane().setDefaultButton(buttonOK);
    luceneEditorPane.setText("<h1 color=\"black\" align=\"center\">Spanish Slang</h1><br/>\n" +
        "<br/>\n" +
        "<p>List of results:</p>\n" +
        "<br/>\n" );

    final HTMLDocument htmlDoc = (HTMLDocument) luceneEditorPane.getDocument();
    final HTMLEditorKit kit = (HTMLEditorKit) luceneEditorPane.getEditorKit();

      /*
      try {
          kit.insertHTML(htmlDoc, htmlDoc.getLength(), "<b>Hello</b>", 0, 0, null);
          kit.insertHTML(htmlDoc, htmlDoc.getLength(), "World", 0, 0, null);
      } catch (BadLocationException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      }

      Element[] roots = htmlDoc.getRootElements(); // #0 is the HTML element, #1 the bidi-root
      Element ul = null;
      for( int i = 0; i < roots[0].getElementCount(); i++ ) {
          Element element = roots[0].getElement( i );
          System.out.println("Element: "+element.getName()+" - "+element.toString());
          if( element.getAttributes().getAttribute( StyleConstants.NameAttribute ) == HTML.Tag.UL ) {
              ul = element;
              break;
          }
      }

      try {
          htmlDoc.insertAfterStart( ul, "<font>text</font>" );
          htmlDoc.insertBeforeEnd( htmlDoc.getElement("body"), "<span>Lalala</span>" );
      } catch (BadLocationException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      }
      */

    try {
      //luceneEditorPane.setText("<ul><li><a href=\""+new File("d:\\eula.1028.txt").toURI().toURL()+"\">File link</a></li></
      luceneEditorPane.setText("<ul><li><a href=\""+new File("corpus/slang/latear_http%253A%252F%252Felcomercio.pe%252Fluces%252Fmusica%252Fvoz-propia-relato-coral-banda-culto-noticia-1932383.txt").toURI().toURL()+"\">File link</a></li></ul>");

      //System.out.println("Doc web: "+luceneEditorPane.getDocument().toString());
      //System.out.println("Html doc: "+((HTMLDocument) luceneEditorPane.getDocument()).getElement("ul").getName());
      ((HTMLDocument) luceneEditorPane.getDocument()).insertBeforeEnd(((HTMLDocument) luceneEditorPane.getDocument()).getElement("ul"),"<li><a href=\\\"\"+new File(\"d:\\\\eula.1028.txt\").toURI().toURL()+\"\\\">File link</a></li>");
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (BadLocationException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onOK();
      }
    });

    buttonCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onCancel();
      }
    });

// call onCancel() when cross is clicked
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onCancel();
      }
    });

// call onCancel() on ESCAPE
    contentPane.registerKeyboardAction(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onCancel();
      }
    }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    owlLoadBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          InputStream slangIs = App2.class.getClassLoader().getResourceAsStream(owlFileTxt.getText());
//          FileInputStream slangIs = new FileInputStream(new File(owlFileTxt.getText()));
          model = readModelFromFactory(slangIs);
          showInfo("OWL file loaded!");

        } catch (Exception e1) {
          e1.printStackTrace();
          showError(e1.getMessage());
        }
      }
    });
    queryBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (model == null) {
          showError("Load the OWL file first!");
          return;
        }

        try {
          Model queryModel;
          if (withInferencesCheckBox.isSelected()) {
            Reasoner owlReasoner = ReasonerRegistry.getOWLReasoner();
            Reasoner wnReasoner = owlReasoner.bindSchema(model);
            wnReasoner.setDerivationLogging(true);
            queryModel = ModelFactory.createInfModel(wnReasoner, model);
          } else {
            queryModel = model;
          }

          String queryString = queryTxt.getText();
          String result = query(queryModel, queryString, "txt");
          resultTxt.setText(result);

        } catch (Exception e1) {
          resultTxt.setText("");
          e1.printStackTrace();
          showError(e1.getMessage());
        }
      }
    });
    query1RadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        queryTxt.setText(SparqlQuery.SELECT_ALL);
      }
    });
    query2RadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        queryTxt.setText(SparqlQuery.SELECT_SLANG_SYN);
      }
    });
    query3RadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        queryTxt.setText(SparqlQuery.SELECT_WORD_SYN);
      }
    });
    query4RadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        queryTxt.setText(SparqlQuery.SELECT_WORD_SYN_SLANG);
      }
    });
    query5RadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        queryTxt.setText(SparqlQuery.SELECT_WORD_ANT);
      }
    });
    query6RadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        queryTxt.setText(SparqlQuery.SELECT_WORD_ANT_SLANG);
      }
    });
    query7RadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        queryTxt.setText(SparqlQuery.SELECT_SLANG_MEANING);
      }
    });
    loadFromFileButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          File file;
          JFileChooser fileChooser = new JFileChooser();
          int returnVal = fileChooser.showOpenDialog(SparqlUI.this);
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
          } else {
            return;
          }

          FileInputStream slangIs = new FileInputStream(file);
          model = readModelFromFactory(slangIs);
          showInfo("OWL file loaded!");

        } catch (Exception e1) {
          e1.printStackTrace();
          showError(e1.getMessage());
        }
      }
    });
    explainBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (model == null) {
          showError("Load the OWL file first!");
          return;
        }

        try {
          Model queryModel;
          if (explainInferencesChk.isSelected()) {
            Reasoner owlReasoner = ReasonerRegistry.getOWLReasoner();
            Reasoner wnReasoner = owlReasoner.bindSchema(model);
            wnReasoner.setDerivationLogging(true);
            queryModel = ModelFactory.createInfModel(wnReasoner, model);
          } else {
            queryModel = model;
          }

          String result;
          String subjectString = subjectTxt.getText();
          Resource subject = queryModel.getResource(subjectString);
          if (!queryModel.containsResource(subject)) {
            throw new RuntimeException("subject doesn't exist in model");
          }

          String propertyString = propertyTxt.getText();
          Property property = queryModel.getProperty(propertyString);
          if (!queryModel.contains(subject, property)) {
            throw new RuntimeException("subject/property doesn't exist in model");
          }

          String objectString = objectTxt.getText();
          Resource object = queryModel.getResource(objectString);
          if (!queryModel.contains(subject, property, object)) {
            throw new RuntimeException("subject/property/object doesn't exist in model");
          }

          if (queryModel instanceof InfModel) {
            InfModel inf = (InfModel) queryModel;
            StringWriter sw = new StringWriter();
            PrintWriter out = new PrintWriter(sw);
            for (StmtIterator i = inf.listStatements(subject, property, object); i.hasNext(); ) {
              Statement s = i.nextStatement();
              for (Iterator id = inf.getDerivation(s); id.hasNext(); ) {
                Derivation deriv = (Derivation) id.next();
                deriv.printTrace(out, true);
              }
            }
            out.flush();
            sw.flush();
            result = sw.toString();

          } else {
            result = "---";
          }

          explainTxt.setText(result);

        } catch (Exception e1) {
          explainTxt.setText("");
          e1.printStackTrace();
          showError(e1.getMessage());
        }
      }
    });
    indexBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        Indexer indexer = null;
        try {
          String corpusPath = corpusPathTxt.getText();
          indexer = Indexer.getInstance(corpusPath);

          System.out.println(Arrays.toString(luceneEditorPane.getDocument().getRootElements()));
          File folder = new File(corpusPath);
          File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return name.toLowerCase().endsWith(".txt")
                  && !name.toLowerCase().endsWith("clase.txt"); //ignoremos clase
            }
          });

          StringBuilder sb = new StringBuilder();
          if (files == null || files.length == 0) {
            sb.append("0 indexed files");
          } else {
            for (File file : files) {
              //sb.append("indexing: " + file.toPath() + "\n");
//              try {
//                kit.insertHTML(htmlDoc, htmlDoc.getLength(), "<p>Indexing: <a href=\"" + file.toURI().toURL() + "\">"+file.getName()+"</a>" + "</p>", 0, 0, null);
//                //((HTMLDocument) luceneEditorPane.getDocument()).insertBeforeEnd(((HTMLDocument) luceneEditorPane.getDocument()).getElement("ul"),"<li>indexing: " + file.toPath() + "</li>");
//                //luceneEditorPane.getDocument().insertString(luceneEditorPane.getDocument().getLength(), "indexing: " + file.toPath() + "<br />", new SimpleAttributeSet());
//              } catch (BadLocationException e) {
//                e.printStackTrace();
//              }
              byte[] raw = Files.readAllBytes(file.toPath());
              indexer.indexar(file.toPath().toString(), new String(raw, Charsets.UTF_8));
//              sb.append(file.toPath() + " indexed...\n");
              try {
                System.out.println("<li>" + file.toPath() + " indexed..." + "</li>");
                kit.insertHTML(htmlDoc, htmlDoc.getLength(), "<p><a href=\"" + file.toURI().toURL() + "\">"+file.getName()+"</a> indexed..." + "</p>", 0, 0, null);
                //((HTMLDocument) luceneEditorPane.getDocument()).insertBeforeEnd(luceneEditorPane.getDocument().getRootElements()[0],"<li>" + file.toPath() + " indexed..." + "</li>");
                //luceneEditorPane.getDocument().insertString(luceneEditorPane.getDocument().getLength(), file.toPath() + " indexed..." + "<br />", new SimpleAttributeSet());
              } catch (BadLocationException e) {
                e.printStackTrace();
              }
            }
//            sb.append(files.length + " indexed files");
          }
          //luceneEditorPane.setText(sb.toString());

          //indexer.cerrarEscritor();
          //showInfo("Indexación completada correctamente.");
          JOptionPane.showMessageDialog(null, "Indexación completada correctamente.", "Resultado de la Indexación", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
          showInfo("Error al realizar la indexación. \n"+e.getMessage());
          //JOptionPane.showMessageDialog(null, "Error al realizar la indexación. \n"+e.getMessage(), "Resultado de la Indexación", JOptionPane.INFORMATION_MESSAGE);
          e.printStackTrace();

        } finally {
          if (indexer != null) {
            try {
              indexer.cerrarEscritor();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    });
    resolveBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (model == null) {
          showError("Load the OWL file first!");
          return;
        }

        try {

          Model queryModel;
          Reasoner owlReasoner = ReasonerRegistry.getOWLReasoner();
          Reasoner wnReasoner = owlReasoner.bindSchema(model);
          wnReasoner.setDerivationLogging(true);
          queryModel = ModelFactory.createInfModel(wnReasoner, model);

          String queryString = oneColumnQueryTxt.getText();
          String result = query(queryModel, queryString, "csv");

          String[] tokens = result.split("\n");
          String words = "";
          for (int i = 0; i < tokens.length; i++) {
            if (i != 0 && !tokens[i].isEmpty()) {
              words += tokens[i] + ",";
            }
          }
          luceneQueryTxt.setText(words);

        } catch (Exception e1) {
          luceneQueryTxt.setText("");
          e1.printStackTrace();
          showError(e1.getMessage());
        }
      }
    });
    searchBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {

        try {
          String corpusPath = corpusPathTxt.getText();
          String[] words = luceneQueryTxt.getText().split(",");
          Searcher searcher = Searcher.getInstance(corpusPath);

          luceneEditorPane.setText("");
          luceneEditorPane.setText("<h1 color=\"black\" align=\"center\">Spanish Slang</h1><br/>\n" +
              "<br/>\n" +
              "<p>List of results:</p>\n" +
              "<br/>\n" );

          StringBuilder sb = new StringBuilder();
          for (String word : words) {
            sb.append(word.toUpperCase()).append("\n");
            try {
              kit.insertHTML(htmlDoc, htmlDoc.getLength(), "<p>"+word.toUpperCase()+"</p>", 0, 0, null);
              //((HTMLDocument) luceneEditorPane.getDocument()).insertBeforeEnd(((HTMLDocument) luceneEditorPane.getDocument()).getElement("ul"),"<li>" + word.toUpperCase() + "</li>");
              //luceneEditorPane.getDocument().insertString(luceneEditorPane.getDocument().getLength(), word.toUpperCase() + "<br />", new SimpleAttributeSet());
            } catch (BadLocationException e) {
              e.printStackTrace();
            }

            if (!word.isEmpty()) {
              ScoreDoc[] docs = searcher.buscar(word);
              sb.append(searcher.visualizarDocumentos(docs)).append("\n");

              try {
                kit.insertHTML(htmlDoc, htmlDoc.getLength(), searcher.visualizarDocumentos2(docs), 0, 0, null);
                //((HTMLDocument) luceneEditorPane.getDocument()).insertBeforeEnd(((HTMLDocument) luceneEditorPane.getDocument()).getElement("ul"),"<li>" + searcher.visualizarDocumentos(docs) + "</li>");
                //luceneEditorPane.getDocument().insertString(luceneEditorPane.getDocument().getLength(), searcher.visualizarDocumentos(docs) + "<br />", new SimpleAttributeSet());
              } catch (BadLocationException e) {
                e.printStackTrace();
              }
            }
          }

//          luceneEditorPane.setText(sb.toString());

          //searcher.cerrarBuscador();

        } catch (IOException | ParseException e) {
          e.printStackTrace();
        }
      }
    });

    luceneEditorPane.addHyperlinkListener(this);

    /*luceneEditorPane.addHyperlinkListener(new HyperlinkListener() {
      @Override
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
          // Do something with e.getURL() here
          if(Desktop.isDesktopSupported()) {
            try {
              System.out.println(e.toString());
              System.out.println("Opening: "+e.getURL().toURI());
              Desktop.getDesktop().browse(e.getURL().toURI());
            } catch (IOException e1) {
              e1.printStackTrace();
            } catch (URISyntaxException e1) {
              e1.printStackTrace();
            }
          }
        }
      }
    });*/
  }

  @Override
  public void hyperlinkUpdate(HyperlinkEvent e) {
    if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      // Do something with e.getURL() here
      if(Desktop.isDesktopSupported()) {
        try {
          System.out.println(e.toString());
          System.out.println(e.getURL());
          System.out.println("Opening: "+e.getURL().toURI());
          Desktop.getDesktop().browse(e.getURL().toURI());
        } catch (IOException e1) {
          e1.printStackTrace();
        } catch (URISyntaxException e1) {
          e1.printStackTrace();
        }
      }
    }
  }

  private void showInfo(String message) {
    JOptionPane.showOptionDialog(this, message, "Info", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
  }

  private void showError(String message) {
    JOptionPane.showOptionDialog(this, message, "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
  }

  private void onOK() {
// add your code here
    dispose();
  }

  private void onCancel() {
// add your code here if necessary
    dispose();
  }

  public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
    System.setProperty("file.encoding", "UTF-8");
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    SparqlUI dialog = new SparqlUI();
    dialog.pack();
    dialog.setSize(800, 600);
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    System.exit(0);
  }

  {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout(0, 0));
    contentPane.setMinimumSize(new Dimension(800, 600));
    contentPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null));
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    contentPane.add(panel1, BorderLayout.SOUTH);
    buttonOK = new JButton();
    buttonOK.setText("OK");
    panel1.add(buttonOK);
    buttonCancel = new JButton();
    buttonCancel.setText("Cancel");
    panel1.add(buttonCancel);
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new BorderLayout(0, 0));
    contentPane.add(panel2, BorderLayout.NORTH);
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel2.add(panel3, BorderLayout.SOUTH);
    final JLabel label1 = new JLabel();
    label1.setText("OWL file");
    panel3.add(label1);
    owlFileTxt = new JTextField();
    owlFileTxt.setColumns(24);
    owlFileTxt.setFont(new Font("Consolas", owlFileTxt.getFont().getStyle(), owlFileTxt.getFont().getSize()));
    owlFileTxt.setText("slang.owl");
    panel3.add(owlFileTxt);
    owlLoadBtn = new JButton();
    owlLoadBtn.setText("Load");
    panel3.add(owlLoadBtn);
    final JLabel label2 = new JLabel();
    label2.setText("or");
    panel3.add(label2);
    loadFromFileButton = new JButton();
    loadFromFileButton.setText("Load from file...");
    panel3.add(loadFromFileButton);
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel2.add(panel4, BorderLayout.CENTER);
    final JLabel label3 = new JLabel();
    label3.setFont(new Font(label3.getFont().getName(), Font.BOLD, 14));
    label3.setText("PUCP - Gestión del Conocimiento");
    panel4.add(label3);
    tabbedPane1 = new JTabbedPane();
    contentPane.add(tabbedPane1, BorderLayout.CENTER);
    final JPanel panel5 = new JPanel();
    panel5.setLayout(new BorderLayout(0, 0));
    tabbedPane1.addTab("  SPARQL querying  ", panel5);
    final JPanel panel6 = new JPanel();
    panel6.setLayout(new BorderLayout(0, 0));
    panel5.add(panel6, BorderLayout.NORTH);
    final JPanel panel7 = new JPanel();
    panel7.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel6.add(panel7, BorderLayout.NORTH);
    query1RadioButton = new JRadioButton();
    query1RadioButton.setText("query1");
    panel7.add(query1RadioButton);
    query2RadioButton = new JRadioButton();
    query2RadioButton.setText("query2");
    panel7.add(query2RadioButton);
    query3RadioButton = new JRadioButton();
    query3RadioButton.setText("query3");
    panel7.add(query3RadioButton);
    query4RadioButton = new JRadioButton();
    query4RadioButton.setText("query4");
    panel7.add(query4RadioButton);
    query5RadioButton = new JRadioButton();
    query5RadioButton.setText("query5");
    panel7.add(query5RadioButton);
    query6RadioButton = new JRadioButton();
    query6RadioButton.setText("query6");
    panel7.add(query6RadioButton);
    query7RadioButton = new JRadioButton();
    query7RadioButton.setText("query7");
    panel7.add(query7RadioButton);
    final JPanel panel8 = new JPanel();
    panel8.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel6.add(panel8, BorderLayout.CENTER);
    final JLabel label4 = new JLabel();
    label4.setText("Query string");
    panel8.add(label4);
    final JPanel panel9 = new JPanel();
    panel9.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel8.add(panel9);
    final JScrollPane scrollPane1 = new JScrollPane();
    panel9.add(scrollPane1);
    queryTxt = new JTextArea();
    queryTxt.setColumns(75);
    queryTxt.setFont(new Font("Consolas", queryTxt.getFont().getStyle(), queryTxt.getFont().getSize()));
    queryTxt.setRows(6);
    queryTxt.setText(" ");
    scrollPane1.setViewportView(queryTxt);
    final JPanel panel10 = new JPanel();
    panel10.setLayout(new BorderLayout(0, 0));
    panel8.add(panel10);
    queryBtn = new JButton();
    queryBtn.setText("Query");
    panel10.add(queryBtn, BorderLayout.CENTER);
    withInferencesCheckBox = new JCheckBox();
    withInferencesCheckBox.setSelected(true);
    withInferencesCheckBox.setText("with inferences");
    panel10.add(withInferencesCheckBox, BorderLayout.SOUTH);
    final JPanel panel11 = new JPanel();
    panel11.setLayout(new BorderLayout(0, 0));
    panel5.add(panel11, BorderLayout.CENTER);
    panel11.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null));
    final JScrollPane scrollPane2 = new JScrollPane();
    panel11.add(scrollPane2, BorderLayout.CENTER);
    resultTxt = new JTextArea();
    resultTxt.setFont(new Font("Consolas", resultTxt.getFont().getStyle(), resultTxt.getFont().getSize()));
    resultTxt.setRows(10);
    scrollPane2.setViewportView(resultTxt);
    final JPanel panel12 = new JPanel();
    panel12.setLayout(new BorderLayout(0, 0));
    tabbedPane1.addTab("  Derivation explainer  ", panel12);
    final JPanel panel13 = new JPanel();
    panel13.setLayout(new BorderLayout(0, 0));
    panel12.add(panel13, BorderLayout.NORTH);
    final JPanel panel14 = new JPanel();
    panel14.setLayout(new BorderLayout(0, 0));
    panel13.add(panel14, BorderLayout.NORTH);
    final JPanel panel15 = new JPanel();
    panel15.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel13.add(panel15, BorderLayout.CENTER);
    final JPanel panel16 = new JPanel();
    panel16.setLayout(new BorderLayout(0, 0));
    panel15.add(panel16);
    final JPanel panel17 = new JPanel();
    panel17.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel16.add(panel17, BorderLayout.NORTH);
    final JLabel label5 = new JLabel();
    label5.setText("Subject");
    panel17.add(label5);
    final JPanel panel18 = new JPanel();
    panel18.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel16.add(panel18, BorderLayout.CENTER);
    final JLabel label6 = new JLabel();
    label6.setText("Property");
    panel18.add(label6);
    final JPanel panel19 = new JPanel();
    panel19.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel16.add(panel19, BorderLayout.SOUTH);
    final JLabel label7 = new JLabel();
    label7.setText("Object");
    panel19.add(label7);
    final JPanel panel20 = new JPanel();
    panel20.setLayout(new BorderLayout(0, 0));
    panel15.add(panel20);
    final JPanel panel21 = new JPanel();
    panel21.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel20.add(panel21, BorderLayout.NORTH);
    subjectTxt = new JTextField();
    subjectTxt.setColumns(40);
    subjectTxt.setText("http://pucp.s2.gc.ontology/slang#molesto");
    panel21.add(subjectTxt);
    final JPanel panel22 = new JPanel();
    panel22.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel20.add(panel22, BorderLayout.CENTER);
    propertyTxt = new JTextField();
    propertyTxt.setColumns(40);
    propertyTxt.setText("http://pucp.s2.gc.ontology/slang#isSynonymOf");
    panel22.add(propertyTxt);
    final JPanel panel23 = new JPanel();
    panel23.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel20.add(panel23, BorderLayout.SOUTH);
    objectTxt = new JTextField();
    objectTxt.setColumns(40);
    objectTxt.setText("http://pucp.s2.gc.ontology/slang#enojado");
    panel23.add(objectTxt);
    final JPanel panel24 = new JPanel();
    panel24.setLayout(new BorderLayout(0, 0));
    panel15.add(panel24);
    explainBtn = new JButton();
    explainBtn.setText("Explain");
    panel24.add(explainBtn, BorderLayout.CENTER);
    explainInferencesChk = new JCheckBox();
    explainInferencesChk.setSelected(true);
    explainInferencesChk.setText("with inferences");
    panel24.add(explainInferencesChk, BorderLayout.SOUTH);
    final JPanel panel25 = new JPanel();
    panel25.setLayout(new BorderLayout(0, 0));
    panel12.add(panel25, BorderLayout.CENTER);
    panel25.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null));
    final JScrollPane scrollPane3 = new JScrollPane();
    panel25.add(scrollPane3, BorderLayout.CENTER);
    explainTxt = new JTextArea();
    scrollPane3.setViewportView(explainTxt);
    final JPanel panel26 = new JPanel();
    panel26.setLayout(new BorderLayout(0, 0));
    tabbedPane1.addTab("  Lucene Indexer&Searcher  ", panel26);
    final JPanel panel27 = new JPanel();
    panel27.setLayout(new BorderLayout(0, 0));
    panel26.add(panel27, BorderLayout.NORTH);
    final JPanel panel28 = new JPanel();
    panel28.setLayout(new BorderLayout(0, 0));
    panel27.add(panel28, BorderLayout.NORTH);
    final JPanel panel29 = new JPanel();
    panel29.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
    panel27.add(panel29, BorderLayout.CENTER);
    final JPanel panel30 = new JPanel();
    panel30.setLayout(new BorderLayout(5, 30));
    panel29.add(panel30);
    final JPanel panel31 = new JPanel();
    panel31.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel30.add(panel31, BorderLayout.NORTH);
    final JLabel label8 = new JLabel();
    label8.setText("Corpus path");
    panel31.add(label8);
    final JPanel panel32 = new JPanel();
    panel32.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel30.add(panel32, BorderLayout.CENTER);
    final JLabel label9 = new JLabel();
    label9.setText("One col query");
    panel32.add(label9);
    final JPanel panel33 = new JPanel();
    panel33.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel30.add(panel33, BorderLayout.SOUTH);
    final JLabel label10 = new JLabel();
    label10.setText("Words");
    panel33.add(label10);
    final JPanel panel34 = new JPanel();
    panel34.setLayout(new BorderLayout(0, 0));
    panel29.add(panel34);
    final JPanel panel35 = new JPanel();
    panel35.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel34.add(panel35, BorderLayout.NORTH);
    corpusPathTxt = new JTextField();
    corpusPathTxt.setColumns(40);
    corpusPathTxt.setText("./corpus/slang");
    panel35.add(corpusPathTxt);
    indexBtn = new JButton();
    indexBtn.setText("Index");
    panel35.add(indexBtn);
    final JPanel panel36 = new JPanel();
    panel36.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel34.add(panel36, BorderLayout.CENTER);
    final JScrollPane scrollPane4 = new JScrollPane();
    panel36.add(scrollPane4);
    oneColumnQueryTxt = new JTextArea();
    oneColumnQueryTxt.setColumns(45);
    oneColumnQueryTxt.setFont(new Font("Consolas", oneColumnQueryTxt.getFont().getStyle(), oneColumnQueryTxt.getFont().getSize()));
    oneColumnQueryTxt.setRows(3);
    oneColumnQueryTxt.setText("SELECT ?synonym_word \nWHERE {\n?word <http://pucp.s2.gc.ontology/slang#value> \"enojado\" .\n?synonym <http://pucp.s2.gc.ontology/slang#isSynonymOf> ?word .\n?synonym <http://pucp.s2.gc.ontology/slang#value> ?synonym_word .\n}");
    scrollPane4.setViewportView(oneColumnQueryTxt);
    resolveBtn = new JButton();
    resolveBtn.setText("Resolve");
    panel36.add(resolveBtn);
    final JPanel panel37 = new JPanel();
    panel37.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel34.add(panel37, BorderLayout.SOUTH);
    luceneQueryTxt = new JTextField();
    luceneQueryTxt.setColumns(40);
    luceneQueryTxt.setText("");
    panel37.add(luceneQueryTxt);
    searchBtn = new JButton();
    searchBtn.setText("Search");
    panel37.add(searchBtn);
    final JPanel panel38 = new JPanel();
    panel38.setLayout(new BorderLayout(0, 0));
    panel29.add(panel38);
    final JPanel panel39 = new JPanel();
    panel39.setLayout(new BorderLayout(0, 0));
    panel26.add(panel39, BorderLayout.CENTER);
    panel39.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null));
    final JScrollPane scrollPane5 = new JScrollPane();
    panel39.add(scrollPane5, BorderLayout.CENTER);
    luceneEditorPane = new JEditorPane();
    scrollPane5.setViewportView(luceneEditorPane);
    ButtonGroup buttonGroup;
    buttonGroup = new ButtonGroup();
    buttonGroup.add(query4RadioButton);
    buttonGroup.add(query4RadioButton);
    buttonGroup.add(query5RadioButton);
    buttonGroup.add(query1RadioButton);
    buttonGroup.add(query3RadioButton);
    buttonGroup.add(query2RadioButton);
    buttonGroup.add(query6RadioButton);
    buttonGroup.add(query7RadioButton);
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return contentPane;
  }
}
