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
