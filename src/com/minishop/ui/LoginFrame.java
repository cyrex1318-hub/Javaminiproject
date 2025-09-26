package com.minishop.ui;

import com.minishop.db.DBConnection;
import java.awt.Button;
import java.awt.Choice;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends Frame {
    private final TextField emailField = new TextField(22);
    private final TextField passwordField = new TextField(22);
    private final Choice roleChoice = new Choice();
    private final Label messageLabel = new Label("");

    public interface LoginListener {
        void onUserLogin(int userId, String username);
        void onAdminLogin(int adminId, String adminName);
    }

    private final LoginListener listener;

    public LoginFrame(LoginListener listener) {
        super("Login");
        this.listener = listener;
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        add(new Label("Role:"));
        roleChoice.add("User");
        roleChoice.add("Admin");
        add(roleChoice);

        add(new Label("Email:"));
        add(emailField);

        add(new Label("Password:"));
        passwordField.setEchoChar('*');
        add(passwordField);

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");
        add(loginBtn);
        add(registerBtn);
        add(messageLabel);

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterFrame().setVisible(true);
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setSize(360, 220);
        setLocationRelativeTo(null);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        boolean isAdmin = roleChoice.getSelectedItem().equals("Admin");

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Email and password required");
            return;
        }

        String sql = isAdmin ?
                "SELECT admin_id, admin_name FROM Admin WHERE email=? AND password=?" :
                "SELECT user_id, username FROM Users WHERE email=? AND password=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (isAdmin) {
                        int adminId = rs.getInt(1);
                        String name = rs.getString(2);
                        messageLabel.setText("Welcome, " + name);
                        if (listener != null) listener.onAdminLogin(adminId, name);
                    } else {
                        int userId = rs.getInt(1);
                        String username = rs.getString(2);
                        messageLabel.setText("Welcome, " + username);
                        if (listener != null) listener.onUserLogin(userId, username);
                    }
                } else {
                    messageLabel.setText("Invalid credentials");
                }
            }
        } catch (SQLException ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }
}


