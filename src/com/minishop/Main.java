package com.minishop;

import com.minishop.ui.AdminFrame;
import com.minishop.ui.HistoryFrame;
import com.minishop.ui.LoginFrame;
import com.minishop.ui.OrderFrame;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {
        Frame root = new Frame("Online Shop (AWT)");
        CardLayout layout = new CardLayout();
        root.setLayout(layout);

        Panel entry = new Panel();
        Button loginBtn = new Button("Login");
        entry.add(new Label("Welcome to Online Shop"));
        entry.add(loginBtn);

        root.add(entry, "entry");

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginFrame lf = new LoginFrame(new LoginFrame.LoginListener() {
                    @Override
                    public void onUserLogin(int userId, String username) {
                        lf.dispose();
                        Frame userHub = new Frame("User: " + username);
                        Button orderBtn = new Button("Place Order");
                        Button historyBtn = new Button("Order History");
                        userHub.add(orderBtn);
                        userHub.add(historyBtn);
                        userHub.setLayout(new java.awt.FlowLayout());
                        userHub.setSize(360, 180);
                        userHub.setLocationRelativeTo(null);
                        orderBtn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                new OrderFrame(userId).setVisible(true);
                            }
                        });
                        historyBtn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                new HistoryFrame(userId).setVisible(true);
                            }
                        });
                        userHub.setVisible(true);
                    }

                    @Override
                    public void onAdminLogin(int adminId, String adminName) {
                        lf.dispose();
                        new AdminFrame(adminId, adminName).setVisible(true);
                    }
                });
                lf.setVisible(true);
            }
        });

        root.setSize(400, 200);
        root.setLocationRelativeTo(null);
        root.setVisible(true);
    }
}


