package com.vehiclemaintenance.controller;

import com.vehiclemaintenance.HibernateUtil;
import com.vehiclemaintenance.dao.MechanicDAO;
import com.vehiclemaintenance.dao.ServiceRecordDAO;
import com.vehiclemaintenance.dao.UserMechanicMappingDAO;
import com.vehiclemaintenance.entity.Mechanic;
import com.vehiclemaintenance.entity.ServiceRecord;
import com.vehiclemaintenance.entity.Users;
import com.vehiclemaintenance.entity.Vehicle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;

public class MechanicDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private TableView<ServiceRecord> tasksTable;
    @FXML private TableColumn<ServiceRecord, String> descriptionColumn;
    @FXML private TableColumn<ServiceRecord, String> vehicleColumn;
    @FXML private TableColumn<ServiceRecord, String> customerColumn;
    @FXML private TableColumn<ServiceRecord, String> serviceDateColumn;
    @FXML private TableColumn<ServiceRecord, String> statusColumn;

    // New TableView for Completed Tasks
    @FXML private TableView<ServiceRecord> completedTasksTable;
    @FXML private TableColumn<ServiceRecord, String> completedDescriptionColumn;
    @FXML private TableColumn<ServiceRecord, String> completedVehicleColumn;
    @FXML private TableColumn<ServiceRecord, String> completedCustomerColumn;
    @FXML private TableColumn<ServiceRecord, String> completedServiceDateColumn;
    @FXML private TableColumn<ServiceRecord, String> completedStatusColumn;

    private Mechanic mechanic;
    private Users currentUser;
    private ServiceRecordDAO serviceRecordDAO = new ServiceRecordDAO();
    @FXML private Button logoutButton;

    @FXML
    private void initialize() {
        // Initialize columns for Pending/Ongoing Tasks
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        vehicleColumn.setCellValueFactory(cellData -> {
            Vehicle vehicle = cellData.getValue().getVehicle();
            return new javafx.beans.property.SimpleStringProperty(
                vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getLicensePlate() + ")"
            );
        });
        customerColumn.setCellValueFactory(cellData -> {
            Vehicle vehicle = cellData.getValue().getVehicle();
            return new javafx.beans.property.SimpleStringProperty(vehicle.getOwner().getName());
        });
        serviceDateColumn.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Initialize columns for Completed Tasks
        completedDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        completedVehicleColumn.setCellValueFactory(cellData -> {
            Vehicle vehicle = cellData.getValue().getVehicle();
            return new javafx.beans.property.SimpleStringProperty(
                vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getLicensePlate() + ")"
            );
        });
        completedCustomerColumn.setCellValueFactory(cellData -> {
            Vehicle vehicle = cellData.getValue().getVehicle();
            return new javafx.beans.property.SimpleStringProperty(vehicle.getOwner().getName());
        });
        completedServiceDateColumn.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));
        completedStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    public void setCurrentUser(Users user) {
        this.currentUser = user;
        System.out.println("Setting current user: Username=" + user.getUsername() + 
                          ", UserID=" + user.getUserId() + 
                          ", RoleID=" + user.getRoleId());
        if (currentUser != null && currentUser.getRoleId() == 2) {
            Long mechanicId = new UserMechanicMappingDAO().getMechanicIdByUserId(currentUser.getUserId());
            if (mechanicId != null) {
                this.mechanic = new MechanicDAO().getMechanicById(mechanicId);
                if (mechanic != null) {
                    welcomeLabel.setText("Welcome, " + mechanic.getName());
                    initializeTasks();
                } else {
                    welcomeLabel.setText("Welcome, Mechanic (Unknown)");
                }
            } else {
                welcomeLabel.setText("Welcome, Mechanic (Unknown)");
            }
        }
    }

    private void initializeTasks() {
        if (mechanic == null) {
            System.out.println("Mechanic is not set. Cannot initialize tasks.");
            return;
        }

        // Clear Hibernate cache to ensure fresh data
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.clear();
        } catch (Exception e) {
            System.err.println("Failed to clear Hibernate cache: " + e.getMessage());
        }

        List<ServiceRecord> assignedTasks = serviceRecordDAO.getAssignedTasks(mechanic.getMechanicId());
        System.out.println("Found " + assignedTasks.size() + " tasks for mechanic: " + mechanic.getName());

        // Split tasks into Pending/Ongoing and Completed (case-insensitive)
        List<ServiceRecord> pendingOngoingTasks = assignedTasks.stream()
            .filter(task -> !task.getStatus().equalsIgnoreCase("Completed"))
            .collect(Collectors.toList());
        List<ServiceRecord> completedTasks = assignedTasks.stream()
            .filter(task -> task.getStatus().equalsIgnoreCase("Completed"))
            .collect(Collectors.toList());

        // Log Pending/Ongoing Tasks
        System.out.println("Pending/Ongoing Tasks:");
        pendingOngoingTasks.forEach(task -> System.out.println("Task: " + task.getDescription() + 
                                                              ", Status: " + task.getStatus()));
        // Log Completed Tasks
        System.out.println("Completed Tasks:");
        completedTasks.forEach(task -> System.out.println("Task: " + task.getDescription() + 
                                                         ", Status: " + task.getStatus()));

        // Populate the tables
        tasksTable.setItems(FXCollections.observableArrayList(pendingOngoingTasks));
        completedTasksTable.setItems(FXCollections.observableArrayList(completedTasks));
    }
    
    @FXML
    private void markAsOngoing() {
        ServiceRecord selectedTask = tasksTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a task to mark as ongoing.");
            alert.showAndWait();
            return;
        }
        System.out.println("Selected task: ServiceID=" + selectedTask.getServiceId() + 
                          ", Description=" + selectedTask.getDescription() + 
                          ", Status=" + selectedTask.getStatus());

        // Show confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Action");
        confirmation.setHeaderText("Mark Task as Ongoing");
        confirmation.setContentText("Are you sure you want to mark the task '" + 
                                   selectedTask.getDescription() + "' as Ongoing?");
        confirmation.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (selectedTask.getStatus().equals("Pending")) {
                    selectedTask.setStatus("Ongoing");
                    System.out.println("Updating task status to Ongoing: ServiceID=" + selectedTask.getServiceId());
                    try {
                        serviceRecordDAO.updateServiceRecord(selectedTask);
                        System.out.println("UpdateServiceRecord called successfully for ServiceID=" + selectedTask.getServiceId());
                    } catch (Exception e) {
                        System.err.println("Error calling updateServiceRecord for ServiceID=" + selectedTask.getServiceId());
                        e.printStackTrace();
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Failed to update task status: " + e.getMessage());
                        errorAlert.showAndWait();
                        return;
                    }
                    initializeTasks();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Only pending tasks can be marked as ongoing.");
                    alert.showAndWait();
                }
            }
        });
    }
    
    @FXML
    private void markAsComplete() {
        ServiceRecord selectedTask = tasksTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a task to mark as complete.");
            alert.showAndWait();
            return;
        }
        System.out.println("Selected task: ServiceID=" + selectedTask.getServiceId() + 
                          ", Description=" + selectedTask.getDescription() + 
                          ", Status=" + selectedTask.getStatus());

        // Show confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Action");
        confirmation.setHeaderText("Mark Task as Complete");
        confirmation.setContentText("Are you sure you want to mark the task '" + 
                                   selectedTask.getDescription() + "' as Complete?");
        confirmation.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (selectedTask.getStatus().equals("Ongoing")) {
                    selectedTask.setStatus("Completed");
                    System.out.println("Updating task status to Completed: ServiceID=" + selectedTask.getServiceId());
                    try {
                        serviceRecordDAO.updateServiceRecord(selectedTask);
                        System.out.println("UpdateServiceRecord called successfully for ServiceID=" + selectedTask.getServiceId());
                    } catch (Exception e) {
                        System.err.println("Error calling updateServiceRecord for ServiceID=" + selectedTask.getServiceId());
                        e.printStackTrace();
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Failed to update task status: " + e.getMessage());
                        errorAlert.showAndWait();
                        return;
                    }
                    initializeTasks();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Only ongoing tasks can be marked as complete.");
                    alert.showAndWait();
                }
            }
        });
    }
    
    @FXML
    private void logout() {
        this.mechanic = null;

        try {
            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent loginRoot = loader.load();
            Scene loginScene = new Scene(loginRoot, 500, 300);

            // Get the current stage and set the login scene
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Login");
            stage.show();

            System.out.println("Mechanic logged out successfully.");
        } catch (IOException e) {
            System.err.println("Error loading login screen: " + e.getMessage());
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Failed to load login screen: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }
}