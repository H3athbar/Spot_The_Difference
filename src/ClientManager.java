/**
 * Handles all the different client objects at once, and helps with the
 * communication between the clients and controllers.
 */
public class ClientManager {

    /**
     * The single shared ClientManager object used by all controllers.
     */
    private Client client;

    /**
     * The username entered by the current player.
     */
    private String username;

    /**
     * The client object that sends messages to and receives messages from the server.
     */
    private static ClientManager instance;

    /**
     * Constructs a ClientManager object. This creates the client connection information and starts the client's
     * listening task so the client can receive messages from the server.
     */
    private ClientManager() {
        client = new Client("128.255.19.255", 23505);
        // Making sure the client class is now waiting for a message from the server.
        client.taskListening();
    }

    /**
     * Returns the single shared ClientManager instance.
     *
     * @return The shared ClientManager instance.
     */
    public static ClientManager getInstance() {
        // Making sure the instance isn't null.
        if (instance == null) {
            instance = new ClientManager();
        }

        return instance;
    }

    /**
     * Has the client join the game with the username only if less than 5 player currently.
     *
     * @param username The name of the user that they input.
     */
    public void joinGame(String username) {
        if(getPlayerCount()<5) {
            this.username = username;
            client.sendMessage("USERNAME:" + username);
        }
    }

    /**
     * Starts the game by sending PLAY to the server.
     */
    public void startGame() {
        client.sendMessage("PLAY");
    }

    /**
     * Gets the image choice sent by the server.
     *
     * @return The image choice number.
     */
    public int getImageChoice() {
        return client.getImageChoice();
    }

    /**
     * Gets the current player's username.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sends the completed message to the server that the game is over.
     */
    public void sendComplete(int score, int timeRemaining) {
        client.sendMessage("COMPLETE:" + username + "," + score + "," + timeRemaining);
    }

    /**
     * Gets the starting game time sent by the server.
     *
     * @return The game time in seconds.
     */
    public int getGameTimeSeconds() {
        return client.getGameTime();
    }


    /**
     * Gets the player count from the client.
     *
     * @return The player count.
     */
    public int getPlayerCount() {
        return client.getPlayerCount();
    }

    /**
     * Requests the current number of connected clients from the server.
     */
    public void requestPlayerCount() {
        client.sendMessage("COUNT_REQUEST");
    }

    /**
     * Gets the current leaderboard from the client
     * @return the current leaderboard
     */
    public Leaderboard getLeaderboard(){
        return client.getLeaderboard();
    }

    /**
     * Stops the client from running. Used when closing the clients application window.
     */
    public void stopClient() {
        client.stopClient();
    }
}
