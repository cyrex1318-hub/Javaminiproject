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

public class OrderFrame extends Frame {
    private final int userId;
    private final Choice productChoice = new Choice();
    private final TextField qtyField = new TextField("1", 5);
    private final Label totalLabel = new Label("Total: $0.00");
    private final Label messageLabel = new Label("");

    public OrderFrame(int userId) {
        super("Place Order");
        this.userId = userId;
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        add(new Label("Product:"));
        add(productChoice);
        add(new Label("Qty:"));
        add(qtyField);
        Button calcBtn = new Button("Calculate");
        Button placeBtn = new Button("Place Order");
        add(calcBtn);
        add(placeBtn);
        add(totalLabel);
        add(messageLabel);

        calcBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateTotal();
            }
        });
        placeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeOrder();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setSize(520, 180);
        setLocationRelativeTo(null);
        loadProducts();
    }

    private void loadProducts() {
        productChoice.removeAll();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT product_id, product_name, price, stock FROM Products WHERE stock > 0 ORDER BY product_name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String item = rs.getInt("product_id") + ":" + rs.getString("product_name") + " ($" + rs.getBigDecimal("price") + ") [" + rs.getInt("stock") + "]";
                productChoice.add(item);
            }
        } catch (SQLException ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void calculateTotal() {
        try {
            String sel = productChoice.getSelectedItem();
            if (sel == null) return;
            int colon = sel.indexOf(":");
            int id = Integer.parseInt(sel.substring(0, colon));
            int qty = Integer.parseInt(qtyField.getText().trim());
            if (qty <= 0) { messageLabel.setText("Qty must be > 0"); return; }
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT price FROM Products WHERE product_id=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        java.math.BigDecimal price = rs.getBigDecimal(1);
                        java.math.BigDecimal total = price.multiply(new java.math.BigDecimal(qty));
                        totalLabel.setText("Total: $" + total.toPlainString());
                    }
                }
            }
        } catch (Exception ex) {
            messageLabel.setText("Error");
        }
    }

    private void placeOrder() {
        String sel = productChoice.getSelectedItem();
        if (sel == null) { messageLabel.setText("No product"); return; }
        try {
            int colon = sel.indexOf(":");
            int productId = Integer.parseInt(sel.substring(0, colon));
            int qty = Integer.parseInt(qtyField.getText().trim());
            if (qty <= 0) { messageLabel.setText("Qty must be > 0"); return; }

            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false);
                java.math.BigDecimal price;
                int stock;
                try (PreparedStatement ps = conn.prepareStatement("SELECT price, stock FROM Products WHERE product_id=? FOR UPDATE")) {
                    ps.setInt(1, productId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) { conn.rollback(); messageLabel.setText("Product missing"); return; }
                        price = rs.getBigDecimal(1);
                        stock = rs.getInt(2);
                    }
                }
                if (stock < qty) { conn.rollback(); messageLabel.setText("Insufficient stock"); return; }

                java.math.BigDecimal total = price.multiply(new java.math.BigDecimal(qty));
                int orderId;
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Orders (user_id, total_amount, status) VALUES (?, ?, 'PLACED')", PreparedStatement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, userId);
                    ps.setBigDecimal(2, total);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        orderId = keys.getInt(1);
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO OrderItems (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, productId);
                    ps.setInt(3, qty);
                    ps.setBigDecimal(4, price);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("UPDATE Products SET stock = stock - ? WHERE product_id=?")) {
                    ps.setInt(1, qty);
                    ps.setInt(2, productId);
                    ps.executeUpdate();
                }
                conn.commit();
                messageLabel.setText("Order placed. ID=" + orderId);
                calculateTotal();
                loadProducts();
            } catch (SQLException ex) {
                messageLabel.setText("Error: " + ex.getMessage());
            }
        } catch (Exception ex) {
            messageLabel.setText("Error");
        }
    }
}


