package GarageApp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import GarageApp.model.Mechanic;
import GarageApp.model.Role;
import GarageApp.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private ComboBox<String> mechanicComboBox;

    @FXML
    private Button registerButton;

    @FXML
    private Button backButton;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        if (errorLabel == null) {
            errorLabel = new Label();
        }
        errorLabel.setStyle("-fx-text-fill: red;");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Role> roleQuery = session.createQuery("FROM Role", Role.class);
            List<Role> roles = roleQuery.list();
            for (Role role : roles) {
                roleComboBox.getItems().add(role.getRoleName());
            }

            Query<Mechanic> mechanicQuery = session.createQuery("FROM Mechanic", Mechanic.class);
            List<Mechanic> mechanics = mechanicQuery.list();
            for (Mechanic mechanic : mechanics) {
                mechanicComboBox.getItems().add(mechanic.getName());
            }

            roleComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                mechanicComboBox.setDisable(!"Mechanic".equals(newValue));
            });

            roleComboBox.getSelectionModel().select("Admin");
        } catch (Exception e) {
            errorLabel.setText("Error loading roles or mechanics: " + e.getMessage());
        }

        registerButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            String selectedRole = roleComboBox.getValue();
            String selectedMechanic = mechanicComboBox.getValue();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || selectedRole == null) {
                errorLabel.setText("Please fill in all required fields!");
                return;
            }

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();

                Query<User> query = session.createQuery("FROM User WHERE username = :username OR email = :email", User.class);
                query.setParameter("username", username);
                query.setParameter("email", email);
                if (query.uniqueResult() != null) {
                    errorLabel.setText("Username or email already exists!");
                    return;
                }

                Query<Role> roleQuery = session.createQuery("FROM Role WHERE roleName = :roleName", Role.class);
                roleQuery.setParameter("roleName", selectedRole);
                Role role = roleQuery.uniqueResult();
                if (role == null) {
                    errorLabel.setText("Selected role not found!");
                    return;
                }

                Mechanic mechanic = null;
                if ("Mechanic".equals(selectedRole) && selectedMechanic != null) {
                    Query<Mechanic> mechanicQuery = session.createQuery("FROM Mechanic WHERE name = :name", Mechanic.class);
                    mechanicQuery.setParameter("name", selectedMechanic);
                    mechanic = mechanicQuery.uniqueResult();
                    if (mechanic == null) {
                        errorLabel.setText("Selected mechanic not found!");
                        return;
                    }
                }

                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
                newUser.setEmail(email);
                newUser.setRole(role);
                newUser.setMechanic(mechanic);

                session.persist(newUser);
                tx.commit();

                Stage stage = (Stage) registerButton.getScene().getWindow();
                stage.close();
                Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
                Stage loginStage = new Stage();
                loginStage.setScene(new Scene(root));
                loginStage.setTitle("Login - GarageApp");
                loginStage.show();
            } catch (Exception e) {
                errorLabel.setText("Error during registration: " + e.getMessage());
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
                errorLabel.setText("Error returning to login: " + e.getMessage());
            }
        });
    }
}