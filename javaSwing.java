import javax.swing.*;
import java.awt.*;

public class TutoringTrackingSystemGUI {

  public static void main(String[] args) {
    // Create the main frame
    JFrame frame = new JFrame("CS Tutoring Tracking System");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(600, 400);

    // Main panel
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayout(7, 2, 10, 10)); // Rows, Columns, Hgap, Vgap

    // Labels and Text Fields for Student Information
    JLabel studentIdLabel = new JLabel("Student Win Number:");
    JTextField studentIdField = new JTextField();

    JLabel nameLabel = new JLabel("Student Name:");
    JTextField nameField = new JTextField();

    JLabel topicLabel = new JLabel("Tutoring Topic:");
    JTextField topicField = new JTextField();

    JLabel classLabel = new JLabel("Class:");
    String[] classOptions = {"CM 105", "CM 111", "CM 130", "CM 203",  "CM 231", "CM 245","CM 261", "CM 290", "CM 303", "CM 307", "CM 322", "CM 331", "CM 333"};
    JComboBox<String> classDropdown = new JComboBox<>(classOptions);
    
     // New label and text field for Reason for Visiting
    JLabel reasonLabel = new JLabel("Reason for Visiting:");
    JTextField reasonField = new JTextField();

    // Add Visit Button and View Visit History
    JButton addVisitButton = new JButton("Add Visit");
    JButton viewHistoryButton = new JButton("View Visit History");

    // Area for viewing saved visit logs
    JTextArea visitLogsArea = new JTextArea(8, 40);
    visitLogsArea.setEditable(false); // Makes the area read-only
    JScrollPane scrollPane = new JScrollPane(visitLogsArea);

    // Adding components to the panel
    mainPanel.add(studentIdLabel);
    mainPanel.add(studentIdField);
    mainPanel.add(nameLabel);
    mainPanel.add(nameField);
    mainPanel.add(topicLabel);
    mainPanel.add(topicField);
    mainPanel.add(classLabel);
    mainPanel.add(classDropdown);
    mainPanel.add(reasonLabel); 
    mainPanel.add(reasonField);

    mainPanel.add(addVisitButton);
    mainPanel.add(viewHistoryButton);

    // Adding the main panel and text area for logs to the frame
    frame.add(mainPanel, BorderLayout.NORTH);
    frame.add(scrollPane, BorderLayout.CENTER);

    // Make frame visible
    frame.setVisible(true);
  }
}
