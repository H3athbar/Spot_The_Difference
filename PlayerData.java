import java.net.InetAddress;

/**
 * Holds the player's data, such as the score, username, and data from each client, since each client is a player.
 */
public class PlayerData {
    /**
     * The IP Address of the client.
     */
    private InetAddress address;

    /**
     * The port of the client.
     */
    private int port;

    /**
     * The username of the player.
     */
    private String username;

    /**
     * Constructor for the player data class.
     *
     * @param username The username of the player.
     */
    public PlayerData(String username, int port, InetAddress address) {
        this.port = port;
        this.address = address;
        this.username = username;
    }

    /**
     * Gets the port of the client.
     *
     * @return The port of the client.
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the IP address of the client.
     *
     * @return The IP address of the client.
     */
    public InetAddress getAddress() {
        return address;
    }
}
