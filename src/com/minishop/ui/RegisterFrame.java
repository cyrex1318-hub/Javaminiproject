package com.minishop.ui;

import com.minishop.db.DBConnection;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterFrame extends Frame {
    private final TextField usernameField = new TextField(20);
    private final TextField emailField = new TextField(20);
    private final TextField passwordField = new TextField(20);
    private final TextField phoneField = new TextField(15);
    private final TextArea addressArea = new TextArea(3, 20);
    private final Label messageLabel = new Label("");

    public RegisterFrame() {
        super("User Registration");
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        add(new Label("Username:"));
        add(usernameField);

        add(new Label("Email:"));
        add(emailField);

        add(new Label("Password:"));
        passwordField.setEchoChar('*');
        add(passwordField);

        add(new Label("Phone:"));
        add(phoneField);

        add(new Label("Address:"));
        add(addressArea);

        Button registerBtn = new Button("Register");
        Button cancelBtn = new Button("Cancel");
        add(registerBtn);
        add(cancelBtn);
        add(messageLabel);

        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setSize(360, 350);
        setLocationRelativeTo(null);
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Username, Email, Password are required");
            return;
        }

        String sql = "INSERT INTO Users (username, email, password, phone, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, phone);
            ps.setString(5, address);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                messageLabel.setText("Registered successfully");
                clearForm();
            } else {
                messageLabel.setText("Registration failed");
            }
        } catch (SQLException ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        phoneField.setText("");
        addressArea.setText("");
    }
}


