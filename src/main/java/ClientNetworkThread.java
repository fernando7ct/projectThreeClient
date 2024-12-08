// ClientNetworkThread.java
import java.io.*;
import java.net.Socket;

// Thread class to handle network communication with the server
public class ClientNetworkThread extends Thread {
    private String host; // Server IP address
    private int port; // Server port number
    private ObjectInputStream in; // Input stream for receiving data
    private ObjectOutputStream out; // Output stream for sending data
    private volatile ClientController controller; // Reference to the controller to handle updates

    /**
     * Constructor to initialize network thread with server details and controller.
     * @param host Server IP address.
     * @param port Server port number.
     * @param controller ClientController instance to handle server updates.
     */
    public ClientNetworkThread(String host, int port, ClientController controller) {
        this.host = host;
        this.port = port;
        this.controller = controller;
    }

    /**
     * Update the controller reference, useful when UI controllers change.
     * @param newController New ClientController instance.
     */
    public void setController(ClientController newController) {
        this.controller = newController;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(host, port); // Establish connection to the server
            out = new ObjectOutputStream(socket.getOutputStream());
            in  = new ObjectInputStream(socket.getInputStream());

            // Send CONNECT action initially to notify the server
            PokerInfo connectInfo = new PokerInfo();
            connectInfo.setAction(PokerInfo.Action.CONNECT);
            sendInfo(connectInfo);

            PokerInfo response;
            // Continuously listen for server messages
            while ((response = (PokerInfo) in.readObject()) != null) {
                ClientController currentController = this.controller;
                if (currentController != null) {
                    currentController.handleServerUpdate(response); // Delegate handling to the controller
                }
            }
        } catch (Exception e) {
            if (controller != null) {
                controller.showMessage("Connection lost or unable to connect.");
            }
        }
    }

    /**
     * Send a PokerInfo object to the server.
     * @param info The PokerInfo object containing action and data.
     */
    public void sendInfo(PokerInfo info) {
        try {
            out.writeObject(info);
            out.flush();
        } catch (IOException e) {
            if (controller != null) {
                controller.showMessage("Error sending to server.");
            }
        }
    }
}