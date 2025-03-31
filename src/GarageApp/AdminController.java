package GarageApp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import GarageApp.model.ServiceRecord;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class AdminController {

    @FXML
    private ListView<String> jobList;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        if (errorLabel == null) {
            errorLabel = new Label();
        }
        errorLabel.setStyle("-fx-text-fill: red;");

        refreshJobList();

        addButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("JobForm.fxml"));
                Parent root = loader.load();
                JobFormController controller = loader.getController();
                controller.setAdminController(this);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Add Service Record");
                stage.show();
            } catch (Exception e) {
                errorLabel.setText("Error opening job form: " + e.getMessage());
            }
        });

        editButton.setOnAction(event -> {
            int selectedIndex = jobList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                String selectedItem = jobList.getItems().get(selectedIndex);
                Long serviceId = Long.parseLong(selectedItem.split(" ")[1]);
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    ServiceRecord selectedRecord = session.get(ServiceRecord.class, serviceId);
                    if (selectedRecord != null) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("JobForm.fxml"));
                        Parent root = loader.load();
                        JobFormController controller = loader.getController();
                        controller.setServiceRecord(selectedRecord);
                        controller.setAdminController(this);
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Edit Service Record");
                        stage.show();
                    } else {
                        errorLabel.setText("Service record not found!");
                    }
                } catch (Exception e) {
                    errorLabel.setText("Error opening job form: " + e.getMessage());
                }
            } else {
                errorLabel.setText("Please select a service record to edit!");
            }
        });

        deleteButton.setOnAction(event -> {
            int selectedIndex = jobList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                String selectedItem = jobList.getItems().get(selectedIndex);
                Long serviceId = Long.parseLong(selectedItem.split(" ")[1]);
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    Transaction transaction = session.beginTransaction();
                    ServiceRecord recordToDelete = session.get(ServiceRecord.class, serviceId);
                    if (recordToDelete != null) {
                        session.delete(recordToDelete);
                        transaction.commit();
                        refreshJobList();
                    } else {
                        errorLabel.setText("Service record not found!");
                        transaction.rollback();
                    }
                } catch (Exception e) {
                    errorLabel.setText("Error deleting service record: " + e.getMessage());
                }
            } else {
                errorLabel.setText("Please select a service record to delete!");
            }
        });

        logoutButton.setOnAction(event -> {
            try {
                Stage stage = (Stage) logoutButton.getScene().getWindow();
                stage.close();
                Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
                Stage loginStage = new Stage();
                loginStage.setScene(new Scene(root));
                loginStage.setTitle("Login - GarageApp");
                loginStage.show();
            } catch (Exception e) {
                errorLabel.setText("Error logging out: " + e.getMessage());
            }
        });
    }

    public void refreshJobList() {
        jobList.getItems().clear();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<ServiceRecord> recordListFromDB = session.createQuery("FROM ServiceRecord", ServiceRecord.class).list();
            for (ServiceRecord record : recordListFromDB) {
                String recordInfo = "ID: " + record.getServiceId() +
                                   " | License Plate: " + (record.getVehicle() != null ? record.getVehicle().getLicensePlate() : "N/A") +
                                   " | Service: " + record.getDescription() +
                                   " | Status: " + record.getStatus() +
                                   " | Date: " + record.getServiceDate() +
                                   " | Mechanic: " + (record.getMechanic() != null ? record.getMechanic().getName() : "N/A");
                jobList.getItems().add(recordInfo);
            }
        } catch (Exception e) {
            errorLabel.setText("Error loading service records: " + e.getMessage());
        }
    }
}