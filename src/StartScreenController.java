import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javax.swing.*;

public class StartScreenController {
    /**
     * TextField that holds username entered by the user
     */
    @FXML private TextField playerUsername;

    /**
     * displays the count of users in server
     */
    @FXML private TextField playerCountTextField;

    /**
     * When gui is opened set player count from scene manager and display
     */
    @FXML
    public void initialize() {
        SceneManager.setPlayerCountTextField(playerCountTextField);
        ClientManager.getInstance().requestPlayerCount();
    }

    /**
     * When press join, gather username and player to games, changing to the loading screen
     * @param event when the button is pressed
     */
    @FXML
    private void playerJoinButton(ActionEvent event){
        // Storing the username from the text field to send to the server.
        String username = playerUsername.getText();

        // Checking to make sure it isn't null or blank.
        if (username == null || username.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username cannot be blank.",
                    "Enter valid username" , JOptionPane.ERROR_MESSAGE);

            return;
        }

        // Using the client manager class to get access to the client, and sending the PLAYER or LEADER message to the
        // server to change the scene, plus sending the username to store in the server.
        ClientManager.getInstance().joinGame(username);
    }

}
