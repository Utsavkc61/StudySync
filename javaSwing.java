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
        backgroundPanel.setLayout(new BorderLayout()); // Set layout for backgroundPanel

        // Main panel with grid layout
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false); // Make mainPanel transparent to show background
        mainPanel.setLayout(new GridLayout(9, 2, 10, 10)); // Rows, Columns, Hgap, Vgap

        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.setOpaque(false);
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        paddedPanel.add(mainPanel, BorderLayout.CENTER);

        // Labels and Text Fields for Student Information
        JLabel studentIdLabel = new JLabel("Student Win Number:");
        JTextField studentIdField = new JTextField();

        JLabel nameLabel = new JLabel("Student Name:");
        JTextField nameField = new JTextField();

        JLabel tutorLabel = new JLabel("Tutor:");
        String[] tutorOptions = {
            "Drew", "Pra"
        };
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
        /*
        // Reduce margins (remove extra padding inside buttons)
        Insets noMargin = new Insets(2, 5, 2, 5); // Top, Left, Bottom, Right
        addVisitButton.setMargin(noMargin);
        viewHistoryButton.setMargin(noMargin);
        addEditButton.setMargin(noMargin);
        addImportButton.setMargin(noMargin);

        // Set fixed dimensions
        Dimension buttonSize = new Dimension(70, 25); // Width: 70, Height: 25
        addVisitButton.setPreferredSize(buttonSize);
        viewHistoryButton.setPreferredSize(buttonSize);
        addEditButton.setPreferredSize(buttonSize);
        addImportButton.setPreferredSize(buttonSize);
        */
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
        mainPanel.add(addVisitButton);
        mainPanel.add(viewHistoryButton);
        mainPanel.add(addEditButton);
        mainPanel.add(addImportButton);

        // Adding help button to bottom-right corner
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(helpButton, BorderLayout.EAST);

        // Adding main panel and text area for logs to the background panel
        frame.add(paddedPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Add the background panel to the frame
        frame.add(backgroundPanel);

        // Make frame visible
        frame.setVisible(true);
    }
}
