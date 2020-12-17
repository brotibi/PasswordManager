package main.java.passwordmanager.view;

import main.java.passwordmanager.PasswordGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/** Simple swing example from online */
public class ExampleView implements ActionListener  {
    private JFrame frame;
    private JTextField filenameText;
    private JPasswordField passwordText;
    private JPanel cpanel;

    ArrayList<String> tags;
    private JList<String> tagList;
    private DefaultListModel<String> tagListModel;

    private final String CHOOSE_CMD = "choose";
    private final String OPEN_CMD = "open";
    private final String NEW_CMD = "new";
    private final String BACK_CMD = "back";
    private final String SAVE_CMD = "save";
    private final String DELETE_CMD = "delete";
    private final String EDIT_CMD = "edit";

    public static void main(String[] args) {
        ExampleView sw = new ExampleView();
        sw.run();
    }

    public ExampleView() {
        this.frame = new JFrame("Password Manager");
        this.frame.setSize(500, 200);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.cpanel = new JPanel(new CardLayout());
        this.frame.add(cpanel);
        addLoginPanel(cpanel);
        addPasswordsPanel(cpanel);
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
        filenameLabel.setBounds(10,20, labelwidth, labelheight);
        panel.add(filenameLabel);

        this.filenameText = new JTextField(20);
        this.filenameText.setBounds(labelwidth + 20,20,labelwidth, labelheight);
        panel.add(this.filenameText);

        JButton fileChooserButton = new JButton("Choose");
        fileChooserButton.setBounds(labelwidth * 2 + 30, 20, 80, labelheight);
        fileChooserButton.addActionListener(this);
        fileChooserButton.setActionCommand(CHOOSE_CMD);
        panel.add(fileChooserButton);

        // Create label and text box for entering password
        JLabel passwordLabel = new JLabel("Master password");
        passwordLabel.setBounds(10,50, labelwidth, labelheight);
        panel.add(passwordLabel);

        // JPasswordField hides the text you're entering
        this.passwordText = new JPasswordField(20);
        this.passwordText.setBounds(labelwidth + 20,50, labelwidth, labelheight);
        panel.add(this.passwordText);

        // Creating login button
        JButton goButton = new JButton("Open");
        goButton.setBounds(10, 80, 80,  labelheight);
        goButton.addActionListener(this);
        goButton.setActionCommand(OPEN_CMD);
        panel.add(goButton, BorderLayout.SOUTH);

        cpanel.add(panel);
    }

    private void addPasswordsPanel(JPanel cpanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        ///////////////// top panel with navigation buttons
        // buttons for creating new entries and returning to the first screen
        JButton backButton = makeButton("Back", BACK_CMD);
        JButton saveButton = makeButton("Save", SAVE_CMD);
        JButton addEntryButton = makeButton("New Entry", NEW_CMD);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(backButton, BorderLayout.LINE_START);
        topPanel.add(saveButton, BorderLayout.CENTER);
        topPanel.add(addEntryButton, BorderLayout.LINE_END);
        panel.add(topPanel, BorderLayout.PAGE_START);

        ///////////////////// list of tags
        this.tagListModel = new DefaultListModel<>();
        this.tagList = new JList<>(tagListModel);
        DefaultListCellRenderer renderer = (DefaultListCellRenderer)tagList.getCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);

        String[] tagsarr = {"gmail", "paypal"};
        this.tags = new ArrayList<>(Arrays.asList(tagsarr));
        tagListModel.addAll(tags);

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

    @Override
    public void actionPerformed(ActionEvent e) {
        /* Callbacks for when buttons are pressed */

        switch (e.getActionCommand()) {
            // Callback for pressing the button that says "Choose"
            case CHOOSE_CMD:
                JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(this.cpanel);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    this.filenameText.setText(file.getAbsolutePath());
                }
                break;

            // Callback for pressing the button that says "Open"
            case OPEN_CMD:
                // If we guess the password right, then move to the next panel
                if (String.valueOf(this.passwordText.getPassword()).equals("password")) {
                    ((CardLayout)this.cpanel.getLayout()).next(this.cpanel);
                }

                // Here's how we get the values of the text boxes
                System.out.println(this.filenameText.getText());
                System.out.println(String.valueOf(this.passwordText.getPassword()));
                break;

            // Callback for pressing the button that says "Back"
            case BACK_CMD:
                // return to first panel where we choose a filename and enter a password
                ((CardLayout)this.cpanel.getLayout()).first(this.cpanel);
                break;

            // Callback for pressing the button that says "New Entry"
            case NEW_CMD:
                // TODO bring up a new GUI that allows us to add new entries to the table
                break;

            case SAVE_CMD:
                // TODO write to the database
                break;

            case DELETE_CMD:
                int idx = this.tagList.getSelectedIndex();
                if (idx != -1) {
                    this.tags.remove(idx);
                    this.tagListModel.removeElementAt(idx);
                }
                break;

            case EDIT_CMD:
                // TODO bring up edit/view GUI
                break;
        }
    }
}
