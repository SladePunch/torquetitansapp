package com.vehiclemaintenance.controller;

import com.vehiclemaintenance.dao.UserDAO;
import com.vehiclemaintenance.entity.Users;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Users user = userDAO.authenticate(username, password);
        if (user == null) {
            errorLabel.setText("Invalid username or password");
            errorLabel.setVisible(true);
            return;
        }

        Stage stage = (Stage) usernameField.getScene().getWindow();
        String role = user.getRole().getRoleName();
        Parent root;
        if ("Admin".equals(role)) {
            root = FXMLLoader.load(getClass().getResource("/admin_dashboard.fxml"));
        } else {
            root = FXMLLoader.load(getClass().getResource("/mechanic_dashboard.fxml"));
        }
        stage.setUserData(user);
        stage.setScene(new Scene(root, 800, 600)); // Set consistent size
        stage.setTitle(role + " Dashboard");
    }
}