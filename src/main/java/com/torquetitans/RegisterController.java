package com.torquetitans;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.torquetitans.model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class RegisterController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ChoiceBox<String> roleChoiceBox;

    @FXML
    private Label phoneLabel;

    @FXML
    private TextField phoneField;

    @FXML
    private Button registerButton;

    @FXML
    private Button backButton;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        roleChoiceBox.getItems().addAll("admin", "mechanic");
        roleChoiceBox.setValue("mechanic");

        // Show/hide phone field based on role
        roleChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("mechanic")) {
                phoneLabel.setVisible(true);
                phoneField.setVisible(true);
            } else {
                phoneLabel.setVisible(false);
                phoneField.setVisible(false);
                phoneField.setText(""); // Clear content if role is not mechanic
            }
        });

        // Default visibility for phoneField since the default role is mechanic
        phoneLabel.setVisible(true);
        phoneField.setVisible(true);

        registerButton.setOnAction(event -> {
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = roleChoiceBox.getValue();
            String phone = phoneField.getText();

            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || role == null) {
                errorLabel.setText("Please fill in all required information!");
                return;
            }

            if (role.equals("mechanic") && phone.isEmpty()) {
                errorLabel.setText("Please enter a phone number if registering as a mechanic!");
                return;
            }

            if (registerUser(email, username, password, role, phone)) {
                errorLabel.setText("Registration successful!");
                try {
                    Stage stage = (Stage) registerButton.getScene().getWindow();
                    stage.close();
                    Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
                    Stage loginStage = new Stage();
                    loginStage.setScene(new Scene(root));
                    loginStage.setTitle("Login - GarageApp");
                    loginStage.show();
                } catch (Exception e) {
                    errorLabel.setText("Error returning to login screen: " + e.getMessage());
                }
            }
        });

        backButton.setOnAction(event -> {
            try {
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.close();
                Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
                Stage loginStage = new Stage();
                loginStage.setScene(new Scene(root));
                loginStage.setTitle("Login - GarageApp");
                loginStage.show();
            } catch (Exception e) {
                errorLabel.setText("Error returning to login screen: " + e.getMessage());
            }
        });
    }

    private boolean registerUser(String email, String username, String password, String role, String phone) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Check if email already exists
            Query<User> emailQuery = session.createQuery("FROM User WHERE email = :email", User.class);
            emailQuery.setParameter("email", email);
            if (emailQuery.uniqueResult() != null) {
                errorLabel.setText("Email already exists!");
                return false;
            }

            // Check if username already exists
            Query<User> usernameQuery = session.createQuery("FROM User WHERE username = :username", User.class);
            usernameQuery.setParameter("username", username);
            if (usernameQuery.uniqueResult() != null) {
                errorLabel.setText("Username already exists!");
                return false;
            }

            // Create a new user
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setRole(role);

            // If the role is mechanic, create a new record in the mechanics table
            if (role.equals("mechanic")) {
                Mechanic mechanic = new Mechanic();
                mechanic.setName(username);
                mechanic.setEmail(email); // Use the user's email
                mechanic.setPhone(phone);

                Transaction transaction = session.beginTransaction();
                session.persist(mechanic);
                transaction.commit();

                // Assign the newly created MECHANIC_ID to the user
                newUser.setMechanicId(mechanic.getMechanicId());
            }

            // Save the user to the database
            Transaction transaction = session.beginTransaction();
            session.persist(newUser);
            transaction.commit();
            return true;
        } catch (Exception e) {
            errorLabel.setText("Registration error: " + e.getMessage());
            return false;
        }
    }
}