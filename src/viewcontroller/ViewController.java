package viewcontroller;

import lib.PasswordGenerator;
import lib.Utils;
import model.Model;
import model.TagPasswordPair;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * View and Controller for the password manager app
 **/
public class ViewController implements ActionListener {
    Model model;

    private JFrame frame;
    private JPanel cpanel;

    // First panel objects that need to be visible to callbacks
    private JTextField filenameText;
    private JPasswordField masterPasswordText;
    private JFileChooser fileChooser;

    // Second panel objects that need to be visible to callbacks
    private ArrayList<TagPasswordPair> tags;
    private JList<String> tagList;
    private DefaultListModel<String> tagListModel;

    // Third panel objects that need to be visible to callbacks
    private JTextField tagText;
    private JTextField passwordText;
    private JTextField lengthText;

    // Callback commands
    private final String CHOOSE_CMD = "choose";
    private final String OPEN_CMD = "open";
    private final String NEW_CMD = "new";
    private final String BACK_CMD = "back";
    private final String SAVE_ALL_CMD = "save_all";
    private final String DELETE_CMD = "delete";
    private final String EDIT_CMD = "edit";
    private final String GENERATE_CMD = "generate";
    private final String CANCEL_CMD = "cancel";
    private final String SAVE_ENTRY_CMD = "save_entry";

    // Global control signals
    private boolean isNewEntry = true;

    public static void main(String[] args) {
        ViewController sw = new ViewController();
        sw.run();
    }

    public ViewController() {
        this.model = new Model();

        this.frame = new JFrame("Password Manager");
        this.frame.setSize(500, 200);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.cpanel = new JPanel(new CardLayout());
        this.frame.add(cpanel);
        addLoginPanel(cpanel);
        addPasswordListPanel(cpanel);
        addViewEntryPanel(cpanel);
    }

    private void run() {
        // Setting the frame visibility to true
        this.frame.setVisible(true);
    }

    private JButton makeButton(String text, String actionCmd) {
        JButton out = new JButton(text);
        out.addActionListener(this);
        out.setActionCommand(actionCmd);
        return out;
    }

    private void addLoginPanel(JPanel cpanel) {
        JPanel panel = new JPanel();

        // no layout for this panel since it's just 2 labels, 2 text boxes, and 2 buttons
        // https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html
        panel.setLayout(null);

        int labelwidth = 165;
        int labelheight = 25;

        // Create label, text box, and button for choosing filename
        JLabel filenameLabel = new JLabel("Path to password database");
        filenameLabel.setBounds(10, 20, labelwidth, labelheight);
        panel.add(filenameLabel);

        this.filenameText = new JTextField(20);
        this.filenameText.setBounds(labelwidth + 20, 20, labelwidth, labelheight);
        panel.add(this.filenameText);

        JButton fileChooserButton = makeButton("Choose", CHOOSE_CMD);
        fileChooserButton.setBounds(labelwidth * 2 + 30, 20, 80, labelheight);
        panel.add(fileChooserButton);

        this.fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose *.pwdb file or else provide the name of one to create");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Password Database Files", "pwdb");
        fileChooser.setFileFilter(filter);

        // Create label and text box for entering password
        JLabel passwordLabel = new JLabel("Master password");
        passwordLabel.setBounds(10, 50, labelwidth, labelheight);
        panel.add(passwordLabel);

        // JPasswordField hides the text you're entering
        this.masterPasswordText = new JPasswordField(20);
        this.masterPasswordText.setBounds(labelwidth + 20, 50, labelwidth, labelheight);
        panel.add(this.masterPasswordText);

        // Creating login button
        JButton goButton = makeButton("Open", OPEN_CMD);
        goButton.setBounds(10, 80, 80, labelheight);
        panel.add(goButton);

