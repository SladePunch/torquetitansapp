package GarageApp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import GarageApp.model.ServiceRecord;
import GarageApp.model.User;
import org.hibernate.Session;

public class MechanicController {

    @FXML
    private ListView<String> jobList;

    @FXML
    private Button logoutButton;

    @FXML
    private Label errorLabel; // Thêm Label để hiển thị lỗi

    @FXML
    private void initialize() {

        if (errorLabel == null) {
            errorLabel = new Label();
            errorLabel.setStyle("-fx-text-fill: red;");
        }

        // Kiểm tra người dùng đã đăng nhập
        User loggedInUser = LoginController.getLoggedInUser();
        if (loggedInUser == null) {
            errorLabel.setText("No user is logged in!");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Lấy Mechanic từ User
            if (loggedInUser.getMechanic() == null) {
                errorLabel.setText("User is not associated with a mechanic!");
                return;
            }
            Long mechanicId = loggedInUser.getMechanic().getMechanicID();
            if (mechanicId == null) {
                errorLabel.setText("Mechanic ID is null!");
                return;
            }

            // Truy vấn danh sách ServiceRecord
            java.util.List<ServiceRecord> records = session.createQuery("FROM ServiceRecord WHERE mechanic.mechanicID = :mechanicId", ServiceRecord.class)
                                                           .setParameter("mechanicId", mechanicId)
                                                           .list();
            for (ServiceRecord record : records) {
                String recordInfo = "ID: " + record.getServiceId() +
                                   " | License Plate: " + (record.getVehicle() != null ? record.getVehicle().getLicensePlate() : "N/A") +
                                   " | Service: " + record.getDescription() +
                                   " | Status: " + record.getStatus() +
                                   " | Date: " + record.getServiceDate();
                jobList.getItems().add(recordInfo);
            }
        } catch (Exception e) {
            errorLabel.setText("Connection error: " + e.getMessage());
        }

        // Xử lý nút logout
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
                errorLabel.setText("Error logging out: " + e.getMessage());
            }
        });
    }
}