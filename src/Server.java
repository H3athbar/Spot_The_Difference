import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Server {
    /**
     * Socket connection to client
     */
    private DatagramSocket socket; // socket to connect to client

    /**
     * holds address/port string, and scores for current players
     */
    private Map<String, PlayerData> players;

    /**
     * Randomly generate a number to select a photo from options
     */
    private final int IMAGE_CHOICE;

    /**
     * The total game time for when a player in on the game screen.
     */
    private static final int GAME_TIME_SECONDS = 60;

    /**
     * Stores and ranks player results after they finish the game.
     */
    private Leaderboard leaderboard;

    /**
     * Initializing the private variables such as Datagram socket, the image choice,
     * number of players, and if the game has started yet.
     */
    public Server() {
        players = new HashMap<>();
        IMAGE_CHOICE = new Random().nextInt(3); //between 0 and 3, for 3 images
        leaderboard = new Leaderboard();

        // create DatagramSocket for sending and receiving packets
        try {
            socket = new DatagramSocket(23505); //my personal port
        }
        catch (SocketException socketException) {
            socketException.printStackTrace();
            System.exit(1);
        }
    }

    // wait for packets(users) to arrive, display data and echo packet to client
    public void waitForPlayers()
    {
        boolean gameStarted = false;
        while (!gameStarted)
        {
            try // receive packet, display contents, return copy to client
            {
                byte[] data = new byte[100]; // set up packet
                DatagramPacket receivePacket =
                        new DatagramPacket(data, data.length);

                socket.receive(receivePacket); // wait to receive packet
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                //create new player if client sends "USERNAME:___"
                if(message.startsWith("USERNAME:")) {
                    // Obtain the username from the message, and increase the number of players by one.
                    String username = message.substring("USERNAME:".length());

                    // Create a new Player data object for the new username from the client.
                    PlayerData player = new PlayerData(username, receivePacket.getPort(), receivePacket.getAddress());

                    // Getting the data for the player, so that now messages will be sent to ALL clients.
                    String playerKey = receivePacket.getAddress().toString() + ":" + receivePacket.getPort();

                    // Put the object and username into the map for this class.
                    players.put(playerKey, player); //removes USERNAME:

                    sendPacketToAllClients("PLAYER_COUNT:" + players.size());

                    // Deciding which message to send to client if they are the leader (first) or a normal player.
                    if(players.size() ==1) {
                        sendPacketToClient(receivePacket, "LEADER"); // send packet to client
                    }
                    else {
                        sendPacketToClient(receivePacket, "PLAYER");
                    }
                }
                else if(message.startsWith("PLAY")) { // Message is sent by client to play the game.
                    // Game starts only if there are two or more people that are currently waiting.
                    if (players.size() >= 2) {
                        // Sets the boolean to true and sends the image choice command with the image choice.
                        gameStarted = true;
                        // Using the helper method to send the image choice message to ALL clients.
                        sendPacketToAllClients("GAME_START:" + IMAGE_CHOICE + "-" + GAME_TIME_SECONDS);
                    }
                } else if (message.startsWith("COUNT_REQUEST")) {
                sendPacketToClient(receivePacket, "PLAYER_COUNT:" + players.size());
                } else {

                }
            } catch (IOException ioException) {
                System.out.println(ioException + "\n");
                ioException.printStackTrace();
            }
        }
        waitForGameOver();
    }

    // echo packet to client
    private void sendPacketToClient(DatagramPacket receivePacket, String outputMessage) throws IOException {
        System.out.println("\n\nEcho data to client...");
        receivePacket.setData(outputMessage.getBytes());

        // Use a fresh byte array so the packet size is based on the outgoing message,
        // not the length of the packet that was previously received.
        byte[] data = outputMessage.getBytes();

        // create packet to send
        DatagramPacket sendPacket = new DatagramPacket(
                data,
                data.length,
                receivePacket.getAddress(),
                receivePacket.getPort()
        );

        socket.send(sendPacket); // send packet to client
        System.out.println("Packet sent\n");
    }


    //DEPENDENT ON TIME
    private void waitForGameOver(){
        boolean gamePlaying = true;
        while(gamePlaying){
            try // receive packet, display contents, return copy to client
            {
                byte[] data = new byte[100]; // set up packet
                DatagramPacket receivePacket =
                        new DatagramPacket(data, data.length);

                socket.receive(receivePacket); // wait to receive packet
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                if(message.startsWith("COMPLETE:")) {
                    String playerInfo = message.substring("COMPLETE:".length());
                    String[] parts = playerInfo.split(",");

                    String username = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    int timeRemaining = Integer.parseInt(parts[2]);

                    leaderboard.addPlayer(username, score, timeRemaining);

                    sendPacketToAllClients("LEADERBOARD:" + leaderboard.toMessage());
                    sendPacketToClient(receivePacket, "GAME_OVER");

                }
            } catch (IOException ioException) {
                System.out.println(ioException + "\n");
                ioException.printStackTrace();
            }
        }
    }

    // Helper method for sending the same message to ALL clients, such as game start and game over occurring.
    private void sendPacketToAllClients(String outputMessage) throws IOException {
        byte[] data = outputMessage.getBytes();

        // Sending the output message to ALL clients through their Player Data objects.
        for (PlayerData player : players.values()) {
            DatagramPacket sendPacket = new DatagramPacket(
                    data,
                    data.length,
                    player.getAddress(),
                    player.getPort()
            );

            // Sending the output message to the clients.
            socket.send(sendPacket);
        }
    }
}