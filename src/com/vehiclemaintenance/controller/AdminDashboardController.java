package com.vehiclemaintenance.controller;

import com.vehiclemaintenance.HibernateUtil;
import com.vehiclemaintenance.dao.CustomerDAO;
import com.vehiclemaintenance.dao.MechanicDAO;
import com.vehiclemaintenance.dao.ServiceRecordDAO;
import com.vehiclemaintenance.dao.VehicleDAO;
import com.vehiclemaintenance.entity.Customer;
import com.vehiclemaintenance.entity.Mechanic;
import com.vehiclemaintenance.entity.ServiceRecord;
import com.vehiclemaintenance.entity.Users;
import com.vehiclemaintenance.entity.Vehicle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.query.Query;

public class AdminDashboardController {
    @FXML private Label totalServicesLabel;
    @FXML private Label mostRequestedLabel;
    @FXML private TableView<ServiceRecord> servicesTable;
    @FXML private TableColumn<ServiceRecord, String> serviceIdColumn;
    @FXML private TableColumn<ServiceRecord, String> vehiclePlateColumn;
    @FXML private TableColumn<ServiceRecord, String> serviceTypeColumn;
	@FXML private TableColumn<ServiceRecord, String> jobServiceTypeColumn;
    @FXML private TableColumn<ServiceRecord, String> mechanicColumn;
    @FXML private TableColumn<ServiceRecord, String> dateColumn;
    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
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
    @FXML private TableColumn<ServiceRecord, String> jobMechanicColumn;
    @FXML private TextField searchField;

    private ServiceRecordDAO serviceRecordDAO = new ServiceRecordDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private MechanicDAO mechanicDAO = new MechanicDAO();
    private ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private ObservableList<ServiceRecord> allServices;
    private Users currentUser;
    private Customer selectedCustomer;

    public void setCurrentUser(Users user) {
        this.currentUser = user;
        checkUserRole();
    }

    private void checkUserRole() {
        if (currentUser == null) {
            System.err.println("Current user is null. Cannot load admin dashboard.");
            showErrorAndLogout("User session not found. Please log in again.");
            return;
        }

        Long userRoleId = currentUser.getRoleId();
        if (userRoleId == null || userRoleId != 1) {
            System.err.println("Current user is not an admin. RoleID: " + userRoleId);
            showErrorAndLogout("You are not authorized to access this dashboard.");
        }
    }

    private void showErrorAndLogout(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
        logout();
    }

