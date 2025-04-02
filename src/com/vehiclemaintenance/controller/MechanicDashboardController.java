package com.vehiclemaintenance.controller;

import com.vehiclemaintenance.dao.ServiceRecordDAO;
import com.vehiclemaintenance.entity.ServiceRecord;
import com.vehiclemaintenance.entity.Users;
import com.vehiclemaintenance.entity.Vehicle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.List;

public class MechanicDashboardController {
    @FXML private TableView<ServiceRecord> tasksTable;
    @FXML private TableColumn<ServiceRecord, String> vehiclePlateColumn;
    @FXML private TableColumn<ServiceRecord, String> serviceTypeColumn;
    @FXML private TableColumn<ServiceRecord, String> statusColumn;
    @FXML private TextArea vehicleDetailsArea;
    @FXML private TextField searchField;

    private ServiceRecordDAO serviceRecordDAO = new ServiceRecordDAO();
    private Users currentUser;
    private ObservableList<ServiceRecord> allTasks;

    @FXML
    private void initialize() {
        // Set up table columns
        vehiclePlateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVehicle().getLicensePlate()));
        serviceTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        // Display vehicle details when a task is selected
        tasksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayVehicleDetails(newSelection);
            } else {
                vehicleDetailsArea.clear();
            }
        });

        // Load tasks after the stage is set
        Platform.runLater(this::loadTasks);
    }

    private void loadTasks() {
        Stage stage = (Stage) tasksTable.getScene().getWindow();
        currentUser = (Users) stage.getUserData();
        List<ServiceRecord> tasks = serviceRecordDAO.getAssignedTasks(currentUser.getUserId());
        allTasks = FXCollections.observableArrayList(tasks);
        tasksTable.setItems(allTasks);
    }

    @FXML
    private void searchTasks() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            tasksTable.setItems(allTasks);
            return;
        }

        ObservableList<ServiceRecord> filteredTasks = FXCollections.observableArrayList();
        for (ServiceRecord task : allTasks) {
            if (task.getVehicle().getLicensePlate().toLowerCase().contains(searchText) ||
                task.getDescription().toLowerCase().contains(searchText) ||
                task.getVehicle().getOwner().getName().toLowerCase().contains(searchText)) {
                filteredTasks.add(task);
            }
        }
        tasksTable.setItems(filteredTasks);
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        tasksTable.setItems(allTasks);
    }

    @FXML
    private void updateStatus() {
        ServiceRecord selected = tasksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a task to update.");
            alert.showAndWait();
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Ongoing", "Ongoing", "Pending");
        dialog.setTitle("Update Status");
        dialog.setHeaderText("Select new status for task");
        dialog.setContentText("Status:");

        dialog.showAndWait().ifPresent(status -> {
            selected.setStatus(status);
            serviceRecordDAO.updateServiceRecord(selected);
            tasksTable.refresh();
        });
    }

    private void displayVehicleDetails(ServiceRecord serviceRecord) {
        Vehicle vehicle = serviceRecord.getVehicle();
        StringBuilder details = new StringBuilder();
        details.append("Owner: ").append(vehicle.getOwner().getName()).append("\n");
        details.append("Plate Number: ").append(vehicle.getLicensePlate()).append("\n");
        details.append("Model: ").append(vehicle.getMake()).append(" ").append(vehicle.getModel())
               .append(" (").append(vehicle.getYear()).append(")\n");
        details.append("Last Service: ").append(new SimpleDateFormat("yyyy-MM-dd").format(serviceRecord.getServiceDate())).append("\n");
        details.append("Service History:\n");
        details.append("- ").append(serviceRecord.getDescription()).append(" (")
               .append(serviceRecord.getServiceDate()).append(")\n");
        vehicleDetailsArea.setText(details.toString());
    }

    @FXML
    private void logout() throws Exception {
        Stage stage = (Stage) tasksTable.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("Login");
    }
}