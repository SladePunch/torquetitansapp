package com.vehiclemaintenance.controller;

import com.vehiclemaintenance.dao.CustomerDAO;
import com.vehiclemaintenance.dao.ServiceRecordDAO;
import com.vehiclemaintenance.entity.Customers;
import com.vehiclemaintenance.entity.ServiceRecord;
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

public class AdminDashboardController {
    @FXML private Label totalServicesLabel;
    @FXML private Label mostRequestedLabel;
    @FXML private TableView<ServiceRecord> servicesTable;
    @FXML private TableColumn<ServiceRecord, String> serviceIdColumn;
    @FXML private TableColumn<ServiceRecord, String> vehiclePlateColumn;
    @FXML private TableColumn<ServiceRecord, String> serviceTypeColumn;
    @FXML private TableColumn<ServiceRecord, String> mechanicColumn;
    @FXML private TableColumn<ServiceRecord, String> dateColumn;
    @FXML private TableView<Customers> customersTable;
    @FXML private TableColumn<Customers, String> customerIdColumn;
    @FXML private TableColumn<Customers, String> nameColumn;
    @FXML private TableColumn<Customers, String> contactInfoColumn;
    @FXML private TableColumn<Customers, String> vehiclesCountColumn;
    @FXML private TextArea customerVehicleDetailsArea;

    private ServiceRecordDAO serviceRecordDAO = new ServiceRecordDAO();
    private CustomerDAO customerDAO = new CustomerDAO();

    @FXML
    private void initialize() {
        // Services Table
        serviceIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getServiceId().toString()));
        vehiclePlateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVehicle().getLicensePlate()));
        serviceTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        mechanicColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMechanic().getName()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            new SimpleDateFormat("yyyy-MM-dd").format(cellData.getValue().getServiceDate())));

        // Customers Table
        customerIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerId().toString()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        contactInfoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        vehiclesCountColumn.setCellValueFactory(cellData -> new SimpleStringProperty("2")); // Placeholder

        loadData();
    }

    private void loadData() {
        servicesTable.setItems(FXCollections.observableArrayList(serviceRecordDAO.getRecentServices()));
        customersTable.setItems(FXCollections.observableArrayList(customerDAO.getAllCustomers()));
        totalServicesLabel.setText("Total Services Completed: 152"); // Placeholder
        mostRequestedLabel.setText("Most Requested Service: Oil Change (47 times)"); // Placeholder
    }

    @FXML
    private void addCustomer() {
        // Implement add customer logic
    }

    @FXML
    private void editCustomer() {
        // Implement edit customer logic
    }

    @FXML
    private void deleteCustomer() {
        // Implement delete customer logic
    }

    @FXML
    private void showJobs() {
        // Already showing jobs
    }

    @FXML
    private void showCustomers() {
        // Already showing customers
    }

    @FXML
    private void showVehicles() {
        // Implement manage vehicles logic
    }

    @FXML
    private void logout() throws Exception {
        Stage stage = (Stage) servicesTable.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        stage.setScene(new Scene(root));
    }
}