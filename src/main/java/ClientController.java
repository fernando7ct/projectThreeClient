// ClientController.java
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import java.util.ArrayList;

public class ClientController {
    @FXML private TextArea infoDisplay; // Display area for game messages
    @FXML private TextField anteBetField, pairPlusBetField; // Input fields for bets
    @FXML private Label playerBalanceLabel, pAnteLabel, pPairPlusLabel, pPlayLabel; // Labels for displaying player stats
    @FXML private Button dealButton, playButton, foldButton, placeBetButton; // Action buttons
    @FXML private ImageView pCard1, pCard2, pCard3, dealerCard1, dealerCard2, dealerCard3; // Image views for displaying cards

    private ClientNetworkThread networkThread; // Handles network communication
    private ClientMain mainApp; // Reference to the main application
    private boolean newLookEnabled = false; // Toggle for UI theme

    // Set the main application reference
    public void setMain(ClientMain main) {
        this.mainApp = main;
    }

    // Set the network thread reference
    public void setNetworkThread(ClientNetworkThread thread) {
        this.networkThread = thread;
    }

    /**
     * Handle updates received from the server.
     * @param info PokerInfo object containing update details.
     */
    public void handleServerUpdate(PokerInfo info) {
        Platform.runLater(() -> {
            if (info.getAction() == PokerInfo.Action.RESULTS) {
                // Round ended, show results screen
                mainApp.showResultsScreen(info.getMessage(), info.getRoundWinnings());
                return;
            }
            // Update UI elements with received information
            infoDisplay.appendText(info.getMessage() + "\n");
            playerBalanceLabel.setText(String.valueOf(info.getPlayerBalance()));

            pAnteLabel.setText("$" + info.getPlayerAnte());
            pPairPlusLabel.setText("$" + info.getPlayerPairPlus());
            pPlayLabel.setText("$" + info.getPlayerPlay());

            // Update card images based on hands
            updateCards(info.getPlayerHand(), info.getDealerHand(), info.isDealerCardsRevealed());

            String msg = info.getMessage().toLowerCase();
            // Determine UI state based on the message content
            if (msg.contains("new game started")) {
                // After new game: clear UI, prompt for bets
                clearInfoAndCards();
                infoDisplay.appendText("Place your bets.\n");
                showBetControls();
            } else if (msg.contains("fresh start")) {
                // After fresh start: reset UI without resetting winnings
                clearInfoAndCards();
                infoDisplay.appendText("Place your bets.\n");
                showBetControls();
            } else if (msg.contains("place your bets")) {
                // Initial connection or after exit: prompt for bets
                showBetControls();
            } else if (msg.contains("bet placed")) {
                // Bets placed: enable DEAL button
                showDealControls();
            } else if (msg.contains("cards dealt")) {
                // Cards dealt: enable PLAY and FOLD buttons
                showPlayFoldControls();
            } else {
                // Default state: show bet controls
                showBetControls();
            }
        });
    }

    /**
     * Update the card images on the UI based on the player's and dealer's hands.
     * @param playerHand ArrayList of player's Card objects.
     * @param dealerHand ArrayList of dealer's Card objects.
     * @param dealerRevealed Boolean indicating if dealer's cards should be shown.
     */
    private void updateCards(ArrayList<Card> playerHand, ArrayList<Card> dealerHand, boolean dealerRevealed) {
        setCardImage(pCard1, playerHand.size() > 0 ? playerHand.get(0) : null);
        setCardImage(pCard2, playerHand.size() > 1 ? playerHand.get(1) : null);
        setCardImage(pCard3, playerHand.size() > 2 ? playerHand.get(2) : null);

        if (dealerRevealed && dealerHand.size() == 3) {
            // Reveal dealer's cards
            setCardImage(dealerCard1, dealerHand.get(0));
            setCardImage(dealerCard2, dealerHand.get(1));
            setCardImage(dealerCard3, dealerHand.get(2));
        } else {
            // Hide dealer's cards
            setCardImage(dealerCard1, null);
            setCardImage(dealerCard2, null);
            setCardImage(dealerCard3, null);
        }
    }

    /**
     * Set the image for a given ImageView based on the Card.
     * @param imageView The ImageView to update.
     * @param card The Card object. If null, display the card back.
     */
    private void setCardImage(ImageView imageView, Card card) {
        if (card == null) {
            // Display card back image
            imageView.setImage(new Image(getClass().getResourceAsStream("/cards/back.png")));
        } else {
            // Construct image path based on card value and suit
            String imagePath = "/cards/" + card.getValue() + card.getSuit() + ".png";
            imageView.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        }
    }

