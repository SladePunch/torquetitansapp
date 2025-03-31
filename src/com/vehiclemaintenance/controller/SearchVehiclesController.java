package com.vehiclemaintenance.controller;

import com.vehiclemaintenance.dao.VehicleDAO;
import com.vehiclemaintenance.entity.Vehicle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;

public class SearchVehiclesController {
    @FXML private TextField searchField;
    @FXML private TableView<Vehicle> vehiclesTable;
    @FXML private TableColumn<Vehicle, String> customerColumn;
    @FXML private TableColumn<Vehicle, String> vehiclePlateColumn;
    @FXML private TableColumn<Vehicle, String> modelColumn;
    @FXML private TableColumn<Vehicle, String> lastServiceColumn;

    private VehicleDAO vehicleDAO = new VehicleDAO();

    @FXML
    private void initialize() {
        customerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOwner().getName()));
        vehiclePlateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLicensePlate()));
        modelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getMake() + " " + cellData.getValue().getModel()));
        lastServiceColumn.setCellValueFactory(cellData -> new SimpleStringProperty("2023-03-01")); // Placeholder
    }

    @FXML
    private void searchVehicles() {
        String customerName = searchField.getText();
        vehiclesTable.setItems(FXCollections.observableArrayList(
            vehicleDAO.searchVehiclesByCustomerName(customerName)));
    }

    @FXML
    private void viewDetails() {
        // Implement view details logic
    }

    @FXML
    private void goBack() throws Exception {
        Stage stage = (Stage) vehiclesTable.getScene().getWindow();
        Users user = (Users) stage.getUserData();
        String fxmlFile = user.getRole().getRoleName().equals("Admin") ? "/admin_dashboard.fxml" : "/mechanic_dashboard.fxml";
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        stage.setScene(new Scene(root));
    }
}