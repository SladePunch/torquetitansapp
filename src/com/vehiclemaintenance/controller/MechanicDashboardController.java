package com.vehiclemaintenance.controller;

import com.vehiclemaintenance.dao.ServiceRecordDAO;
import com.vehiclemaintenance.entity.ServiceRecord;
import com.vehiclemaintenance.entity.Users;
import com.vehiclemaintenance.entity.Vehicle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;

public class MechanicDashboardController {
    @FXML private TableView<ServiceRecord> tasksTable;
    @FXML private TableColumn<ServiceRecord, String> taskIdColumn;
    @FXML private TableColumn<ServiceRecord, String> vehiclePlateColumn;
    @FXML private TableColumn<ServiceRecord, String> serviceTypeColumn;
    @FXML private TableColumn<ServiceRecord, String> statusColumn;
    @FXML private TextArea vehicleDetailsArea;

    private ServiceRecordDAO serviceRecordDAO = new ServiceRecordDAO();
    private Users currentUser;

    @FXML
    private void initialize() {
        // Set up the table columns
        taskIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getServiceId().toString()));
        vehiclePlateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVehicle().getLicensePlate()));
        serviceTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        // Defer loadTasks until the scene is fully set up
        Platform.runLater(this::loadTasks);
    }

    private void loadTasks() {
        Stage stage = (Stage) tasksTable.getScene().getWindow();
        currentUser = (Users) stage.getUserData();
        tasksTable.setItems(FXCollections.observableArrayList(
            serviceRecordDAO.getAssignedTasks(currentUser.getUserId())));
    }

    @FXML
    private void showTasks() {
        loadTasks(); // Refresh the tasks table
    }

    @FXML
    private void updateStatus() {
        // Implement status update logic
    }

    @FXML
    private void viewVehicleDetails() {
        ServiceRecord selected = tasksTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Vehicle vehicle = selected.getVehicle();
            vehicleDetailsArea.setText(
                "Owner: " + vehicle.getOwner().getName() + "\n" +
                "Plate Number: " + vehicle.getLicensePlate() + "\n" +
                "Model: " + vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getYear() + ")\n" +
                "Last Service: " + new SimpleDateFormat("yyyy-MM-dd").format(selected.getServiceDate()) + "\n" +
                "Service History:\n" +
                "- " + selected.getDescription() + " (" + selected.getServiceDate() + ")"
            );
        }
    }

    @FXML
    private void showSearch() throws Exception {
        Stage stage = (Stage) tasksTable.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/search_vehicles.fxml"));
        stage.setScene(new Scene(root));
    }

    @FXML
    private void logout() throws Exception {
        Stage stage = (Stage) tasksTable.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        stage.setScene(new Scene(root));
    }
}