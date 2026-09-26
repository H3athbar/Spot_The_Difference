import java.util.ArrayList;

public class Leaderboard {
    /**
     * The list of players that have finished the game and are entered in the leaderboard
     */
    private ArrayList<PlayerResult> entries;

    /**
     * Constructor for Leaderboard
     * Creates an empty leaderboard list to be populated by
     * PlayerResult objects
     */
    public Leaderboard(){
        entries = new ArrayList<>();
    }

    /**
     * Adds a player to the leaderboard
     * @param username name of player
     * @param score of the player
     * @param timeRemaining the time left when the player finished
     */
    public void addPlayer(String username, int score, int timeRemaining){
        PlayerResult newEntry = new PlayerResult(username, score, timeRemaining);

        for(int i = 0; i < entries.size(); i++){
            if(rankedHigher(newEntry, entries.get(i))){
                entries.add(i, newEntry);
                return;
            }
        }
        // If newEntry doesn't rank higher than any of the current entries it gets added at the end
        entries.add(newEntry);
    }

    /**
     * Returns the list of leaderboard entries
     * @return entries
     */
    public ArrayList<PlayerResult> getEntries() {
        return entries;
    }

    /**
     * Flag to decide if a new leaderboard entry should be placed above an existing one
     * @return true if the new player entry should be higher than a current entry, false otherwise
     */
    private boolean rankedHigher(PlayerResult newEntry, PlayerResult currentEntry){
        // Checks score first
        if(newEntry.getScore() > currentEntry.getScore()){
            return true; // If new score is higher -> true
        }
        if(newEntry.getScore() < currentEntry.getScore()){
            return false; // Otherwise -> false
        }
        // Then if scores are tied -> check time remaining when they finished
        if(newEntry.getScore() == currentEntry.getScore()){
            if(newEntry.getTimeRemaining() > currentEntry.getTimeRemaining()){
                return true; // If time remaining is more than existing entry -> true
            }
            if(newEntry.getTimeRemaining() < currentEntry.getTimeRemaining()){
                return false; // If time remaining is less than existing entry -> false
            }
        }
        // Last case happens if scores and times are tied
        return false; //change once we add logic for who found their most recent difference first
    }

    /**
     * Converts leaderboard entries into a message that can be sent to clients
     * @return the leaderboard as a String message
     */
    public String toMessage(){
        String message = "";
        // Loops through all the leaderboard entries
        for(int i = 0; i < entries.size(); i++){
            PlayerResult entry = entries.get(i);
            // Adds the player data to the message
            message += entry.getUsername() + "," + entry.getScore() + "," + entry.getTimeRemaining();
            // Adds a separator between the entries, except the last
            if(i < entries.size() - 1){
                message += "|";
            }
        }
        // Returns the completed message
        return message;
    }
}
