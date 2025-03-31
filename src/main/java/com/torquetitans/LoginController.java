package com.torquetitans;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.torquetitans.model.*;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton; // Add Register button

    // Static variable to store the logged-in user's information
    private static User loggedInUser;

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (checkLogin(username, password)) {
                try {
                    loggedInUser = getUser(username, password);
                    if (loggedInUser.getRole().equals("mechanic")) {
                        showMechanicDashboard();
                    } else if (loggedInUser.getRole().equals("admin")) {
                        showAdminDashboard();
                    } else {
                        System.out.println("Invalid role: " + loggedInUser.getRole());
                    }
                } catch (Exception e) {
                    System.out.println("Error during redirection: " + e.getMessage());
                }
            } else {
                System.out.println("Incorrect login information!");
            }
        });

        // Handle Register button event
        registerButton.setOnAction(event -> {
            try {
                Stage stage = (Stage) registerButton.getScene().getWindow();
                stage.close();
                Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
                Stage registerStage = new Stage();
                registerStage.setScene(new Scene(root));
                registerStage.setTitle("Register - GarageApp");
                registerStage.show();
            } catch (Exception e) {
                System.out.println("Error opening registration screen: " + e.getMessage());
            }
        });
    }

    private boolean checkLogin(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("Checking: username=" + username + ", password=" + password);
            Query<User> query = session.createQuery("FROM User WHERE username = :username AND password = :password", User.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            User result = query.uniqueResult();
            System.out.println("Query result: " + (result != null ? "Found: " + result.getUsername() : "Not found"));
            return result != null;
        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
            return false;
        }
    }

    private User getUser(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE username = :username AND password = :password", User.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            return query.uniqueResult();
        } catch (Exception e) {
            System.out.println("Error retrieving user information: " + e.getMessage());
            return null;
        }
    }

    private void showMechanicDashboard() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();
            Parent root = FXMLLoader.load(getClass().getResource("MechanicDashboard.fxml"));
            Stage mechanicStage = new Stage();
            mechanicStage.setScene(new Scene(root));
            mechanicStage.setTitle("Mechanic - GarageApp");
            mechanicStage.show();
        } catch (Exception e) {
            System.out.println("Error opening MechanicDashboard: " + e.getMessage());
        }
    }

    private void showAdminDashboard() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();
            Parent root = FXMLLoader.load(getClass().getResource("AdminDashboard.fxml"));
            Stage adminStage = new Stage();
            adminStage.setScene(new Scene(root));
            adminStage.setTitle("Admin - GarageApp");
            adminStage.show();
        } catch (Exception e) {
            System.out.println("Error opening AdminDashboard: " + e.getMessage());
        }
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }
}