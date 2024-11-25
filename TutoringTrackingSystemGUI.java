package toggle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TutoringTrackingSystemGUI {

    private JFrame frame;
    private JButton addVisitButton, viewHistoryButton, EditButton, ImportButton;
    private JPanel mainPanel;
    private JTextField studentIdField, nameField, topicField, reasonField;
    private JComboBox<String> tutorDropdown, classDropdown;
    private JTextArea visitLogsArea;
    
    // Constructor
    public TutoringTrackingSystemGUI() {
        // Initialize the frame
        frame = new JFrame("CS Tutoring Tracking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Main panel setup
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(9, 2, 10, 10));

        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.setOpaque(false);
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        paddedPanel.add(mainPanel, BorderLayout.CENTER);

        // Labels and Text Fields for Student Information
        JLabel studentIdLabel = new JLabel("Student Win Number:");
        studentIdField = new JTextField();

        JLabel nameLabel = new JLabel("Student Name:");
        nameField = new JTextField();

        JLabel tutorLabel = new JLabel("Tutor:");
        String[] tutorOptions = {"Drew", "Pra"};
        tutorDropdown = new JComboBox<>(tutorOptions);

        JLabel topicLabel = new JLabel("Tutoring Topic:");
        topicField = new JTextField();

        JLabel classLabel = new JLabel("Class:");
        String[] classOptions = {"CM 105", "CM 111", "CM 130", "CM 203", "CM 231", "CM 245", "CM 261", "CM 290", "CM 303", "CM 307", "CM 322", "CM 331", "CM 333"};
        classDropdown = new JComboBox<>(classOptions);

        // Reason for Visiting
        JLabel reasonLabel = new JLabel("Reason for Visiting:");
        reasonField = new JTextField();

        // Buttons
        addVisitButton = new JButton("Add Visit");
        viewHistoryButton = new JButton("View Visit History");
        addVisitButton.setName("addVisitButton");
        viewHistoryButton.setName("viewHistoryButton");

        EditButton = new JButton("Edit");
        ImportButton = new JButton("Import");

        // Help Button
        JButton helpButton = new JButton("?");
        helpButton.setToolTipText("Click for help");
        helpButton.setPreferredSize(new Dimension(20, 20));
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
        visitLogsArea = new JTextArea(8, 40);
        visitLogsArea.setEditable(false);
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
        mainPanel.add(EditButton);
        mainPanel.add(ImportButton);

        // Bottom panel for help button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(helpButton, BorderLayout.EAST);

        // Add main panel and text area for logs to frame
        frame.add(paddedPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Button actions
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
                } else if (studentWin.length() < 8) {
                    JOptionPane.showMessageDialog(frame, "Invalid Win number", "Invalid input", JOptionPane.ERROR_MESSAGE);
                } else {
                    String tutorWin = getTutorWinNumber(tutorName);
                    if (tutorWin != null) {
                        saveVisitToDatabase(studentWin, studentName, topic, className, reason, tutorWin);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid tutor selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        viewHistoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayVisitHistory(visitLogsArea);
            }
        });

        // Make frame visible
        frame.setVisible(true);
    }

    public static String getTutorWinNumber(String tutorName) {
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
            JOptionPane.showMessageDialog(null, "Error retrieving visit history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JButton getAddVisitButton() {
        return addVisitButton;
    }

    // Getter method for View History button
    public JButton getViewHistoryButton() {
        return viewHistoryButton;
    }

    public JTextField getStudentIdField() {
        return studentIdField;
    }
    
    public JTextField getNameField() {
        return nameField;
    }
    
    public JTextField getTopicField() {
        return topicField;
    }
    
    public JTextField getReasonField() {
        return reasonField;
    }
    
    public JComboBox<String> getTutorDropdown() {
        return tutorDropdown;
    }
    
    public JComboBox<String> getClassDropdown() {
        return classDropdown;
    }

    public void displayGUI() {
        frame.setVisible(true);
    }

    public JFrame getFrame() {
        return frame;
    }

    public static void main(String[] args) {
        new TutoringTrackingSystemGUI();
    }

    
}
