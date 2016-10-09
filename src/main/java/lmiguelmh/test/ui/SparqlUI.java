package lmiguelmh.test.ui;

import lmiguelmh.test.SparqlQuery;
import lmiguelmh.test.examples.App2;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static lmiguelmh.test.examples.App2.query;
import static lmiguelmh.test.examples.App2.readModelFromFactory;

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
            queryModel = ModelFactory.createInfModel(wnReasoner, model);
          } else {
            queryModel = model;
          }

          String queryString = queryTxt.getText();
          String result = query(queryModel, queryString, "txt");
          resultTxt.setText(result);

        } catch (Exception e1) {
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
    panel1.setLayout(new BorderLayout(0, 0));
    contentPane.add(panel1, BorderLayout.CENTER);
    panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new BorderLayout(0, 0));
    panel1.add(panel2, BorderLayout.NORTH);
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel2.add(panel3, BorderLayout.NORTH);
    query1RadioButton = new JRadioButton();
    query1RadioButton.setText("query1");
    panel3.add(query1RadioButton);
    query2RadioButton = new JRadioButton();
    query2RadioButton.setText("query2");
    panel3.add(query2RadioButton);
    query3RadioButton = new JRadioButton();
    query3RadioButton.setText("query3");
    panel3.add(query3RadioButton);
    query4RadioButton = new JRadioButton();
    query4RadioButton.setText("query4");
    panel3.add(query4RadioButton);
    query5RadioButton = new JRadioButton();
    query5RadioButton.setText("query5");
    panel3.add(query5RadioButton);
    query6RadioButton = new JRadioButton();
    query6RadioButton.setText("query6");
    panel3.add(query6RadioButton);
    query7RadioButton = new JRadioButton();
    query7RadioButton.setText("query7");
    panel3.add(query7RadioButton);
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel2.add(panel4, BorderLayout.CENTER);
    final JLabel label1 = new JLabel();
    label1.setText("Query string");
    panel4.add(label1);
    final JPanel panel5 = new JPanel();
    panel5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel4.add(panel5);
    final JScrollPane scrollPane1 = new JScrollPane();
    panel5.add(scrollPane1);
    queryTxt = new JTextArea();
    queryTxt.setColumns(75);
    queryTxt.setFont(new Font("Consolas", queryTxt.getFont().getStyle(), queryTxt.getFont().getSize()));
    queryTxt.setRows(6);
    queryTxt.setText(" ");
    scrollPane1.setViewportView(queryTxt);
    final JPanel panel6 = new JPanel();
    panel6.setLayout(new BorderLayout(0, 0));
    panel4.add(panel6);
    queryBtn = new JButton();
    queryBtn.setText("Query");
    panel6.add(queryBtn, BorderLayout.CENTER);
    withInferencesCheckBox = new JCheckBox();
    withInferencesCheckBox.setSelected(true);
    withInferencesCheckBox.setText("with inferences");
    panel6.add(withInferencesCheckBox, BorderLayout.SOUTH);
    final JPanel panel7 = new JPanel();
    panel7.setLayout(new BorderLayout(0, 0));
    panel1.add(panel7, BorderLayout.CENTER);
    panel7.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null));
    final JScrollPane scrollPane2 = new JScrollPane();
    panel7.add(scrollPane2, BorderLayout.CENTER);
    resultTxt = new JTextArea();
    resultTxt.setFont(new Font("Consolas", resultTxt.getFont().getStyle(), resultTxt.getFont().getSize()));
    resultTxt.setRows(10);
    scrollPane2.setViewportView(resultTxt);
    final JPanel panel8 = new JPanel();
    panel8.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    contentPane.add(panel8, BorderLayout.SOUTH);
    buttonOK = new JButton();
    buttonOK.setText("OK");
    panel8.add(buttonOK);
    buttonCancel = new JButton();
    buttonCancel.setText("Cancel");
    panel8.add(buttonCancel);
    final JPanel panel9 = new JPanel();
    panel9.setLayout(new BorderLayout(0, 0));
    contentPane.add(panel9, BorderLayout.NORTH);
    final JPanel panel10 = new JPanel();
    panel10.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel9.add(panel10, BorderLayout.SOUTH);
    final JLabel label2 = new JLabel();
    label2.setText("OWL file");
    panel10.add(label2);
    owlFileTxt = new JTextField();
    owlFileTxt.setColumns(24);
    owlFileTxt.setFont(new Font("Consolas", owlFileTxt.getFont().getStyle(), owlFileTxt.getFont().getSize()));
    owlFileTxt.setText("slang.owl");
    panel10.add(owlFileTxt);
    owlLoadBtn = new JButton();
    owlLoadBtn.setText("Load");
    panel10.add(owlLoadBtn);
    final JLabel label3 = new JLabel();
    label3.setText("or");
    panel10.add(label3);
    loadFromFileButton = new JButton();
    loadFromFileButton.setText("Load from file...");
    panel10.add(loadFromFileButton);
    final JPanel panel11 = new JPanel();
    panel11.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel9.add(panel11, BorderLayout.CENTER);
    final JLabel label4 = new JLabel();
    label4.setFont(new Font(label4.getFont().getName(), Font.BOLD, 14));
    label4.setText("SPARQL UI");
    panel11.add(label4);
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
