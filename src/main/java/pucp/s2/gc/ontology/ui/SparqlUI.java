package pucp.s2.gc.ontology.ui;

import pucp.s2.gc.ontology.SparqlQuery;
import pucp.s2.gc.ontology.examples.App2;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Derivation;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Iterator;

import static pucp.s2.gc.ontology.examples.App2.query;
import static pucp.s2.gc.ontology.examples.App2.readModelFromFactory;

public class SparqlUI extends JDialog {
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

  private Model model;

  public SparqlUI() {
    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);

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