    @FXML
    private void initialize() {
        // Recent Services Table with null checks
        serviceIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getServiceId() != null ? cellData.getValue().getServiceId().toString() : ""));
        vehiclePlateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVehicle() != null ? cellData.getValue().getVehicle().getLicensePlate() : "N/A"));
        serviceTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription() != null ? cellData.getValue().getDescription() : "N/A"));
        mechanicColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMechanic() != null ? cellData.getValue().getMechanic().getName() : "N/A"));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getServiceDate() != null ? cellData.getValue().getServiceDate().toString() : "N/A"));

        // Customers Table
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        vehiclesCountColumn.setCellValueFactory(cellData -> {
            List<Vehicle> vehicles = cellData.getValue().getVehicles();
            return new SimpleStringProperty(vehicles != null ? String.valueOf(vehicles.size()) : "0");
        });

        // Bind allCustomers to customersTable
        customersTable.setItems(allCustomers);

        // Vehicles Table
        vehicleIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVehicleId() != null ? cellData.getValue().getVehicleId().toString() : ""));
        makeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMake() != null ? cellData.getValue().getMake() : "N/A"));
        modelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModel() != null ? cellData.getValue().getModel() : "N/A"));
        yearColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getYear())));
        plateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLicensePlate() != null ? cellData.getValue().getLicensePlate() : "N/A"));

        // Current Jobs Table with null checks
        jobCustomerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getVehicle() != null && cellData.getValue().getVehicle().getOwner() != null ?
            cellData.getValue().getVehicle().getOwner().getName() : "N/A"));
        jobPlateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getVehicle() != null ? cellData.getValue().getVehicle().getLicensePlate() : "N/A"));
        jobServiceTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDescription() != null ? cellData.getValue().getDescription() : "N/A"));
        jobStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getStatus() != null ? cellData.getValue().getStatus() : "N/A"));
        jobMechanicColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getMechanic() != null ? cellData.getValue().getMechanic().getName() : "N/A"));

     // Load mechanics into ComboBox
        List<Mechanic> mechanics = mechanicDAO.getAllMechanics();
        System.out.println("Loaded " + mechanics.size() + " mechanics for ComboBox");
        if (mechanics.isEmpty()) {
            System.out.println("No mechanics loaded. Check MechanicDAO.getAllMechanics() and database data.");
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Long> countQuery = session.createQuery("SELECT COUNT(*) FROM Mechanic", Long.class);
                Long count = countQuery.uniqueResult();
                System.out.println("Number of mechanics in database: " + count);
                if (count == 0) {
                    System.out.println("Adding test mechanics...");
                    Mechanic mechanic1 = new Mechanic();
                    mechanic1.setName("Mike Smith");
                    mechanic1.setContactInfo("mike.smith@email.com");
                    Mechanic mechanic2 = new Mechanic();
                    mechanic2.setName("Sarah Johnson");
                    mechanic2.setContactInfo("sarah.johnson@email.com");
                    mechanicDAO.addMechanic(mechanic1);
                    mechanicDAO.addMechanic(mechanic2);
                    System.out.println("Test mechanics added. Reloading mechanics...");
                    mechanics = mechanicDAO.getAllMechanics();
                    System.out.println("Loaded " + mechanics.size() + " mechanics after adding test data");
                }
            } catch (Exception e) {
                System.err.println("Error checking mechanic count: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            mechanics.forEach(mechanic -> System.out.println("Mechanic: " + mechanic.getName() + ", ID: " + mechanic.getMechanicId()));
        }
        mechanicComboBox.setItems(FXCollections.observableArrayList(mechanics));
        mechanicComboBox.setCellFactory(lv -> new ListCell<Mechanic>() {
            @Override
            protected void updateItem(Mechanic item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item != null ? item.getName() : "N/A"));
            }
        });
        mechanicComboBox.setButtonCell(new ListCell<Mechanic>() {
            @Override
            protected void updateItem(Mechanic item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item != null ? item.getName() : "N/A"));
            }
        });
        mechanicComboBox.setOnMouseClicked(event -> {
            System.out.println("mechanicComboBox clicked! Items: " + mechanicComboBox.getItems().size());
            mechanicComboBox.show();
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
                selectedCustomer = newSelection;
                loadVehiclesForCustomer(newSelection);
            } else {
                vehiclesTable.getItems().clear();
            }
        });

        // Load initial data with try-catch for debugging
        try {
            System.out.println("Starting to load services...");
            loadServices();
            System.out.println("Starting to load current jobs...");
            loadCurrentJobs();
            System.out.println("Starting to load customers...");
            loadCustomers();
        } catch (Exception e) {
            System.err.println("Error during initialization: " + e.getMessage());
            e.printStackTrace();
        }

        searchField.setOnMouseClicked(event -> {
            searchField.requestFocus();
            searchField.positionCaret(searchField.getText().length()); // Place cursor at the end
        });

        // Existing real-time search listener (assumed to be present)
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            searchCustomers();
        });
    }
    
    private void filterCustomers(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            allCustomers.setAll(customerDAO.getAllCustomers());
            return;
        }

        List<Customer> filteredList = customerDAO.getAllCustomers().stream()
            .filter(customer ->
                customer.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                customer.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                customer.getPhone().toLowerCase().contains(searchText.toLowerCase()))
            .collect(Collectors.toList());
        allCustomers.setAll(filteredList);
        customersTable.refresh();
    }
    
    private void loadVehiclesForCustomer(Customer customer) {
        vehiclesTable.getItems().clear();
        vehiclesTable.getItems().addAll(vehicleDAO.getVehiclesByCustomer(customer));
    }

    private void loadServices() {
        allServices = FXCollections.observableArrayList(serviceRecordDAO.getRecentServices());
        System.out.println("Loaded " + allServices.size() + " recent services");
        if (allServices.isEmpty()) {
            System.out.println("No services loaded. Check ServiceRecordDAO.getRecentServices() and database data.");
        } else {
            allServices.forEach(sr -> System.out.println("Service: " + sr.getDescription() + ", Status: " + sr.getStatus() +
                ", Vehicle: " + (sr.getVehicle() != null ? sr.getVehicle().getLicensePlate() : "N/A") +
                ", Mechanic: " + (sr.getMechanic() != null ? sr.getMechanic().getName() : "N/A")));
        }
        // Filter for completed services only
        ObservableList<ServiceRecord> completedServices = allServices.filtered(sr -> "Completed".equals(sr.getStatus()));
        System.out.println("Filtered to " + completedServices.size() + " completed services");
        servicesTable.setItems(completedServices);
        totalServicesLabel.setText("Total Services Completed: " + completedServices.size());
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

    private void loadCurrentJobs() {
        // Filter for pending and ongoing services
        List<ServiceRecord> currentJobs = allServices.stream()
            .filter(sr -> "Pending".equals(sr.getStatus()) || "Ongoing".equals(sr.getStatus()))
            .collect(Collectors.toList());
        System.out.println("Loaded " + currentJobs.size() + " current jobs (Pending/Ongoing)");
        if (currentJobs.isEmpty()) {
            System.out.println("No current jobs loaded. Ensure there are Pending or Ongoing services.");
        } else {
            currentJobs.forEach(job -> System.out.println("Job: " + job.getDescription() + ", Status: " + job.getStatus() +
                ", Vehicle: " + (job.getVehicle() != null ? job.getVehicle().getLicensePlate() : "N/A") +
                ", Customer: " + (job.getVehicle() != null && job.getVehicle().getOwner() != null ? job.getVehicle().getOwner().getName() : "N/A")));
        }
        ObservableList<ServiceRecord> observableJobs = FXCollections.observableArrayList(currentJobs);
        currentJobsTable.setItems(observableJobs);
        currentJobsTable.refresh(); // Force table refresh
    }

    private void loadCustomers() {
        System.out.println("Attempting to fetch customers from CustomerDAO...");
        List<Customer> fetchedCustomers = customerDAO.getAllCustomers();
        System.out.println("Fetched " + fetchedCustomers.size() + " customers from DAO");
        // Remove duplicates based on customerId
        List<Customer> uniqueCustomers = fetchedCustomers.stream()
            .distinct()
            .collect(Collectors.toList());
        allCustomers.setAll(uniqueCustomers);
        System.out.println("Loaded " + allCustomers.size() + " customers into allCustomers");
        allCustomers.forEach(c -> System.out.println("Customer: " + c.getName() + ", ID: " + c.getCustomerId() + ", Vehicles: " + (c.getVehicles() != null ? c.getVehicles().size() : 0)));
        if (allCustomers.isEmpty()) {
            System.out.println("No customers loaded. Check CustomerDAO.getAllCustomers() and database data.");
        }
        customersTable.refresh(); // Force table refresh
    }

    @FXML
    private void searchCustomers() {
        String searchText = searchField.getText().trim().toLowerCase();
        List<Customer> customers = customerDAO.getAllCustomers().stream()
            .distinct()
            .collect(Collectors.toList());
        if (searchText.isEmpty()) {
            System.out.println("Search text is empty. Showing " + customers.size() + " unique customers");
            customersTable.setItems(FXCollections.observableArrayList(customers));
        } else {
            List<Customer> filteredCustomers = customers.stream()
                .filter(customer -> {
                    boolean matches = customer.getName().toLowerCase().contains(searchText) ||
                                     customer.getEmail().toLowerCase().contains(searchText) ||
                                     customer.getPhone().toLowerCase().contains(searchText);
                    if (matches) {
                        System.out.println("Customer matched: " + customer.getName() +
                                          ", Name: " + customer.getName() +
                                          ", Email: " + customer.getEmail() +
                                          ", Phone: " + customer.getPhone());
                    }
                    return matches;
                })
                .collect(Collectors.toList());
            System.out.println("Search text: " + searchText + ", Found " + filteredCustomers.size() + " matching customers");
            customersTable.setItems(FXCollections.observableArrayList(filteredCustomers));
        }
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        allCustomers.setAll(customerDAO.getAllCustomers());
        customersTable.refresh();
    }

    @FXML
    private void addCustomer() {
        // Create a dialog to collect customer details
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Customer");
        dialog.setHeaderText("Enter the details for the new customer");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the customer details form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable Add button based on input
        Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        // Add validation to ensure all fields are filled
        nameField.textProperty().addListener((obs, oldValue, newValue) -> {
            addButton.setDisable(
                nameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                phoneField.getText().trim().isEmpty()
            );
        });

        emailField.textProperty().addListener((obs, oldValue, newValue) -> {
            addButton.setDisable(
                nameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                phoneField.getText().trim().isEmpty()
            );
        });

        phoneField.textProperty().addListener((obs, oldValue, newValue) -> {
            addButton.setDisable(
                nameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                phoneField.getText().trim().isEmpty()
            );
        });

        // Show the dialog and process the result
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == addButtonType) {
            try {
                Customer newCustomer = new Customer();
                newCustomer.setName(nameField.getText().trim());
                newCustomer.setEmail(emailField.getText().trim());
                newCustomer.setPhone(phoneField.getText().trim());

                customerDAO.addCustomer(newCustomer);

                // Refresh the customer list via allCustomers
                List<Customer> updatedCustomers = customerDAO.getAllCustomers();
                List<Customer> uniqueCustomers = updatedCustomers.stream()
                    .distinct()
                    .collect(Collectors.toList());
                allCustomers.setAll(uniqueCustomers);

                // Find the newly added customer in the updated list and select it
                Customer customerToSelect = allCustomers.stream()
                    .filter(c -> c.getName().equals(newCustomer.getName()) &&
                                 c.getEmail().equals(newCustomer.getEmail()) &&
                                 c.getPhone().equals(newCustomer.getPhone()))
                    .findFirst()
                    .orElse(newCustomer);

                customersTable.getSelectionModel().select(customerToSelect);
                customersTable.refresh();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to add customer: " + e.getMessage());
                alert.showAndWait();
            }
        }
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
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a customer to delete.");
            alert.showAndWait();
            return;
        }

        System.out.println("Selected customer: " + selectedCustomer.getName() + ", Vehicles: " + (selectedCustomer.getVehicles() != null ? selectedCustomer.getVehicles().size() : 0));

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + selectedCustomer.getName() + "? This will also delete their vehicles and associated service records.");
        confirmation.setHeaderText("Confirm Deletion");
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK) {
            return;
        }

        try {
            customerDAO.deleteCustomer(selectedCustomer.getCustomerId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Customer deleted successfully.");
            alert.showAndWait();
            loadCustomers();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to delete customer: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void addVehicle() {
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a customer to add a vehicle.");
            alert.showAndWait();
            return;
        }

        // Create a dialog to collect vehicle details
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Vehicle");
        dialog.setHeaderText("Enter the details for the new vehicle");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the vehicle details form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField makeField = new TextField();
        makeField.setPromptText("Make");
        TextField modelField = new TextField();
        modelField.setPromptText("Model");
        TextField yearField = new TextField();
        yearField.setPromptText("Year");
        TextField licensePlateField = new TextField();
        licensePlateField.setPromptText("License Plate");

        grid.add(new Label("Make:"), 0, 0);
        grid.add(makeField, 1, 0);
        grid.add(new Label("Model:"), 0, 1);
        grid.add(modelField, 1, 1);
        grid.add(new Label("Year:"), 0, 2);
        grid.add(yearField, 1, 2);
        grid.add(new Label("License Plate:"), 0, 3);
        grid.add(licensePlateField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable Add button based on input
        Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        // Add validation to ensure all fields are filled
        licensePlateField.textProperty().addListener((obs, oldValue, newValue) -> {
            addButton.setDisable(
                makeField.getText().trim().isEmpty() ||
                modelField.getText().trim().isEmpty() ||
                yearField.getText().trim().isEmpty() ||
                licensePlateField.getText().trim().isEmpty()
            );
        });

        makeField.textProperty().addListener((obs, oldValue, newValue) -> {
            addButton.setDisable(
                makeField.getText().trim().isEmpty() ||
                modelField.getText().trim().isEmpty() ||
                yearField.getText().trim().isEmpty() ||
                licensePlateField.getText().trim().isEmpty()
            );
        });

        modelField.textProperty().addListener((obs, oldValue, newValue) -> {
            addButton.setDisable(
                makeField.getText().trim().isEmpty() ||
                modelField.getText().trim().isEmpty() ||
                yearField.getText().trim().isEmpty() ||
                licensePlateField.getText().trim().isEmpty()
            );
        });

        yearField.textProperty().addListener((obs, oldValue, newValue) -> {
            addButton.setDisable(
                makeField.getText().trim().isEmpty() ||
                modelField.getText().trim().isEmpty() ||
                yearField.getText().trim().isEmpty() ||
                licensePlateField.getText().trim().isEmpty()
            );
        });

        // Show the dialog and process the result
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == addButtonType) {
            try {
                // Validate fields again (just to be safe)
                String make = makeField.getText().trim();
                String model = modelField.getText().trim();
                String yearText = yearField.getText().trim();
                String licensePlate = licensePlateField.getText().trim();

                if (make.isEmpty() || model.isEmpty() || yearText.isEmpty() || licensePlate.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "All fields are required.");
                    alert.showAndWait();
                    return;
                }

                Vehicle newVehicle = new Vehicle();
                newVehicle.setOwner(selectedCustomer);
                newVehicle.setMake(make);
                newVehicle.setModel(model);
                newVehicle.setLicensePlate(licensePlate);

                // Validate and set the year
                try {
                    newVehicle.setYear(Integer.parseInt(yearText));
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Year must be a valid number.");
                    alert.showAndWait();
                    return;
                }

                vehicleDAO.addVehicle(newVehicle);

                // Refresh the vehicle table without losing customer selection
                loadVehiclesForCustomer(selectedCustomer);
                customersTable.getSelectionModel().select(selectedCustomer); // Reselect the customer

                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Vehicle added successfully!");
                successAlert.showAndWait();
            } catch (IllegalArgumentException e) {
                // Handle validation errors from VehicleDAO
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            } catch (Exception e) {
                // Handle other unexpected errors
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to add vehicle: " + e.getMessage());
                alert.showAndWait();
                e.printStackTrace(); // Log for debugging
            }
        }
    }

    @FXML
    private void editVehicle() {
        Vehicle selectedVehicle = vehiclesTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a vehicle to edit.");
            alert.showAndWait();
            return;
        }

        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a customer.");
            alert.showAndWait();
            return;
        }

        // Create a dialog to edit vehicle details
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Vehicle");
        dialog.setHeaderText("Edit details for vehicle: " + selectedVehicle.getLicensePlate());

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the vehicle details form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField makeField = new TextField(selectedVehicle.getMake() != null ? selectedVehicle.getMake() : "");
        makeField.setPromptText("Make");
        TextField modelField = new TextField(selectedVehicle.getModel() != null ? selectedVehicle.getModel() : "");
        modelField.setPromptText("Model");
        TextField yearField = new TextField(String.valueOf(selectedVehicle.getYear()));
        yearField.setPromptText("Year");
        TextField licensePlateField = new TextField(selectedVehicle.getLicensePlate() != null ? selectedVehicle.getLicensePlate() : "");
        licensePlateField.setPromptText("License Plate");

        grid.add(new Label("Make:"), 0, 0);
        grid.add(makeField, 1, 0);
        grid.add(new Label("Model:"), 0, 1);
        grid.add(modelField, 1, 1);
        grid.add(new Label("Year:"), 0, 2);
        grid.add(yearField, 1, 2);
        grid.add(new Label("License Plate:"), 0, 3);
        grid.add(licensePlateField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable Save button based on input
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Add validation to ensure all fields are filled
        makeField.textProperty().addListener((obs, oldValue, newValue) -> {
            saveButton.setDisable(
                makeField.getText().trim().isEmpty() ||
                modelField.getText().trim().isEmpty() ||
                yearField.getText().trim().isEmpty() ||
                licensePlateField.getText().trim().isEmpty()
            );
        });

        modelField.textProperty().addListener((obs, oldValue, newValue) -> {
            saveButton.setDisable(
                makeField.getText().trim().isEmpty() ||
                modelField.getText().trim().isEmpty() ||
                yearField.getText().trim().isEmpty() ||
                licensePlateField.getText().trim().isEmpty()
            );
        });

        yearField.textProperty().addListener((obs, oldValue, newValue) -> {
            saveButton.setDisable(
                makeField.getText().trim().isEmpty() ||
                modelField.getText().trim().isEmpty() ||
                yearField.getText().trim().isEmpty() ||
                licensePlateField.getText().trim().isEmpty()
            );
        });

        licensePlateField.textProperty().addListener((obs, oldValue, newValue) -> {
            saveButton.setDisable(
                makeField.getText().trim().isEmpty() ||
                modelField.getText().trim().isEmpty() ||
                yearField.getText().trim().isEmpty() ||
                licensePlateField.getText().trim().isEmpty()
            );
        });

        // Show the dialog and process the result
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveButtonType) {
            try {
                // Validate fields
                String make = makeField.getText().trim();
                String model = modelField.getText().trim();
                String yearText = yearField.getText().trim();
                String licensePlate = licensePlateField.getText().trim();

                if (make.isEmpty() || model.isEmpty() || yearText.isEmpty() || licensePlate.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "All fields are required.");
                    alert.showAndWait();
                    return;
                }

                // Update the vehicle
                selectedVehicle.setMake(make);
                selectedVehicle.setModel(model);
                selectedVehicle.setLicensePlate(licensePlate);

                // Validate and set the year
                try {
                    selectedVehicle.setYear(Integer.parseInt(yearText));
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Year must be a valid number.");
                    alert.showAndWait();
                    return;
                }

                // Persist the changes
                vehicleDAO.updateVehicle(selectedVehicle);

                // Refresh the vehicle table without losing customer selection
                loadVehiclesForCustomer(selectedCustomer);
                customersTable.getSelectionModel().select(selectedCustomer); // Reselect the customer

                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Vehicle updated successfully!");
                successAlert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update vehicle: " + e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
            }
        }
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

        // Create a dialog to collect the service type
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Service Record");
        dialog.setHeaderText("Assign a job to " + selectedMechanic.getName() + " for vehicle: " + selectedVehicle.getLicensePlate());
        dialog.setContentText("Service Type:");
        TextField serviceTypeField = dialog.getEditor();

        // Show the dialog and process the result
        dialog.showAndWait().ifPresent(serviceType -> {
            try {
                if (serviceType == null || serviceType.trim().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Service type cannot be empty.");
                    alert.showAndWait();
                    return;
                }

                ServiceRecord serviceRecord = new ServiceRecord();
                serviceRecord.setVehicle(selectedVehicle);
                serviceRecord.setMechanic(selectedMechanic);
                serviceRecord.setDescription(serviceType.trim());
                serviceRecord.setServiceDate(new java.sql.Date(System.currentTimeMillis()));
                serviceRecord.setStatus("Pending");

                serviceRecordDAO.addServiceRecord(serviceRecord);

                // Refresh the tables
                loadServices();
                loadCurrentJobs();
                displayVehicleDetails(selectedVehicle);

                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Job assigned successfully!");
                successAlert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to assign mechanic: " + e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
            }
        });
    }

    private void displayVehicleDetails(Vehicle vehicle) {
        StringBuilder details = new StringBuilder();
        details.append("Vehicle: ").append(vehicle.getMake() != null ? vehicle.getMake() : "N/A").append(" ")
               .append(vehicle.getModel() != null ? vehicle.getModel() : "N/A")
               .append(" (").append(vehicle.getYear()).append(")\n");
        details.append("Plate Number: ").append(vehicle.getLicensePlate() != null ? vehicle.getLicensePlate() : "N/A").append("\n");
        details.append("Owner: ").append(vehicle.getOwner() != null ? vehicle.getOwner().getName() : "N/A").append("\n");
        details.append("Service History:\n");
        List<ServiceRecord> serviceRecords = serviceRecordDAO.getServiceRecordsByVehicle(vehicle.getVehicleId());
        if (serviceRecords.isEmpty()) {
            details.append("No service history available.");
        } else {
            for (ServiceRecord sr : serviceRecords) {
                details.append("- ").append(sr.getDescription() != null ? sr.getDescription() : "N/A").append(" (")
                       .append(sr.getServiceDate() != null ? sr.getServiceDate() : "N/A").append(", ")
                       .append(sr.getStatus() != null ? sr.getStatus() : "N/A")
                       .append(", Mechanic: ").append(sr.getMechanic() != null ? sr.getMechanic().getName() : "N/A").append(")\n");
            }
        }
        vehicleDetailsArea.setText(details.toString());
    }

    @FXML
    private void refreshServicesAndJobs() {
        loadServices();
        loadCurrentJobs();
        loadCustomers(); // Also refresh customers to ensure data consistency
    }

    @FXML
    private void logout() {
        try {
            Stage stage = (Stage) servicesTable.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            stage.setScene(new Scene(root, 500, 300));
            stage.setTitle("Login");
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
}