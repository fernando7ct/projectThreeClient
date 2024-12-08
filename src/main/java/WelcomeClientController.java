// WelcomeClientController.java
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class WelcomeClientController {
    @FXML private TextField ipField; // Input field for server IP
    @FXML private TextField portField; // Input field for server port
    @FXML private Label errorLabel; // Label to display error messages
    @FXML private ProgressIndicator loadingIndicator; // Loading indicator

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
        loadingIndicator.setVisible(true); // Show loading indicator

        String ip = ipField.getText().trim();
        String portText = portField.getText().trim();

        // Validate IP and Port inputs
        if (ip.isEmpty() || portText.isEmpty()) {
            showError("IP and Port fields cannot be empty.");
            loadingIndicator.setVisible(false);
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portText);
            if (port < 1 || port > 65535) {
                showError("Port number must be between 1 and 65535.");
                loadingIndicator.setVisible(false);
                return;
            }
        } catch (NumberFormatException e) {
            showError("Enter a valid integer for port.");
            loadingIndicator.setVisible(false);
            return;
        }

        // Attempt to establish a connection
        new Thread(() -> {
            try (Socket socket = new Socket(ip, port)) {
                // Connection successful; proceed to switch screens on the JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    loadingIndicator.setVisible(false); // Hide loading indicator
                    try {
                        mainApp.showGameScreen(ip, port);
                    } catch (Exception e) {
                        showError("Error loading game screen.");
                        e.printStackTrace();
                    }
                });
            } catch (UnknownHostException e) {
                javafx.application.Platform.runLater(() -> {
                    loadingIndicator.setVisible(false); // Hide loading indicator
                    showError("Unknown host: " + ip);
                });
            } catch (IOException e) {
                javafx.application.Platform.runLater(() -> {
                    loadingIndicator.setVisible(false); // Hide loading indicator
                    showError("Unable to connect to the server.");
                });
            }
        }).start();
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