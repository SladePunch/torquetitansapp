package com.torquetitans;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import com.torquetitans.model.*;
import org.hibernate.Session;

public class MechanicController {

    @FXML
    private ListView<String> jobList;

    @FXML
    private Button logoutButton;

    @FXML
    private void initialize() {
        // Retrieve the list of jobs from Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            java.util.List<Job> jobs = session.createQuery("FROM Job WHERE mechanic.mechanicId = 1", Job.class).list();
            for (Job job : jobs) {
                String jobInfo = "ID: " + job.getJobId() +
                                " | License Plate: " + (job.getVehicle() != null ? job.getVehicle().getLicensePlate() : "N/A") +
                                " | Service: " + job.getServiceType() +
                                " | Status: " + job.getStatus();
                jobList.getItems().add(jobInfo);
            }
        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
        }

        // Logout button event
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
                System.out.println("Error: " + e.getMessage());
            }
        });
    }
}