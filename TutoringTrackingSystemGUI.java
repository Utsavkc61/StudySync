import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TutoringTrackingSystemGUI {

  private static final String DB_URL = "jdbc:mysql://localhost:3306/StudentTracking";
  private static final String DB_USER = "root";
  private static final String DB_PASSWORD = "ZxcvAsdf!@3";

  public static void main(String[] args) 
  {
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

    JLabel tutorLabel = new JLabel("Tutor:");
        String[] tutorOptions = {
            "Drew", "Pra"
        };
        JComboBox<String> tutorDropdown = new JComboBox<>(tutorOptions);


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

    // Adding the main panel and text area for logs to the frame
    frame.add(mainPanel, BorderLayout.NORTH);
    frame.add(scrollPane, BorderLayout.CENTER);

    addVisitButton.addActionListener((ActionListener) new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        String studentId = studentIdField.getText();
        String studentName = nameField.getText();
        String topic = topicField.getText();
        String className = (String) classDropdown.getSelectedItem();
        String reason = reasonField.getText();

        if (studentId.isEmpty() || studentName.isEmpty() || topic.isEmpty() || reason.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill out all fields.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            saveVisitToDatabase(studentId, studentName, topic, className, reason);
        }
      }
    });

    viewHistoryButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e){
        displayVisitHistory(visitLogsArea);
      }
    });

    // Make frame visible
    frame.setVisible(true);
  }

  private static void saveVisitToDatabase(String studentId, String studentName, String topic, String className, String reason)
  {
    String insertSQL = "INSERT INTO StudentVisits (student_name, class_name, visit_date, goals, goals_met) VALUES (?, ?, CURDATE(), ?, ?)";

     try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, studentName + " (WIN: " + studentId + ")");
            preparedStatement.setString(2, className);
            preparedStatement.setString(3, "Tutoring Topic: " + topic );
            preparedStatement.setBoolean(4, false); // Goals not marked met initially

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(null, "Visit logged successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to log visit: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
  }

   private static void displayVisitHistory(JTextArea visitLogsArea) {
        String querySQL = "SELECT * FROM StudentVisits";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            visitLogsArea.setText(""); // Clear previous logs

            while (resultSet.next()) {
                String visitLog = String.format("ID: %d | Name: %s | Class: %s | Date: %s | \n",
                        resultSet.getInt("visit_id"),
                        resultSet.getString("student_name"),
                        resultSet.getString("class_name"),
                        resultSet.getDate("visit_date"));
                        //resultSet.getString("goals"),
                        //resultSet.getBoolean("goals_met"));
                        //| Goals: %s | Goals Met: %b

                visitLogsArea.append(visitLog);
            }
        } catch (SQLException e) {
            visitLogsArea.setText("Failed to retrieve visit history: " + e.getMessage());
        }
    }

}
