import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Helps the other classes (mainly Client) with changing the stage of it (window).
 */
public class SceneManager {
    // The stage the user is currently on.
    private static Stage stage;

    private static TextField playerCountTextField;
    private static GameOverController gameOverController;

    /**
     * Setter method for the stage private variable.
     *
     * @param stage The current stage the user is looking at.
     */
    public static void setStage(Stage stage) {
        SceneManager.stage = stage;
    }

    /**
     * Switches the scene/window.
     *
     * @param fxml The name of the fxml file.
     */
    public static void switchScene(String fxml) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxml));
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setter to change the player count text field to be the input text field.
     *
     * @param textField The input text field.
     */
    public static void setPlayerCountTextField(TextField textField) {
        playerCountTextField = textField;
    }


    public static void updatePlayerCountText(int playerCount) {
        if (playerCountTextField != null) {
            playerCountTextField.setText(String.valueOf(playerCount));
        }
    }

    /**
     * Sets the gameOverController to allow the leaderboard to have live updates
     * @param controller the game over controller
     */
    public static void setGameOverController(GameOverController controller){
        gameOverController = controller;
    }
    /**
     * Updates the leaderboard display if the gameOver screen is open
     */
    public static void updateLeaderboard(Leaderboard leaderboard){
        if (gameOverController != null){
            gameOverController.setLeaderboard(leaderboard);
        }
    }
}
