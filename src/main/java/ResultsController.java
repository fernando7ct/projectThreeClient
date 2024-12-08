// ResultsController.java
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ResultsController {
    @FXML private Label resultMessageLabel; // Label to display result message
    @FXML private Label roundWinningsLabel; // Label to display round winnings

    private ClientMain mainApp; // Reference to the main application
    private int roundWinnings; // Amount won or lost in the round

    /**
     * Set the main application reference.
     * @param mainApp ClientMain instance.
     */
    public void setMain(ClientMain mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Set the results to be displayed on the results screen.
     * @param msg Result message.
     * @param roundWinnings Amount won or lost.
     */
    public void setResults(String msg, int roundWinnings) {
        resultMessageLabel.setText(msg);
        this.roundWinnings = roundWinnings;
        roundWinningsLabel.setText("Round Winnings: " + roundWinnings);
    }

    /**
     * Handle the PLAY AGAIN button action.
     * Sends a NEW_GAME request and returns to the game screen.
     */
    @FXML
    private void playAnotherGame() {
        mainApp.sendNewGameRequest();
        mainApp.showGameScreenDirect();
    }

    /**
     * Handle the EXIT button action.
     * Exits the game and returns to the welcome screen.
     */
    @FXML
    private void exitGame() {
        mainApp.exitGame();
    }
}