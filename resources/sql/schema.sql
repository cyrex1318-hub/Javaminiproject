-- Online Shopping Mini Project - MySQL Schema and Sample Data
-- Run this entire script in MySQL before starting the application

DROP DATABASE IF EXISTS online_shop_awt;
CREATE DATABASE online_shop_awt CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE online_shop_awt;

-- Users table
CREATE TABLE Users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  email VARCHAR(150) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  phone VARCHAR(30),
  address VARCHAR(255)
);

-- Products table
CREATE TABLE Products (
  product_id INT AUTO_INCREMENT PRIMARY KEY,
  product_name VARCHAR(150) NOT NULL,
  description TEXT,
  price DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL DEFAULT 0,
  category VARCHAR(100)
);

-- Orders table
CREATE TABLE Orders (
  order_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  status VARCHAR(50) NOT NULL DEFAULT 'PLACED',
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- OrderItems table
CREATE TABLE OrderItems (
  order_item_id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT NOT NULL,
  product_id INT NOT NULL,
  quantity INT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_items_order FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
  CONSTRAINT fk_items_product FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

-- Admin table
CREATE TABLE Admin (
  admin_id INT AUTO_INCREMENT PRIMARY KEY,
  admin_name VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
);

-- Sample data
INSERT INTO Admin (admin_name, email, password) VALUES
('Super Admin', 'admin@example.com', 'admin123');

INSERT INTO Users (username, email, password, phone, address) VALUES
('alice', 'alice@example.com', 'alice123', '1234567890', '221B Baker Street'),
('bob', 'bob@example.com', 'bob123', '0987654321', '742 Evergreen Terrace');

INSERT INTO Products (product_name, description, price, stock, category) VALUES
('Wireless Mouse', '2.4G wireless mouse, ergonomic', 12.99, 50, 'Electronics'),
('USB-C Cable', '1m braided cable', 6.49, 100, 'Accessories'),
('Notebook', 'A5 ruled 200 pages', 3.25, 200, 'Stationery'),
('Water Bottle', 'Insulated 750ml', 15.00, 80, 'Home & Kitchen');

-- Example order for alice
INSERT INTO Orders (user_id, total_amount, status) VALUES (1, 19.48, 'PLACED');
INSERT INTO OrderItems (order_id, product_id, quantity, price) VALUES
(1, 1, 1, 12.99),
(1, 2, 1, 6.49);

-- Reduce stock accordingly (example)
UPDATE Products SET stock = stock - 1 WHERE product_id IN (1,2);