        cpanel.add(panel);
    }

    private void showTagPasswordPairs() {
        this.tagListModel.removeAllElements();
        for (TagPasswordPair pair : this.tags) {
            tagListModel.addElement(pair.tag);
        }
    }

    private void addPasswordListPanel(JPanel cpanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        ///////////////// top panel with navigation buttons
        // buttons for creating new entries and returning to the first screen
        JButton backButton = makeButton("Back", BACK_CMD);
        JButton saveButton = makeButton("Save", SAVE_ALL_CMD);
        JButton addEntryButton = makeButton("New Entry", NEW_CMD);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(backButton, BorderLayout.LINE_START);
        topPanel.add(saveButton, BorderLayout.CENTER);
        topPanel.add(addEntryButton, BorderLayout.LINE_END);
        panel.add(topPanel, BorderLayout.PAGE_START);

        ///////////////////// list of tags
        this.tagListModel = new DefaultListModel<>();
        this.tagList = new JList<>(tagListModel);
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) tagList.getCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);

        tagList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tagList.setLayoutOrientation(JList.VERTICAL);
        JScrollPane scroll = new JScrollPane(tagList);
        panel.add(scroll, BorderLayout.CENTER);

        //////////////////////// bottom panel with delete/edit/view buttons
        JButton deleteButton = makeButton("Delete Selected", DELETE_CMD);
        JButton viewEditButton = makeButton("View/Edit Selected", EDIT_CMD);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(deleteButton, BorderLayout.LINE_START);
        bottomPanel.add(viewEditButton, BorderLayout.LINE_END);
        panel.add(bottomPanel, BorderLayout.PAGE_END);

        // Add the panel to the main panel
        cpanel.add(panel);
    }

    private void addViewEntryPanel(JPanel cpanel) {
        // same as login panel, manually set dimensions
        JPanel panel = new JPanel();
        panel.setLayout(null);

        int startX = 10;
        int xDiff = 20;
        int startY = 20;
        int yDiff = 30;
        int labelwidth = 80;
        int labelheight = 25;
        int textBoxWidth = 150;

        // Tags and text fields, like in login panel
        JLabel tagLabel = new JLabel("Tag");
        tagLabel.setBounds(startX, startY, labelwidth, labelheight);
        panel.add(tagLabel);

        this.tagText = new JTextField(20);
        this.tagText.setBounds(labelwidth + xDiff, startY, textBoxWidth, labelheight);
        panel.add(this.tagText);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(startX, startY + yDiff, labelwidth, labelheight);
        panel.add(passwordLabel);

        this.passwordText = new JTextField(20);
        this.passwordText.setBounds(labelwidth + xDiff, startY + yDiff, textBoxWidth, labelheight);
        panel.add(this.passwordText);

        // Buttons
        JButton generateButton = makeButton("Generate Strong Password", GENERATE_CMD);
        generateButton.setBounds(startX, startY + 2 * yDiff, 200, labelheight);
        panel.add(generateButton);

        JButton cancelButton = makeButton("Cancel", CANCEL_CMD);
        cancelButton.setBounds(startX, startY + 3 * yDiff, 80, labelheight);
        panel.add(cancelButton);

        JButton saveButton = makeButton("Save", SAVE_ENTRY_CMD);
        saveButton.setBounds(80 + xDiff, startY + 3 * yDiff, 80, labelheight);
        panel.add(saveButton);

        // password generator length
        JLabel lengthLabel = new JLabel("Password Length");
        lengthLabel.setBounds(startX + 200 + xDiff, startY + 2 * yDiff, 150, labelheight);
        panel.add(lengthLabel);

        this.lengthText = new JTextField(20);
        this.lengthText.setBounds(startX + 290 + 2 * xDiff, startY + 2 * yDiff, 50, labelheight);
        panel.add(lengthText);

        cpanel.add(panel);
    }

    private String getFileExtension(String filename) {
        if (filename.lastIndexOf(".") != -1 && filename.lastIndexOf(".") != 0) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    private void alert(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        /* Callbacks for when buttons are pressed */

        switch (actionEvent.getActionCommand()) {
            // Callback for pressing the button that says "Choose"
            case CHOOSE_CMD: {
                int returnVal = this.fileChooser.showOpenDialog(this.cpanel);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = this.fileChooser.getSelectedFile();
                    this.filenameText.setText(file.getAbsolutePath());
                }
                break;
            }

            // Callback for pressing the button that says "Open"
            case OPEN_CMD: {
                String filename = this.filenameText.getText();
                File file = new File(filename);

                if (!getFileExtension(filename).equalsIgnoreCase("pwdb")) {
                    alert("Please select a *.pwdb file or else provide the name of one to create");
                }
                else if (file.exists()) {
                    try {
                        this.model.read(filename, this.masterPasswordText.getPassword());
                        this.tags = model.getDatabaseInfo();
                        showTagPasswordPairs(); // uses this.tags
                        ((CardLayout) this.cpanel.getLayout()).next(this.cpanel);
                    } catch (ParseException e) {
                        alert("Error parsing database");
                    }
                }
                else {
                    // TODO open new file
                    this.model.initEmpty();
                    this.tags = new ArrayList<>();
                    showTagPasswordPairs(); // uses this.tags
                    ((CardLayout) this.cpanel.getLayout()).next(this.cpanel);
                }

                break;
            }

            // Callback for pressing the button that says "Back"
            case BACK_CMD:
                // return to first panel where we choose a filename and enter a password
                ((CardLayout) this.cpanel.getLayout()).first(this.cpanel);
                break;

            // Callback for pressing the button that says "New Entry"
            case NEW_CMD:
                // just show new pane and set global flag that tells us to add a new entry instead of editing one
                this.isNewEntry = true;
                this.tagText.setText("");
                this.passwordText.setText("");
                this.lengthText.setText(Integer.toString(PasswordGenerator.DEFAULT_LENGTH));
                ((CardLayout) this.cpanel.getLayout()).next(this.cpanel);
                break;

            // Callback for pressing the button that says "Save"
            case SAVE_ALL_CMD:
                this.model.setDatabaseInfo(this.tags);
                this.model.write(this.filenameText.getText(), this.masterPasswordText.getPassword());
                break;

            case DELETE_CMD: {
                int idx = this.tagList.getSelectedIndex();
                if (idx != -1) {
                    this.tags.remove(idx);
                    this.tagListModel.removeElementAt(idx);
                }
                break;
            }

            case EDIT_CMD: {
                this.isNewEntry = false;
                int idx = this.tagList.getSelectedIndex();
                if (idx != -1) {
                    String pwCipher = this.tags.get(idx).passwordCipher;
                    char[] decrypted = Utils.readableDecrypt(pwCipher, this.masterPasswordText.getPassword(), this.model.getSalt());

                    this.tagText.setText(this.tags.get(idx).tag);
                    this.passwordText.setText(String.valueOf(decrypted));
                    this.lengthText.setText(Integer.toString(PasswordGenerator.DEFAULT_LENGTH));

                    ((CardLayout) this.cpanel.getLayout()).next(this.cpanel);
                }
                break;
            }

            case GENERATE_CMD:
                try {
                    int len = Integer.parseInt(this.lengthText.getText());
                    if (len <= 0) {
                        alert("Invalid length");
                        break;
                    }

                    PasswordGenerator pwGen = new PasswordGenerator();
                    pwGen.length = len;
                    String strongPw = pwGen.generate();
                    this.passwordText.setText(strongPw);
                } catch (NumberFormatException e) {
                    alert("Invalid length");
                }
                break;

            case CANCEL_CMD:
                this.passwordText.setText(""); // clear contents
                ((CardLayout) this.cpanel.getLayout()).previous(this.cpanel);
                break;

            case SAVE_ENTRY_CMD: {
                String tagText = this.tagText.getText();
                String pwText = this.passwordText.getText();
                String encrypted = Utils.readableEncrypt(pwText.toCharArray(), this.masterPasswordText.getPassword(), this.model.getSalt());

                TagPasswordPair entry = new TagPasswordPair(tagText, encrypted);
                if (this.isNewEntry) {
                    this.tags.add(entry);
                } else {
                    int idx = this.tagList.getSelectedIndex();
                    this.tags.set(idx, entry);
                }

                showTagPasswordPairs();
                this.passwordText.setText(""); // clear contents
                ((CardLayout) this.cpanel.getLayout()).previous(this.cpanel);

                break;
            }
        }
    }
}
