public class PlayerResult {
    /**
     * Username of the player
     */
    private String username;
    /**
     * Score of the player
     */
    private int score;
    /**
     * Time player had left when they finished the game
     */
    private int timeRemaining;

    /**
     * Constructor to create PlayerResult object to add to leaderboard
     * @param username the name of the player
     * @param score the player's score
     * @param timeRemaining the time the player had left when they finished
     */
    public PlayerResult(String username, int score, int timeRemaining){
        this.username = username;
        this.score = score;
        this.timeRemaining = timeRemaining;
    }

    /**
     * Gets the username of the player
     * @return username
     */
    public String getUsername(){
        return username;
    }

    /**
     * Gets the score of the player
     * @return score
     */
    public int getScore() {
        return score;
    }

    /**
     * Gets the time the player had left when they finished the game
     * @return the remaining time
     */
    public int getTimeRemaining() {
        return timeRemaining;
    }
}
