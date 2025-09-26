package com.minishop.ui;

import com.minishop.db.DBConnection;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminFrame extends Frame {
    private final int adminId;
    private final String adminName;
    private final List ordersList = new List(14);
    private final Label messageLabel = new Label("");

    public AdminFrame(int adminId, String adminName) {
        super("Admin Dashboard");
        this.adminId = adminId;
        this.adminName = adminName;

        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));

        add(new Label("Welcome, " + adminName));

        Button manageProductsBtn = new Button("Manage Products");
        Button refreshOrdersBtn = new Button("Refresh Orders");
        add(manageProductsBtn);
        add(refreshOrdersBtn);
        add(new Label("All Orders:"));
        add(ordersList);
        add(messageLabel);

        manageProductsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProductFrame().setVisible(true);
            }
        });
        refreshOrdersBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadOrders();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setSize(720, 480);
        setLocationRelativeTo(null);
        loadOrders();
    }

    private void loadOrders() {
        ordersList.removeAll();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT o.order_id, u.username, o.order_date, o.total_amount, o.status FROM Orders o JOIN Users u ON u.user_id = o.user_id ORDER BY o.order_id DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String line = rs.getInt("order_id") + " | " + rs.getString("username") + " | " + rs.getString("order_date") + " | $" + rs.getBigDecimal("total_amount") + " | " + rs.getString("status");
                ordersList.add(line);
            }
            messageLabel.setText("Orders loaded");
        } catch (SQLException ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }
}