    /**
     * Display a message in the info display area.
     * @param msg The message to display.
     */
    public void showMessage(String msg) {
        Platform.runLater(() -> infoDisplay.appendText(msg + "\n"));
    }

    /**
     * Handle the PLACE BET button action.
     * Collects bet amounts and sends a PLACE_BET action to the server.
     */
    @FXML
    private void placeBet() {
        try {
            int ante = anteBetField.getText().isEmpty() ? 0 : Integer.parseInt(anteBetField.getText());
            int pp = pairPlusBetField.getText().isEmpty() ? 0 : Integer.parseInt(pairPlusBetField.getText());
            PokerInfo req = new PokerInfo();
            req.setAction(PokerInfo.Action.PLACE_BET);
            req.setAnteBetRequest(ante);
            req.setPairPlusBetRequest(pp);
            networkThread.sendInfo(req);
        } catch (NumberFormatException e) {
            showMessage("Enter valid bet amounts.");
        }
    }

    /**
     * Handle the DEAL button action.
     * Sends a DEAL action to the server to deal cards.
     */
    @FXML
    private void dealCards() {
        PokerInfo req = new PokerInfo();
        req.setAction(PokerInfo.Action.DEAL);
        networkThread.sendInfo(req);
    }

    /**
     * Handle the PLAY button action.
     * Sends a PLAY action to the server to proceed with the game.
     */
    @FXML
    private void playHand() {
        PokerInfo req = new PokerInfo();
        req.setAction(PokerInfo.Action.PLAY);
        networkThread.sendInfo(req);
    }

    /**
     * Handle the FOLD button action.
     * Sends a FOLD action to the server to fold the hand.
     */
    @FXML
    private void foldHand() {
        PokerInfo req = new PokerInfo();
        req.setAction(PokerInfo.Action.FOLD);
        networkThread.sendInfo(req);
    }

    /**
     * Handle the EXIT button action.
     * Exits the game and returns to the welcome screen.
     */
    @FXML
    private void exitGame() {
        mainApp.exitGame();
    }

    /**
     * Handle the FRESH START button action.
     * Sends a FRESH_START action to the server to restart the game.
     */
    @FXML
    private void handleFreshStart() {
        mainApp.freshStart();
    }

    /**
     * Toggle the UI theme between green and red backgrounds.
     */
    @FXML
    private void handleNewLook() {
        Region root = (Region) infoDisplay.getScene().getRoot();
        if (newLookEnabled) {
            root.setStyle("-fx-background-color: green;");
            newLookEnabled = false;
        } else {
            root.setStyle("-fx-background-color: red;");
            newLookEnabled = true;
        }
    }

    /**
     * Handle the DISCONNECT button action.
     * Disconnects from the server and exits the game.
     */
    @FXML
    private void handleDisconnect() {
        mainApp.exitGame();
    }

    /**
     * Clear the info display and reset card images and labels.
     */
    public void clearInfoAndCards() {
        infoDisplay.clear();
        setCardImage(pCard1, null);
        setCardImage(pCard2, null);
        setCardImage(pCard3, null);
        setCardImage(dealerCard1, null);
        setCardImage(dealerCard2, null);
        setCardImage(dealerCard3, null);

        // Reset the displayed bets
        pAnteLabel.setText("$0");
        pPairPlusLabel.setText("$0");
        pPlayLabel.setText("$0");

        playerBalanceLabel.setText("100");
    }

    /**
     * Show controls related to placing bets.
     */
    public void showBetControls() {
        anteBetField.setVisible(true);
        pairPlusBetField.setVisible(true);
        placeBetButton.setVisible(true);
        dealButton.setVisible(false);
        playButton.setVisible(false);
        foldButton.setVisible(false);
    }

    /**
     * Show controls related to dealing cards.
     */
    public void showDealControls() {
        anteBetField.setVisible(false);
        pairPlusBetField.setVisible(false);
        placeBetButton.setVisible(false);
        dealButton.setVisible(true);
        playButton.setVisible(false);
        foldButton.setVisible(false);
    }

    /**
     * Show controls related to playing or folding the hand.
     */
    public void showPlayFoldControls() {
        anteBetField.setVisible(false);
        pairPlusBetField.setVisible(false);
        placeBetButton.setVisible(false);
        dealButton.setVisible(false);
        playButton.setVisible(true);
        foldButton.setVisible(true);
    }
}