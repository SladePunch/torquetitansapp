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
	private Stage stage;
    
    @FXML
    private void handleLogin() throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Users user = userDAO.validateUser(username, password);
        if (user == null) {
            errorLabel.setText("Invalid username or password");
            errorLabel.setVisible(true);
            return;
        }

        System.out.println("Logged in user: Username=" + user.getUsername() + 
                          ", UserID=" + user.getUserId() + 
                          ", RoleID=" + user.getRoleId());

        Stage stage = (Stage) usernameField.getScene().getWindow();
        String fxmlFile;
        String roleTitle;
        if (user.getRoleId() == 1) {
            fxmlFile = "/admin_dashboard.fxml";
            roleTitle = "Admin";
        } else if (user.getRoleId() == 2) {
            fxmlFile = "/mechanic_dashboard.fxml";
            roleTitle = "Mechanic";
        } else {
            errorLabel.setText("Unknown user role: " + user.getRoleId());
            errorLabel.setVisible(true);
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();

        if (user.getRoleId() == 1) {
            AdminDashboardController adminController = loader.getController();
            adminController.setCurrentUser(user);
        } else if (user.getRoleId() == 2) {
            MechanicDashboardController mechanicController = loader.getController();
            mechanicController.setCurrentUser(user);
        }

        stage.setScene(new Scene(root, 1280, 720));
        stage.setTitle(roleTitle + " Dashboard");
    }   
}