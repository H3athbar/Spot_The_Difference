import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LeaderWaitingScreenController {

    /**
     * TextField that displays username entered by the user
     */
    @FXML private TextField playerUsernameTextField;

    /**
     * displays the count of users in server
     */
    @FXML private TextField playerCountTextField;

    /**
     * Initialize to set player count and username
     */
    @FXML
    public void initialize() {
        SceneManager.setPlayerCountTextField(playerCountTextField);
        playerUsernameTextField.setText(ClientManager.getInstance().getUsername());
        playerCountTextField.setText(String.valueOf(ClientManager.getInstance().getPlayerCount()));
    }

    /**
     * When press start, game screen loads for all players
     * @param event when the button is pressed
     */
    @FXML
    private void leaderStartButton(ActionEvent event) {
        // Get access to the client through Client Manager, and send the command to the server to start game.
        ClientManager.getInstance().startGame();
    }
}
