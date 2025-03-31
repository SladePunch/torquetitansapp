package GarageApp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import GarageApp.model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Label errorLabel;

    private static User loggedInUser;

    @FXML
    private void initialize() {
        if (errorLabel == null) {
            errorLabel = new Label();
        }
        errorLabel.setStyle("-fx-text-fill: red;");

        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please enter both username and password!");
                return;
            }
            if (checkLogin(username, password)) {
                try {
                    loggedInUser = getUser(username);
                    String roleName = loggedInUser.getRole().getRoleName();
                    if (roleName.equals("Mechanic")) {
                        showMechanicDashboard();
                    } else if (roleName.equals("Admin")) {
                        showAdminDashboard();
                    } else {
                        errorLabel.setText("Invalid role: " + roleName);
                    }
                } catch (Exception e) {
                    errorLabel.setText("Error during redirection: " + e.getMessage());
                }
            } else {
                errorLabel.setText("Incorrect login information!");
            }
        });

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
                errorLabel.setText("Error opening registration screen: " + e.getMessage());
            }
        });
    }

    private boolean checkLogin(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            User result = query.uniqueResult();
            if (result != null && BCrypt.checkpw(password, result.getPassword())) {
                return true;
            }
            return false;
        } catch (Exception e) {
            errorLabel.setText("Connection error: " + e.getMessage());
            return false;
        }
    }

    private User getUser(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        } catch (Exception e) {
            errorLabel.setText("Error retrieving user information: " + e.getMessage());
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
            errorLabel.setText("Error opening MechanicDashboard: " + e.getMessage());
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
            errorLabel.setText("Error opening AdminDashboard: " + e.getMessage());
        }
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }
}