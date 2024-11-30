package toggle;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ActionMapUIResource;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TutoringTrackingSystemGUI {

    private JFrame frame;
    private JButton addVisitButton, viewHistoryButton, EditButton, ImportButton, SearchButton;
    private JPanel mainPanel;
    private JTextField studentIdField, nameField, topicField, reasonField;
    private JComboBox<String> tutorDropdown, classDropdown;
    private JTextArea visitLogsArea;
    
    // Constructor
    public TutoringTrackingSystemGUI() {
        // Initialize the frame
        frame = new JFrame("StudySync");
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

        //EditButton = new JButton("Edit");
        SearchButton = new JButton("Search");
        EditButton = new JButton("Update");

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
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0)); 

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
        mainPanel.add(SearchButton);
        mainPanel.add(EditButton);

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

                if (studentWin.isEmpty() || studentName.isEmpty() || tutorName == null || topic.isEmpty() || className == null) {
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

        SearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                openSearchFrame();
            }
        });

        EditButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                updatePerformed();
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

    private String getTutorName(String tutorWin) {
        String querySQL = "SELECT tutor_name FROM Tutor WHERE win_number = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setString(1, tutorWin);
    
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("tutor_name");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error retrieving tutor name: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private void saveVisitToDatabase(String studentWin, String studentName, String topic, String className, String reason, String tutorWin) {
        String studentInsertSQL = "INSERT INTO Student (win_number, student_name) VALUES (?, ?) ON DUPLICATE KEY UPDATE student_name = VALUES(student_name)";
        String visitInsertSQL = "INSERT INTO Visit (student_win, tutor_win, class_name, visit_date, topic, reason) VALUES (?, ?, ?, CURDATE(), ?, ?)";
        //Should clear all the fileds once the data is entered
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
            clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to log visit: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void displayVisitHistory(JTextArea visitLogsArea) {
        String querySQL = "SELECT v.visit_id, s.student_name, s.win_number, t.tutor_name, v.class_name, v.visit_date, v.topic, v.reason " +
            "FROM Visit v " +
            "JOIN Student s ON v.student_win = s.win_number " +
            "JOIN Tutor t ON v.tutor_win = t.win_number " +
            "ORDER BY v.visit_id";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            StringBuilder logs = new StringBuilder();
            while (resultSet.next()) {
                logs.append("• Visit ID: ").append(resultSet.getInt("visit_id")).append("\n")
                        .append("• Student Name: ").append(resultSet.getString("student_name"))
                        .append(" • Student Win Number: ").append(resultSet.getString("win_number")).append("\n")
                        .append("• Tutor: ").append(resultSet.getString("tutor_name"))
                        .append(" • Class: ").append(resultSet.getString("class_name"))
                        .append(" • Date: ").append(resultSet.getDate("visit_date")).append("\n")
                        .append("• Topic: ").append(resultSet.getString("topic"))
                        .append(" • Reason: ").append(resultSet.getString("reason")).append("\n")
                        .append("-----------------------------------------------------------------\n");
            }
            visitLogsArea.setText(logs.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to retrieve visit history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSearchFrame()
    {
        
        JFrame searchFrame = new JFrame("Search Visit History");
        searchFrame.setSize(400,300);
        searchFrame.setLayout(new BorderLayout());
        //searchFrame.getContentPane().setBackground(new Color(200, 220, 255)); // Light cyan-like color

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(new Color(230,230,230));

        JLabel searchLabel = new JLabel("Enter Student WIN number");
        JTextField searchField = new JTextField(15);

        JTextArea searchResultArea = new JTextArea(6,30);
        searchResultArea.setEditable(false);

        JScrollPane searchScrollPane = new JScrollPane(searchResultArea);
        searchScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230,230,230));
        JButton searchButton = new JButton("Search");
        buttonPanel.add(searchButton);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        searchFrame.add(searchPanel, BorderLayout.NORTH);
        searchFrame.add(searchScrollPane, BorderLayout.CENTER);
        searchFrame.add(buttonPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                String studentWin = searchField.getText().trim();
                if(studentWin.isEmpty())
                {
                    JOptionPane.showMessageDialog(searchFrame, "Please enter a WIN number", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //String querySQL = "SELECT v.visit_id, s.student_name, t.tutor_name, v.class_name, v.visit_date, v.topic, v.reason" + "FROM Visit v"
                                   // + "JOIN Student s ON v.student_win = s.win_number" + "JOIN Tutor t ON v.tutor_win = t.win_number" +
                                    //"WHERE s.win_number=?";

                                    String querySQL = "SELECT v.visit_id, s.student_name, t.tutor_name, v.class_name, v.visit_date, v.topic, v.reason " +
                                    "FROM Visit v " +
                                    "JOIN Student s ON v.student_win = s.win_number " +
                                    "JOIN Tutor t ON v.tutor_win = t.win_number " +
                                    "WHERE s.win_number = ?";
                  
                try(Connection connection = DatabaseManager.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(querySQL))
                    {
                        preparedStatement.setString(1, studentWin);

                        try(ResultSet resultSet = preparedStatement.executeQuery())
                        {
                            StringBuilder results = new StringBuilder();
                            while(resultSet.next())
                            {
                                results.append("Visit ID: ").append(resultSet.getInt("visit_id")).append("\n") .append("Student Name: ").append(resultSet.getString("student_name")).append("\n") .append("Tutor: ").append(resultSet.getString("tutor_name")).append("\n") .append("Class: ").append(resultSet.getString("class_name")).append("\n") .append("Date: ").append(resultSet.getDate("visit_date")).append("\n") .append("Topic: ").append(resultSet.getString("topic")).append("\n") .append("Reason: ").append(resultSet.getString("reason")).append("\n") .append("----------------------------------------\n");
                            }
                            if(results.length()==0)
                            {
                                results.append("No visits found for WIN number ").append(studentWin).append(".");
                            }
                            searchResultArea.setText(results.toString());
                        }
                    }catch(SQLException ex)
                    {
                        JOptionPane.showMessageDialog(searchFrame, "Error retrieving visit history: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                
            }
        });

        searchFrame.setVisible(true);
    }

    private void updatePerformed()
    {
        JFrame updateFrame = new JFrame("Update Visit Record");
        updateFrame.setSize(400,300);
        updateFrame.setLayout(new BorderLayout());

        JPanel updatePanel = new JPanel(new GridLayout(6,2,10,10));
         
        JLabel visitIdLabel = new JLabel("Visit ID");
        JTextField visitIdField = new JTextField();
        JLabel studentIdLabel = new JLabel("Student Win Number:");
        JTextField studentIdField = new JTextField();
        JLabel studentNameLabel = new JLabel("Student Name:");
        JTextField studentNameField = new JTextField();
        JLabel topicLabel = new JLabel("Tutoring Topic:");
        JTextField topicField = new JTextField();
        JLabel reasonLabel = new JLabel("Reason for Visit:");
        JTextField reasonField = new JTextField();

        updatePanel.add(visitIdLabel);
        updatePanel.add(visitIdField);
        updatePanel.add(studentIdLabel);
        updatePanel.add(studentIdField);
        updatePanel.add(studentNameLabel);
        updatePanel.add(studentNameField);
        updatePanel.add(topicLabel);
        updatePanel.add(topicField);
        updatePanel.add(reasonLabel);
        updatePanel.add(reasonField);

        JButton fetchButton = new JButton("Retrieve");
        JButton saveButton = new JButton("Save Changes");

        fetchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String visitId = visitIdField.getText();
                
                if (visitId.isEmpty()) {
                    JOptionPane.showMessageDialog(updateFrame, "Please enter a valid Visit ID.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Fetch visit details from the database
                String fetchSQL = "SELECT v.student_win, s.student_name, v.topic, v.reason " +
                        "FROM Visit v " +
                        "JOIN Student s ON v.student_win = s.win_number " +
                        "WHERE v.visit_id = ?";
                try (Connection connection = DatabaseManager.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(fetchSQL)) {
                    preparedStatement.setInt(1, Integer.parseInt(visitId));
                    
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            studentIdField.setText(resultSet.getString("student_win"));
                            studentNameField.setText(resultSet.getString("student_name"));
                            topicField.setText(resultSet.getString("topic"));
                            reasonField.setText(resultSet.getString("reason"));
                        } else {
                            JOptionPane.showMessageDialog(updateFrame, "Visit ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(updateFrame, "Failed to fetch visit details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String visitId = visitIdField.getText();
                String studentWin = studentIdField.getText();
                String studentName = studentNameField.getText();
                String topic = topicField.getText();
                String reason = reasonField.getText();
                
                if (visitId.isEmpty() || studentWin.isEmpty() || studentName.isEmpty() || topic.isEmpty() || reason.isEmpty()) {
                    JOptionPane.showMessageDialog(updateFrame, "Please fill out all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Update the database
                String updateSQL = "UPDATE Visit v JOIN Student s ON v.student_win = s.win_number " +
                        "SET v.topic = ?, v.reason = ?, s.student_name = ? " +
                        "WHERE v.visit_id = ?";
                try (Connection connection = DatabaseManager.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
                    preparedStatement.setString(1, topic);
                    preparedStatement.setString(2, reason);
                    preparedStatement.setString(3, studentName);
                    preparedStatement.setInt(4, Integer.parseInt(visitId));
                    
                    int rowsUpdated = preparedStatement.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(updateFrame, "Visit record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        updateFrame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(updateFrame, "Failed to update visit record.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(updateFrame, "Error updating visit record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(fetchButton);
        buttonPanel.add(saveButton);
        
        updateFrame.add(updatePanel, BorderLayout.CENTER);
        updateFrame.add(buttonPanel, BorderLayout.SOUTH);
        
        updateFrame.setVisible(true);

    }

    private void clearFields()
    {
        studentIdField.setText("");
        nameField.setText("");
        topicField.setText("");
        reasonField.setText("");

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

    public static void main(String[] args) 
    {
        new TutoringTrackingSystemGUI();
    }  
}
