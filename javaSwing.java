import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TutoringTrackingSystemGUI {

    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("CS Tutoring Tracking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Background panel with an overridden paintComponent method
        JPanel backgroundPanel = new JPanel() {
            private Image backgroundImage = new ImageIcon(getClass().getResource("/lol.png")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    System.out.println("Background image not found!");
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Main panel with grid layout
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false); // Make mainPanel transparent to show background
        mainPanel.setLayout(new GridLayout(9, 2, 10, 10)); // Rows, Columns, Hgap, Vgap

        // Labels and Text Fields for Student Information
        JLabel studentIdLabel = new JLabel("Student Win Number:");
        JTextField studentIdField = new JTextField();

        JLabel nameLabel = new JLabel("Student Name:");
        JTextField nameField = new JTextField();

        JLabel tutorLabel = new JLabel("Tutor:");
        String[] tutorOptions = {"Drew", "Pra"};
        JComboBox<String> tutorDropdown = new JComboBox<>(tutorOptions);

        JLabel topicLabel = new JLabel("Tutoring Topic:");
        JTextField topicField = new JTextField();

        JLabel classLabel = new JLabel("Class:");
        String[] classOptions = {
                "CM 105", "CM 111", "CM 130", "CM 203", "CM 231",
                "CM 245", "CM 261", "CM 290", "CM 303", "CM 307",
                "CM 322", "CM 331", "CM 333"
        };
        JComboBox<String> classDropdown = new JComboBox<>(classOptions);

        JLabel reasonLabel = new JLabel("Reason for Visiting:");
        JTextField reasonField = new JTextField();

        // Add Visit Button and View Visit History
        JButton addVisitButton = new JButton("Add Visit");
        JButton viewHistoryButton = new JButton("View Visit History");

        JButton addEditButton = new JButton("Edit");
        JButton addImportButton = new JButton("Import");

        // Help button with '?' icon
        JButton helpButton = new JButton("?");
        helpButton.setToolTipText("Click for help"); // Hover message
        helpButton.setPreferredSize(new Dimension(20, 20)); // Make button smaller
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame,
                        "This is the CS Tutoring Tracking System.\n" +
                                "- Fill in the student information fields.\n" +
                                "- Select the tutor and class.\n" +
                                "- Add the reason for the visit and click 'Add Visit'.\n" +
                                "- Use 'View Visit History' to see previous records.",
                        "Help",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Area for viewing saved visit logs
        JTextArea visitLogsArea = new JTextArea(8, 40);
        visitLogsArea.setEditable(false); // Makes the area read-only
        JScrollPane scrollPane = new JScrollPane(visitLogsArea);

        // Use GridBagLayout for button panels to allow resizing
        JPanel firstRowButtonPanel = new JPanel(new GridBagLayout()); // First row: Add Visit and View History
        firstRowButtonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;  // Make buttons stretch horizontally
        gbc.gridx = 0;
        gbc.weightx = 1.0; // Allow buttons to resize proportionally
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER; // Center the buttons
        firstRowButtonPanel.add(addVisitButton, gbc);

        gbc.gridx = 1;
        firstRowButtonPanel.add(viewHistoryButton, gbc);

        JPanel secondRowButtonPanel = new JPanel(new GridBagLayout()); // Second row: Edit, Import, Help
        secondRowButtonPanel.setOpaque(false);
        gbc.gridx = 0;
        secondRowButtonPanel.add(addEditButton, gbc);

        gbc.gridx = 1;
        secondRowButtonPanel.add(addImportButton, gbc);

        gbc.gridx = 2;
        secondRowButtonPanel.add(helpButton, gbc);

        // Adding components to the main panel
        mainPanel.add(studentIdLabel);
        mainPanel.add(studentIdField);
        mainPanel.add(nameLabel);
        mainPanel.add(nameField);
        mainPanel.add(tutorLabel);
        mainPanel.add(tutorDropdown);
        mainPanel.add(topicLabel);
        mainPanel.add(topicField);
        mainPanel.add(classLabel);
        mainPanel.add(classDropdown);
        mainPanel.add(reasonLabel);
        mainPanel.add(reasonField);
        mainPanel.add(firstRowButtonPanel);
        mainPanel.add(new JLabel()); // Blank label for spacing/alignment
        mainPanel.add(secondRowButtonPanel);

        // Add the background panel to the frame
        backgroundPanel.add(mainPanel, BorderLayout.CENTER);
        frame.add(backgroundPanel);

        // Show the frame
        frame.setVisible(true);

        // Action for the "View Visit History" button
        viewHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a new window to show visit history
                JFrame historyFrame = new JFrame("Visit History");
                historyFrame.setSize(500, 300);
                historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                // Panel to hold visit history content
                JPanel historyPanel = new JPanel(new BorderLayout());
                historyPanel.add(scrollPane, BorderLayout.CENTER);

                // Add the history panel to the new window
                historyFrame.add(historyPanel);
                historyFrame.setVisible(true);
            }
        });
    }
}
