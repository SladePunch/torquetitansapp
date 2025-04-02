package com.vehiclemaintenance.controller;

import com.vehiclemaintenance.dao.CustomerDAO;
import com.vehiclemaintenance.dao.MechanicDAO;
import com.vehiclemaintenance.dao.ServiceRecordDAO;
import com.vehiclemaintenance.dao.VehicleDAO;
import com.vehiclemaintenance.entity.Customer;
import com.vehiclemaintenance.entity.Mechanic;
import com.vehiclemaintenance.entity.ServiceRecord;
import com.vehiclemaintenance.entity.Vehicle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox; // Added import for VBox
import javafx.stage.Stage;

import java.text.SimpleDateFormat; // Added import for SimpleDateFormat
import java.util.List;
import java.util.stream.Collectors;

public class AdminDashboardController {
    @FXML private Label totalServicesLabel;
    @FXML private Label mostRequestedLabel;
    @FXML private TableView<ServiceRecord> servicesTable;
    @FXML private TableColumn<ServiceRecord, String> serviceIdColumn;
    @FXML private TableColumn<ServiceRecord, String> vehiclePlateColumn;
    @FXML private TableColumn<ServiceRecord, String> serviceTypeColumn;
    @FXML private TableColumn<ServiceRecord, String> mechanicColumn;
    @FXML private TableColumn<ServiceRecord, String> dateColumn;
    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, String> customerIdColumn;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> contactInfoColumn;
    @FXML private TableColumn<Customer, String> vehiclesCountColumn;
    @FXML private TableView<Vehicle> vehiclesTable;
    @FXML private TableColumn<Vehicle, String> vehicleIdColumn;
    @FXML private TableColumn<Vehicle, String> makeColumn;
    @FXML private TableColumn<Vehicle, String> modelColumn;
    @FXML private TableColumn<Vehicle, String> yearColumn;
    @FXML private TableColumn<Vehicle, String> plateColumn;
    @FXML private TextArea vehicleDetailsArea;
    @FXML private ComboBox<Mechanic> mechanicComboBox;
    @FXML private TableView<ServiceRecord> currentJobsTable;
    @FXML private TableColumn<ServiceRecord, String> jobCustomerColumn;
    @FXML private TableColumn<ServiceRecord, String> jobPlateColumn;
    @FXML private TableColumn<ServiceRecord, String> jobStatusColumn;
    @FXML private TextField searchField;

    private ServiceRecordDAO serviceRecordDAO = new ServiceRecordDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private MechanicDAO mechanicDAO = new MechanicDAO();
    private ObservableList<Customer> allCustomers;
    private ObservableList<ServiceRecord> allServices;

