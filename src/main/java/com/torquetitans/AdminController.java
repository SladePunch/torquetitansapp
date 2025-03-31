package com.torquetitans;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminController {

    @FXML
    private Button logoutButton;

    @FXML
    private void initialize() {
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
                System.out.println("Lỗi: " + e.getMessage());
            }
        });
    }
}