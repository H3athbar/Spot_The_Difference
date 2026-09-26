import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class PlayerWaitingScreenController {

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
}
