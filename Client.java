import javafx.application.Platform;
import javafx.concurrent.Task;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class manages the connection between the server and the normal player client. It should receive nd send packets,
 * using attributes from a UDP messaging system.
 */
public class Client {
    /**
     * The server host.
     */
    private String server_host;

    /**
     * The server port.
     */
    private int server_port;

    /**
     * The Client's socket for connection to the Server.
     */
    private DatagramSocket socket;

    /**
     * The address of the Server.
     */
    private InetAddress serverAddress;

    /**
     * The image number selected by the server for this round.
     */
    private int imageChoice;

    /**
     * The number of players currently connected to the game.
     */
    private int playerCount;

    /**
     * Boolean to check if the Client is running. Starts off as true, until the game either stops or client quits.
     */
    private boolean running;

    /**
     * The game time left for this specific client.
     */
    private int gameTime;

    /**
     * Using Executor Service for the Task Class being utilized.
     */
    private ExecutorService executorService;

    /**
     * The last Leaderboard object received from the server
     */
    private Leaderboard leaderboard;

    /**
     * Constructor for the client class. Initializes private variables for host, port, socket, and address. Aslo starts
     * the executor service, and sets the running boolean to be true.
     *
     * @param server_host The sever host.
     * @param server_port The server's port.
     */
    public Client(String server_host, int server_port) {
        try {
            this.server_host = server_host;
            this.server_port = server_port;
            this.socket = new DatagramSocket();
            this.serverAddress = InetAddress.getByName(this.server_host);
            leaderboard = new Leaderboard();

            running = true;
            executorService = Executors.newCachedThreadPool();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter method for the image choice variable.
     *
     * @return Which image is being chosen for the game.
     */
    public int getImageChoice() {
        return imageChoice;
    }

    /**
     * Getter method for the player count of the game.
     *
     * @return The number of players in the game waiting.
     */
    public int getPlayerCount() {
        return playerCount;
    }

    /**
     * Getter method for the game time of the current client.
     *
     * @return The game time.
     */
    public int getGameTime() {
        return gameTime;
    }

    /**
     * Getter method for the current leaderboard received from the server
     * @return the leaderboard
     */
    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    /**
     * Method to have the client listen until it receives a command from the server.
     */
    public void taskListening() {
        Task<Void> listeningTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Keep looping while the client class is currently running, until
                while (running) {
                    try {
                        // Creating the packet.
                        byte[] data = new byte[1000]; // set up packet
                        // Creating the receiving packet.
                        DatagramPacket receivePacket = new DatagramPacket(data, data.length);

                        System.out.println("Raw packet received by client");

                        // Waiting for the packet.
                        socket.receive(receivePacket);

                        // Storing the message received from the packet.
                        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                        // Invoke later but for the JAVAFX version.
                        Platform.runLater(() -> {
                            messageReader(message);
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

        // Finally, submitting the task to the executor service.
        executorService.submit(listeningTask);
    }

    /**
     * Sending a message to the server class.
     *
     * @param message The string message sent.
     */
    public void sendMessage(String message) {
        // NEED TO SEND COMPLETE WHEN INDIVIDUAL IS DONE!!!!
        try {
            // Creating the data for the sending packet.
            byte[] data = message.getBytes();
            // Creating the packet to send to the server.
            DatagramPacket sendingPacket = new DatagramPacket(data, data.length, serverAddress, server_port);
            // Sending the packet.
            socket.send(sendingPacket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reading the message from the server class, and deciding what are the next steps.
     *
     * @param message The message being read from the server.
     */
    public void messageReader(String message) {
        // Trimming to message in case for whitespace or bugs.
        message = message.trim();

        System.out.println("CLIENT MESSAGE READER GOT: [" + message + "]");

        // If the message from server is LEADER, then switch to the game leader screen.
        if (message.equals("LEADER")) {
            SceneManager.switchScene("LeaderWaitingScreen.fxml");
        } else if (message.equals("PLAYER")) {
            // If the message is PLAYER, send the user to the normal waiting screen.
            SceneManager.switchScene("PlayerWaitingScreen.fxml");
        } else if (message.equals("GAME_OVER")) {
            // If message is GAME_OVER, send the user to the game over screen.
            SceneManager.switchScene("GameOver.fxml");
        } else if (message.startsWith("GAME_START:")) { // If message is IMAGE_CHOICE, go to game screen.
            // Obtaining the image choice and how much time in seconds the users get from the message.
            String gameStartMessage = message.substring("GAME_START:".length());

            // Taking the rest of the message and splitting it by the dash.
            String[] pieces = gameStartMessage.split("-");
            // Assigning the image choice and game time variables of the client.
            imageChoice = Integer.parseInt(pieces[0]);
            gameTime = Integer.parseInt(pieces[1]);

            // Switching scene to be the game screen.
            SceneManager.switchScene("SpotTheDiffView.fxml");
        } else if (message.startsWith("PLAYER_COUNT:")) {
            String countString = message.substring("PLAYER_COUNT:".length());
            playerCount = Integer.parseInt(countString);
            SceneManager.updatePlayerCountText(playerCount);
        } else if (message.startsWith("LEADERBOARD:")){
            readLeaderboardMessage(message);
        } else {
            // The message sent doesn't match with any of the cases.
            System.out.println("Unknown message from server: " + message);
        }
    }

    /**
     * Reads a leaderboard message from the server and uses it to rebuild the leaderboard
     * @param message the leaderboard message from the server
     */
    private void readLeaderboardMessage(String message){
        // Obtains the part of the message that contains the leaderboard data
        String leaderboardData = message.substring("LEADERBOARD:".length());
        //Creates a new leaderboard to store the updated results
        Leaderboard newLeaderboard = new Leaderboard();

        //If leaderboardData is not empty
        if(!leaderboardData.isEmpty()){
            //Splits each player entry
            String[] players = leaderboardData.split("\\|");

            // Loops through each player entry in the leaderboard message
            for(String player : players){
                // Splits into parts which are username, score, timeRemaining
                String[] parts = player.split(",");

                String username = parts[0];
                int score = Integer.parseInt(parts[1]);
                int timeRemaining = Integer.parseInt(parts[2]);

                // Adds the new player result to the updated leaderboard
                newLeaderboard.addPlayer(username, score, timeRemaining);
            }
        }
        // Replaces the old leaderboard with the new updated one
        leaderboard = newLeaderboard;
        SceneManager.updateLeaderboard(leaderboard);
    }

    /**
     * Stops the client and closes the socket.
     */
    public void stopClient() {
        // Setting the state of this client to be "not running".
        running = false;

        // Making sure that the socket isn't null or already closed.
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }

        // Checking to make sure that the executor service isn't null and isn't already shut down.
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}
