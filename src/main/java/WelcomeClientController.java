// WelcomeClientController.java
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class WelcomeClientController {
    @FXML private TextField ipField; // Input field for server IP
    @FXML private TextField portField; // Input field for server port
    @FXML private Label errorLabel; // Label to display error messages

    private ClientMain mainApp; // Reference to the main application

    /**
     * Set the main application reference.
     * @param main ClientMain instance.
     */
    public void setMain(ClientMain main) {
        this.mainApp = main;
    }

    /**
     * Handle the CONNECT button action.
     * Attempts to connect to the server with provided IP and port.
     */
    @FXML
    private void connectToServer() {
        errorLabel.setText("");
        try {
            String ip = ipField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());
            mainApp.showGameScreen(ip, port); // Transition to the game screen
        } catch (NumberFormatException e) {
            showError("Enter valid IP and port numbers.");
        } catch (Exception e) {
            showError("Unable to connect to the server.");
        }
    }

    /**
     * Handle the EXIT button action.
     * Closes the application window.
     */
    @FXML
    private void exitProgram() {
        errorLabel.setText("");
        Stage stage = (Stage) ipField.getScene().getWindow();
        stage.close();
    }

    /**
     * Display an error message in the error label.
     * @param message The error message to display.
     */
    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
    }
}