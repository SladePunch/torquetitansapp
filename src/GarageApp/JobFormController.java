package GarageApp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import GarageApp.model.ServiceRecord;
import GarageApp.model.Mechanic;
import GarageApp.model.Vehicle;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Date;

public class JobFormController {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField serviceTypeField;

    @FXML
    private ChoiceBox<String> statusChoiceBox;

    @FXML
    private TextField mechanicIdField;

    @FXML
    private TextField vehicleIdField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label errorLabel;

    private ServiceRecord serviceRecord;
    private AdminController adminController;

    @FXML
    private void initialize() {
        if (errorLabel == null) {
            errorLabel = new Label();
        }
        errorLabel.setStyle("-fx-text-fill: red;");

        statusChoiceBox.getItems().addAll("Pending", "In Progress", "Completed", "Ongoing");
        statusChoiceBox.setValue("Pending");

        cancelButton.setOnAction(event -> {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });

        saveButton.setOnAction(event -> {
            try {
                String description = serviceTypeField.getText();
                String status = statusChoiceBox.getValue();
                String mechanicIdText = mechanicIdField.getText();
                String vehicleIdText = vehicleIdField.getText();

                if (description.isEmpty() || status == null || mechanicIdText.isEmpty() || vehicleIdText.isEmpty()) {
                    errorLabel.setText("Please fill in all fields!");
                    return;
                }

                Long mechanicId = Long.parseLong(mechanicIdText);
                Long vehicleId = Long.parseLong(vehicleIdText);

                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    Transaction transaction = session.beginTransaction();

                    Mechanic mechanic = session.get(Mechanic.class, mechanicId);
                    Vehicle vehicle = session.get(Vehicle.class, vehicleId);

                    if (mechanic == null || vehicle == null) {
                        errorLabel.setText("Invalid Mechanic ID or Vehicle ID!");
                        transaction.rollback();
                        return;
                    }

                    if (serviceRecord == null) {
                        serviceRecord = new ServiceRecord();
                        serviceRecord.setServiceDate(new Date());
                    }
                    serviceRecord.setDescription(description);
                    serviceRecord.setStatus(status);
                    serviceRecord.setMechanic(mechanic);
                    serviceRecord.setVehicle(vehicle);

                    session.saveOrUpdate(serviceRecord);
                    transaction.commit();

                    adminController.refreshJobList();

                    Stage stage = (Stage) saveButton.getScene().getWindow();
                    stage.close();
                } catch (Exception e) {
                    errorLabel.setText("Error saving service record: " + e.getMessage());
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("Mechanic ID and Vehicle ID must be numbers!");
            }
        });
    }

    public void setServiceRecord(ServiceRecord serviceRecord) {
        this.serviceRecord = serviceRecord;
        if (serviceRecord != null) {
            titleLabel.setText("Edit Service Record");
            serviceTypeField.setText(serviceRecord.getDescription());
            statusChoiceBox.setValue(serviceRecord.getStatus());
            mechanicIdField.setText(serviceRecord.getMechanic() != null ? String.valueOf(serviceRecord.getMechanic().getMechanicID()) : "");
            vehicleIdField.setText(serviceRecord.getVehicle() != null ? String.valueOf(serviceRecord.getVehicle().getVehicleId()) : "");
        } else {
            titleLabel.setText("Add Service Record");
        }
    }

    public void setAdminController(AdminController adminController) {
        this.adminController = adminController;
    }
}