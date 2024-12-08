// ClientMain.java
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Main application class for the client
public class ClientMain extends Application {
    private Stage primaryStage;
    private ClientController gameController; // Controller for the game screen
    private ResultsController resultsController; // Controller for the results screen
    private ClientNetworkThread networkThread; // Network thread for communication
    private WelcomeClientController welcomeController; // Controller for the welcome screen

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showWelcomeScreen(); // Display the welcome screen on startup
    }

    /**
     * Display the welcome screen where users can connect to the server.
     */
    public void showWelcomeScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/WelcomeClient.fxml"));
        Scene scene = new Scene(loader.load(), 500, 300);
        scene.getRoot().setStyle("-fx-background-color: green;");
        welcomeController = loader.getController();
        welcomeController.setMain(this);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Three Card Poker Client - Welcome");
        primaryStage.show();
    }

    /**
     * Display the game screen and initiate network communication.
     * @param host Server IP address.
     * @param port Server port number.
     */
    public void showGameScreen(String host, int port) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI.fxml"));
            Scene scene = new Scene(loader.load(), 900, 700);
            gameController = loader.getController();

            // Initialize and start the network thread
            networkThread = new ClientNetworkThread(host, port, gameController);
            networkThread.start();
            gameController.setNetworkThread(networkThread);
            gameController.setMain(this);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Three Card Poker Client - Game");
            primaryStage.show();
        } catch (Exception e) {
            welcomeController.showError("Error loading game screen.");
        }
    }

    /**
     * Display the game screen directly, typically after a new game request.
     */
    public void showGameScreenDirect() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI.fxml"));
            Scene scene = new Scene(loader.load(), 900, 700);
            gameController = loader.getController();

            // Update the network thread's controller to the new controller
            if (networkThread != null) {
                networkThread.setController(gameController);
                gameController.setNetworkThread(networkThread);
            }

            gameController.setMain(this);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Three Card Poker Client - Game");
            primaryStage.show();

            // Reset UI for a new game
            gameController.clearInfoAndCards();
            gameController.showMessage("Place your bets.");
            gameController.showBetControls();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Display the results screen after a game round concludes.
     * @param msg Result message to display.
     * @param roundWinnings Amount won or lost in the round.
     */
    public void showResultsScreen(String msg, int roundWinnings) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResultsScreen.fxml"));
            Scene scene = new Scene(loader.load());
            resultsController = loader.getController();
            resultsController.setMain(this);
            resultsController.setResults(msg, roundWinnings);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Three Card Poker Client - Results");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a NEW_GAME request to the server to start a new game round.
     */
    public void sendNewGameRequest() {
        PokerInfo req = new PokerInfo();
        req.setAction(PokerInfo.Action.NEW_GAME);
        if (networkThread != null) {
            networkThread.sendInfo(req);
        }
    }

    /**
     * Send a FRESH_START request to the server to restart the game.
     */
    public void freshStart() {
        PokerInfo req = new PokerInfo();
        req.setAction(PokerInfo.Action.FRESH_START);
        if (networkThread != null) {
            networkThread.sendInfo(req);
        }
    }

    /**
     * Exit the game by sending an EXIT request and returning to the welcome screen.
     */
    public void exitGame() {
        if (networkThread != null) {
            PokerInfo req = new PokerInfo();
            req.setAction(PokerInfo.Action.EXIT);
            networkThread.sendInfo(req);
            networkThread.interrupt(); // Terminate the network thread
        }
        // Return to welcome screen
        try {
            showWelcomeScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}