package com.minishop.ui;

import com.minishop.db.DBConnection;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductFrame extends Frame {
    private final List productList = new List(12);
    private final TextField nameField = new TextField(20);
    private final TextArea descArea = new TextArea(3, 20);
    private final TextField priceField = new TextField(10);
    private final TextField stockField = new TextField(10);
    private final TextField categoryField = new TextField(15);
    private final Label messageLabel = new Label("");

    private Integer selectedProductId = null;

    public ProductFrame() {
        super("Product Management");
        setLayout(new BorderLayout(10, 10));

        Panel left = new Panel(new BorderLayout());
        left.add(new Label("Products"), BorderLayout.NORTH);
        left.add(productList, BorderLayout.CENTER);

        Panel right = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        right.add(new Label("Name:")); right.add(nameField);
        right.add(new Label("Category:")); right.add(categoryField);
        right.add(new Label("Price:")); right.add(priceField);
        right.add(new Label("Stock:")); right.add(stockField);
        right.add(new Label("Description:")); right.add(descArea);

        Panel actions = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        Button addBtn = new Button("Add");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");
        Button refreshBtn = new Button("Refresh");
        actions.add(addBtn); actions.add(updateBtn); actions.add(deleteBtn); actions.add(refreshBtn);
        actions.add(messageLabel);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        productList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSelectedProduct();
            }
        });

        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });
        updateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProduct();
            }
        });
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProduct();
            }
        });
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillProductList();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setSize(720, 420);
        setLocationRelativeTo(null);
        fillProductList();
    }

    private void fillProductList() {
        productList.removeAll();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT product_id, product_name, price, stock FROM Products ORDER BY product_id DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("product_id");
                String line = id + " | " + rs.getString("product_name") + " | $" + rs.getBigDecimal("price") + " | stock=" + rs.getInt("stock");
                productList.add(line);
            }
            messageLabel.setText("Loaded products");
        } catch (SQLException ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void loadSelectedProduct() {
        int idx = productList.getSelectedIndex();
        if (idx < 0) return;
        String item = productList.getItem(idx);
        try {
            int pipe = item.indexOf('|');
            int id = Integer.parseInt(item.substring(0, pipe).trim());
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM Products WHERE product_id=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        selectedProductId = id;
                        nameField.setText(rs.getString("product_name"));
                        categoryField.setText(rs.getString("category"));
                        priceField.setText(rs.getBigDecimal("price").toPlainString());
                        stockField.setText(String.valueOf(rs.getInt("stock")));
                        descArea.setText(rs.getString("description"));
                    }
                }
            }
        } catch (Exception ex) {
            messageLabel.setText("Parse error");
        }
    }

    private void addProduct() {
        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        String desc = descArea.getText().trim();
        String priceStr = priceField.getText().trim();
        String stockStr = stockField.getText().trim();
        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            messageLabel.setText("Name, price, stock required");
            return;
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO Products (product_name, description, price, stock, category) VALUES (?,?,?,?,?)")) {
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setBigDecimal(3, new java.math.BigDecimal(priceStr));
            ps.setInt(4, Integer.parseInt(stockStr));
            ps.setString(5, category);
            ps.executeUpdate();
            messageLabel.setText("Product added");
            clearForm();
            fillProductList();
        } catch (SQLException ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void updateProduct() {
        if (selectedProductId == null) { messageLabel.setText("Select a product"); return; }
        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        String desc = descArea.getText().trim();
        String priceStr = priceField.getText().trim();
        String stockStr = stockField.getText().trim();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE Products SET product_name=?, description=?, price=?, stock=?, category=? WHERE product_id=?")) {
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setBigDecimal(3, new java.math.BigDecimal(priceStr));
            ps.setInt(4, Integer.parseInt(stockStr));
            ps.setString(5, category);
            ps.setInt(6, selectedProductId);
            ps.executeUpdate();
            messageLabel.setText("Updated");
            fillProductList();
        } catch (SQLException ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void deleteProduct() {
        if (selectedProductId == null) { messageLabel.setText("Select a product"); return; }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Products WHERE product_id=?")) {
            ps.setInt(1, selectedProductId);
            ps.executeUpdate();
            messageLabel.setText("Deleted");
            clearForm();
            fillProductList();
        } catch (SQLException ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        selectedProductId = null;
        nameField.setText("");
        categoryField.setText("");
        descArea.setText("");
        priceField.setText("");
        stockField.setText("");
    }
}


