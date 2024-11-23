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

  //private static final String DB_URL = "jdbc:mysql://localhost:3306/StudentTracking";
  //private static final String DB_USER = "root";
  //private static final String DB_PASSWORD = "ZxcvAsdf!@3";

  public static void main(String[] args) 
  {
    JFrame frame = new JFrame("CS Tutoring Tracking System"); 
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(600, 400);

    // Main panel
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayout(7, 2, 10, 10)); // Rows, Columns, Hgap, Vgap

    JPanel paddedPanel= new JPanel(new BorderLayout());
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
    String[] classOptions = {"CM 105", "CM 111", "CM 130", "CM 203",  "CM 231", "CM 245","CM 261", "CM 290", "CM 303", "CM 307", "CM 322", "CM 331", "CM 333"};
    JComboBox<String> classDropdown = new JComboBox<>(classOptions);
    
     // New label and text field for Reason for Visiting
    JLabel reasonLabel = new JLabel("Reason for Visiting:");
    JTextField reasonField = new JTextField();

    // Add Visit Button and View Visit History
    JButton addVisitButton = new JButton("Add Visit");
    JButton viewHistoryButton = new JButton("View Visit History");

    // Help button with '?' icon
    //JButton helpButton = new JButton("?");
      JButton helpButton = new JButton("?") {
        @Override
        protected void paintComponent(Graphics g) {
            if (getModel().isArmed()) {
                g.setColor(Color.LIGHT_GRAY);
            } else {
                g.setColor(getBackground());
            }
            g.fillOval(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            g.setColor(getForeground());
            g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
        }

        @Override
        public boolean contains(int x, int y) {
            double radius = getWidth() / 2.0;
            double centerX = getWidth() / 2.0;
            double centerY = getHeight() / 2.0;
            return Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(radius, 2);
        }
    };

    helpButton.setToolTipText("Click for help");
    helpButton.setPreferredSize(new Dimension(20, 20)); // Adjust button size
    helpButton.setContentAreaFilled(false);
    helpButton.setFocusPainted(false);
    helpButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(frame,
                    "--- CS Tutoring Tracking System ---\n" +
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

    // Adding help button to bottom-right corner
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setOpaque(false);
    bottomPanel.add(helpButton, BorderLayout.EAST);


    // Adding the main panel and text area for logs to the frame
    frame.add(paddedPanel, BorderLayout.NORTH);
    frame.add(scrollPane, BorderLayout.CENTER);
    frame.add(bottomPanel, BorderLayout.SOUTH);


    addVisitButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
          String studentWin = studentIdField.getText();
          String studentName = nameField.getText();
          String tutorName = (String) tutorDropdown.getSelectedItem();
          String topic = topicField.getText();
          String className = (String) classDropdown.getSelectedItem();
          String reason = reasonField.getText();
  
          if (studentWin.isEmpty() || studentName.isEmpty() || tutorName == null || topic.isEmpty() || className == null || reason.isEmpty()) {
              JOptionPane.showMessageDialog(frame, "Please fill out all fields.", "Error", JOptionPane.ERROR_MESSAGE);
          } else {
              String tutorWin = getTutorWinNumber(tutorName); // Retrieve tutor's win number based on their name
              if (tutorWin != null) {
                  saveVisitToDatabase(studentWin, studentName, topic, className, reason, tutorWin);
              } else {
                  JOptionPane.showMessageDialog(frame, "Invalid tutor selected.", "Error", JOptionPane.ERROR_MESSAGE);
              }
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

  private static String getTutorWinNumber(String tutorName) {
    String querySQL = "SELECT win_number FROM Tutor WHERE tutor_name = ?";
    try (Connection connection = DatabaseManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
        preparedStatement.setString(1, tutorName);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getString("win_number");
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Failed to retrieve tutor information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return null;
  }


  private static void saveVisitToDatabase(String studentWin, String studentName, String topic, String className, String reason, String tutorWin) {
    String studentInsertSQL = "INSERT INTO Student (win_number, student_name) VALUES (?, ?) ON DUPLICATE KEY UPDATE student_name = VALUES(student_name)";
    String visitInsertSQL = "INSERT INTO Visit (student_win, tutor_win, class_name, visit_date, topic, reason) VALUES (?, ?, ?, CURDATE(), ?, ?)";

    try (Connection connection = DatabaseManager.getConnection()) {
        // Insert or update student details
        try (PreparedStatement studentStmt = connection.prepareStatement(studentInsertSQL)) {
            studentStmt.setString(1, studentWin);
            studentStmt.setString(2, studentName);
            studentStmt.executeUpdate();
        }

        // Insert visit details
        try (PreparedStatement visitStmt = connection.prepareStatement(visitInsertSQL)) {
            visitStmt.setString(1, studentWin);
            visitStmt.setString(2, tutorWin);
            visitStmt.setString(3, className);
            visitStmt.setString(4, topic);
            visitStmt.setString(5, reason);
            visitStmt.executeUpdate();
        }

        JOptionPane.showMessageDialog(null, "Visit logged successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Failed to log visit: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private static void displayVisitHistory(JTextArea visitLogsArea) {
    String querySQL = "SELECT v.visit_id, s.student_name, s.win_number, t.tutor_name, v.class_name, v.visit_date, v.topic, v.reason " +
                      "FROM Visit v " +
                      "JOIN Student s ON v.student_win = s.win_number " +
                      "JOIN Tutor t ON v.tutor_win = t.win_number";

    try (Connection connection = DatabaseManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
         ResultSet resultSet = preparedStatement.executeQuery()) {

        visitLogsArea.setText(""); // Clear previous logs

        while (resultSet.next()) {
            String visitLog = String.format(
                "ID: %d | Student: %s (WIN: %s) | Tutor: %s | Class: %s | Date: %s | Topic: %s | Reason: %s\n",
                resultSet.getInt("visit_id"),
                resultSet.getString("student_name"),
                resultSet.getString("win_number"),
                resultSet.getString("tutor_name"),
                resultSet.getString("class_name"),
                resultSet.getDate("visit_date"),
                resultSet.getString("topic"),
                resultSet.getString("reason")
            );

            visitLogsArea.append(visitLog);
        }
    } catch (SQLException e) {
        visitLogsArea.setText("Failed to retrieve visit history: " + e.getMessage());
    }
  } 


  /* 
  private static void saveVisitToDatabase(String studentId, String studentName, String topic, String className, String reason) {
    String insertSQL = "INSERT INTO StudentVisits (student_name, class_name, visit_date, Topic, Reason) VALUES (?, ?, CURDATE(), ?, ?)";
    //placeholders (?) for parameters.

    try {
        int rowsInserted = DatabaseManager.executeUpdate(insertSQL, 
                studentName + " (WIN: " + studentId + ")", 
                className, 
                "Tutoring Topic: " + topic, 
                false); 

        if (rowsInserted > 0) {
            JOptionPane.showMessageDialog(null, "Visit logged successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Failed to log visit: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    */

/* 
  private static void displayVisitHistory(JTextArea visitLogsArea) {
        String querySQL = "SELECT * FROM StudentVisits";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
              //designed to execute query statements so it returns a ResultSet that contains the data returned by the query

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
  }*/
}