    @FXML
    private void initialize() {
        // Recent Services Table
        serviceIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getServiceId().toString()));
        vehiclePlateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVehicle().getLicensePlate()));
        serviceTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        mechanicColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMechanic().getName()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getServiceDate().toString()));

        // Customers Table
        customerIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerId().toString()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        contactInfoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail() + ", " + cellData.getValue().getPhone()));
        vehiclesCountColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getVehicles().size())));

        // Vehicles Table
        vehicleIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVehicleId().toString()));
        makeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMake()));
        modelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModel()));
        yearColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getYear())));
        plateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLicensePlate()));

        // Current Jobs Table
        jobCustomerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVehicle().getOwner().getName()));
        jobPlateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVehicle().getLicensePlate()));
        jobStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        // Load mechanics into ComboBox
        mechanicComboBox.setItems(FXCollections.observableArrayList(mechanicDAO.getAllMechanics()));
        mechanicComboBox.setCellFactory(lv -> new ListCell<Mechanic>() {
            @Override
            protected void updateItem(Mechanic item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });
        mechanicComboBox.setButtonCell(new ListCell<Mechanic>() {
            @Override
            protected void updateItem(Mechanic item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });

        // Display vehicle details when a vehicle is selected
        vehiclesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayVehicleDetails(newSelection);
            } else {
                vehicleDetailsArea.clear();
            }
        });

        // Load vehicles when a customer is selected
        customersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                vehiclesTable.setItems(FXCollections.observableArrayList(newSelection.getVehicles()));
            } else {
                vehiclesTable.getItems().clear();
            }
        });

        loadServices();
        loadCustomers();
        loadCurrentJobs();
    }

    private void loadServices() {
        allServices = FXCollections.observableArrayList(serviceRecordDAO.getRecentServices());
        servicesTable.setItems(allServices);
        totalServicesLabel.setText("Total Services Completed: " + allServices.stream().filter(sr -> "Completed".equals(sr.getStatus())).count());
        mostRequestedLabel.setText("Most Requested Service: " + getMostRequestedService());
    }

    private String getMostRequestedService() {
        return allServices.stream()
            .collect(Collectors.groupingBy(ServiceRecord::getDescription, Collectors.counting()))
            .entrySet().stream()
            .max((e1, e2) -> Long.compare(e1.getValue(), e2.getValue()))
            .map(e -> e.getKey() + " (" + e.getValue() + " times)")
            .orElse("N/A");
    }

    private void loadCustomers() {
        allCustomers = FXCollections.observableArrayList(customerDAO.getAllCustomers());
        customersTable.setItems(allCustomers);
    }

    private void loadCurrentJobs() {
        List<ServiceRecord> currentJobs = allServices.stream()
            .filter(sr -> "Pending".equals(sr.getStatus()) || "Ongoing".equals(sr.getStatus()))
            .collect(Collectors.toList());
        currentJobsTable.setItems(FXCollections.observableArrayList(currentJobs));
    }

    @FXML
    private void searchCustomers() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            customersTable.setItems(allCustomers);
            return;
        }

        ObservableList<Customer> filteredCustomers = FXCollections.observableArrayList();
        for (Customer customer : allCustomers) {
            boolean matchesCustomer = customer.getName().toLowerCase().contains(searchText) ||
                customer.getEmail().toLowerCase().contains(searchText);
            boolean matchesVehicle = customer.getVehicles().stream()
                .anyMatch(v -> v.getLicensePlate().toLowerCase().contains(searchText));
            if (matchesCustomer || matchesVehicle) {
                filteredCustomers.add(customer);
            }
        }
        customersTable.setItems(filteredCustomers);
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        customersTable.setItems(allCustomers);
    }

    @FXML
    private void addCustomer() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Customer");
        dialog.setHeaderText("Enter customer details");
        dialog.setContentText("Name:");
        TextField nameField = dialog.getEditor();

        DialogPane dialogPane = dialog.getDialogPane();
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        VBox content = new VBox(10, nameField, emailField, phoneField);
        dialogPane.setContent(content);

        dialog.showAndWait().ifPresent(name -> {
            Customer customer = new Customer();
            customer.setName(name);
            customer.setEmail(emailField.getText());
            customer.setPhone(phoneField.getText());
            customerDAO.addCustomer(customer);
            loadCustomers();
        });
    }

    @FXML
    private void editCustomer() {
        Customer selected = customersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a customer to edit.");
            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getName());
        dialog.setTitle("Edit Customer");
        dialog.setHeaderText("Edit customer details");
        dialog.setContentText("Name:");
        TextField nameField = dialog.getEditor();

        DialogPane dialogPane = dialog.getDialogPane();
        TextField emailField = new TextField(selected.getEmail());
        emailField.setPromptText("Email");
        TextField phoneField = new TextField(selected.getPhone());
        phoneField.setPromptText("Phone");
        VBox content = new VBox(10, nameField, emailField, phoneField);
        dialogPane.setContent(content);

        dialog.showAndWait().ifPresent(name -> {
            selected.setName(name);
            selected.setEmail(emailField.getText());
            selected.setPhone(phoneField.getText());
            customerDAO.updateCustomer(selected);
            loadCustomers();
        });
    }

    @FXML
    private void deleteCustomer() {
        Customer selected = customersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a customer to delete.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + selected.getName() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                customerDAO.deleteCustomer(selected.getCustomerId());
                loadCustomers();
            }
        });
    }

    @FXML
    private void addVehicle() {
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a customer to add a vehicle for.");
            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Vehicle");
        dialog.setHeaderText("Enter vehicle details");
        dialog.setContentText("Make:");
        TextField makeField = dialog.getEditor();

        DialogPane dialogPane = dialog.getDialogPane();
        TextField modelField = new TextField();
        modelField.setPromptText("Model");
        TextField yearField = new TextField();
        yearField.setPromptText("Year");
        TextField plateField = new TextField();
        plateField.setPromptText("Plate Number");
        VBox content = new VBox(10, makeField, modelField, yearField, plateField);
        dialogPane.setContent(content);

        dialog.showAndWait().ifPresent(make -> {
            Vehicle vehicle = new Vehicle();
            vehicle.setMake(make);
            vehicle.setModel(modelField.getText());
            try {
                vehicle.setYear(Integer.parseInt(yearField.getText()));
            } catch (NumberFormatException e) {
                vehicle.setYear(0);
            }
            vehicle.setLicensePlate(plateField.getText());
            vehicle.setOwner(selectedCustomer);
            vehicleDAO.addVehicle(vehicle);
            loadCustomers();
            vehiclesTable.setItems(FXCollections.observableArrayList(selectedCustomer.getVehicles()));
        });
    }

    @FXML
    private void editVehicle() {
        Vehicle selected = vehiclesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a vehicle to edit.");
            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getMake());
        dialog.setTitle("Edit Vehicle");
        dialog.setHeaderText("Edit vehicle details");
        dialog.setContentText("Make:");
        TextField makeField = dialog.getEditor();

        DialogPane dialogPane = dialog.getDialogPane();
        TextField modelField = new TextField(selected.getModel());
        modelField.setPromptText("Model");
        TextField yearField = new TextField(String.valueOf(selected.getYear()));
        yearField.setPromptText("Year");
        TextField plateField = new TextField(selected.getLicensePlate());
        plateField.setPromptText("Plate Number");
        VBox content = new VBox(10, makeField, modelField, yearField, plateField);
        dialogPane.setContent(content);

        dialog.showAndWait().ifPresent(make -> {
            selected.setMake(make);
            selected.setModel(modelField.getText());
            try {
                selected.setYear(Integer.parseInt(yearField.getText()));
            } catch (NumberFormatException e) {
                selected.setYear(0);
            }
            selected.setLicensePlate(plateField.getText());
            vehicleDAO.updateVehicle(selected);
            loadCustomers();
            Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
            if (selectedCustomer != null) {
                vehiclesTable.setItems(FXCollections.observableArrayList(selectedCustomer.getVehicles()));
            }
        });
    }

    @FXML
    private void deleteVehicle() {
        Vehicle selected = vehiclesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a vehicle to delete.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this vehicle?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                vehicleDAO.deleteVehicle(selected.getVehicleId());
                loadCustomers();
                Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
                if (selectedCustomer != null) {
                    vehiclesTable.setItems(FXCollections.observableArrayList(selectedCustomer.getVehicles()));
                }
            }
        });
    }

    @FXML
    private void assignMechanic() {
        Vehicle selectedVehicle = vehiclesTable.getSelectionModel().getSelectedItem();
        Mechanic selectedMechanic = mechanicComboBox.getSelectionModel().getSelectedItem();
        if (selectedVehicle == null || selectedMechanic == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a vehicle and a mechanic.");
            alert.showAndWait();
            return;
        }

        // Assign mechanic to a new service record for the vehicle
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Service Record");
        dialog.setHeaderText("Enter service details");
        dialog.setContentText("Service Type:");
        TextField serviceTypeField = dialog.getEditor();

        DialogPane dialogPane = dialog.getDialogPane();
        TextField dateField = new TextField("2025-04-01");
        dateField.setPromptText("Service Date (YYYY-MM-DD)");
        ChoiceBox<String> statusChoice = new ChoiceBox<>(FXCollections.observableArrayList("Pending", "Ongoing"));
        statusChoice.setValue("Pending");
        VBox content = new VBox(10, serviceTypeField, dateField, statusChoice);
        dialogPane.setContent(content);

        dialog.showAndWait().ifPresent(serviceType -> {
            ServiceRecord serviceRecord = new ServiceRecord();
            serviceRecord.setVehicle(selectedVehicle);
            serviceRecord.setMechanic(selectedMechanic);
            serviceRecord.setDescription(serviceType);
            try {
                serviceRecord.setServiceDate(new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText()).getTime()));
            } catch (Exception e) {
                serviceRecord.setServiceDate(new java.sql.Date(System.currentTimeMillis()));
            }
            serviceRecord.setStatus(statusChoice.getValue());
            serviceRecordDAO.addServiceRecord(serviceRecord);
            loadServices();
            loadCurrentJobs();
            displayVehicleDetails(selectedVehicle);
        });
    }

    private void displayVehicleDetails(Vehicle vehicle) {
        StringBuilder details = new StringBuilder();
        details.append("Vehicle: ").append(vehicle.getMake()).append(" ").append(vehicle.getModel())
               .append(" (").append(vehicle.getYear()).append(")\n");
        details.append("Plate Number: ").append(vehicle.getLicensePlate()).append("\n");
        details.append("Owner: ").append(vehicle.getOwner().getName()).append("\n");
        details.append("Service History:\n");
        List<ServiceRecord> serviceRecords = serviceRecordDAO.getServiceRecordsByVehicle(vehicle.getVehicleId());
        if (serviceRecords.isEmpty()) {
            details.append("No service history available.");
        } else {
            for (ServiceRecord sr : serviceRecords) {
                details.append("- ").append(sr.getDescription()).append(" (")
                       .append(sr.getServiceDate()).append(", ").append(sr.getStatus())
                       .append(", Mechanic: ").append(sr.getMechanic().getName()).append(")\n");
            }
        }
        vehicleDetailsArea.setText(details.toString());
    }

    @FXML
    private void logout() throws Exception {
        Stage stage = (Stage) servicesTable.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("Login");
    }
}