import javafx.fxml.FXML;
import java.util.ArrayList;
import javafx.scene.control.TextArea;


public class GameOverController {
    /**
     * Text area for the players' names
     */
    @FXML
    private TextArea playerLeaderboardText;
    /**
     * Text area for the players' scores
     */
    @FXML
    private TextArea scoreLeaderboardText;
    /**
     * Text area for the time it took each player to complete
     */
    @FXML
    private TextArea timeLeaderboardText;

    /**
     * Leaderboard object to store the players that have finished, along with their scores and times
     */
    private Leaderboard leaderboard;

    /**
     * Automatically runs when the GUI opens and displays the leaderboard
     */
    @FXML
    public void initialize(){
        SceneManager.setGameOverController(this);
        // Gets the current leaderboard from ClientManager
        leaderboard = ClientManager.getInstance().getLeaderboard();
        // Shows the leaderboard on the gameOver screen
        updateLeaderboard();
    }

    /**
     * Updates the 3 TextAreas with the leaderboard information
     */
    public void updateLeaderboard(){
        String names = "";
        String scores = "";
        String times = "";

        // Gets sorted leaderboard
        ArrayList<PlayerResult> entries = leaderboard.getEntries();

        // Loops through each leaderboard entry
        for(int i = 0; i < entries.size(); i++){
            // Gets the entry for the current player
            PlayerResult entry = entries.get(i);

            // Adds the player's rank with their name, score, and time to the column
            names += (i+1) + ". " + entry.getUsername() + "\n";
            scores += entry.getScore() + "\n";
            times += entry.getTimeRemaining() + "\n";

        }

        // Sets the text for the three TextAreas to the completed columns of the leaderboard
        playerLeaderboardText.setText(names);
        scoreLeaderboardText.setText(scores);
        timeLeaderboardText.setText(times);
    }

    /**
     * Sets the leaderboard
     * @param leaderboard
     */
    public void setLeaderboard(Leaderboard leaderboard){
        this.leaderboard = leaderboard;
        updateLeaderboard();
    }
}
