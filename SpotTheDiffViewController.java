import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * This class handles the fxml file components for the portion of the game where each player is presented the image
 * pair of the original and changed photo, then must find the differences in the changed photo on the window.
 */
public class SpotTheDiffViewController {

    /**
     * The location of the player's mouse at the x-axis.
     */
    private double mouseXLocation;

    /**
     * The location of the players mouse on the y-axis.
     */
    private double mouseYLocation;

    /**
     * The current user's score in the game.
     */
    private int score = 0;

    /**
     * The original photo in the pair.
     */
    @FXML
    private ImageView photoOriginal;

    /**
     * The photo that is different from the original.
     */
    @FXML
    private ImageView photoChanges;

    /**
     * The current score of the user.
     */
    @FXML
    private TextField scoreTextBox;

    /**
     * Displays how much time is left.
     */
    @FXML
    private TextField timeTextBox;

    /**
     * Holds the username of the player.
     */
    @FXML
    private TextField userNameTextBox;

    @FXML
    private StackPane changesImagePane;

    /**
     * Pane used to draw markers on top of the changed image.
     */
    @FXML
    private Pane markerPane;

    /**
     * The current pair of images with the difference coordinates
     */
    private Images images;

    /**
     * Boolean array to track if the user has already clicked the "Different" spot.
     */
    private boolean[] foundDifferent;

    /**
     * The timeline used to count down the player's timer. Works with the timeLeft variable.
     */
    private Timeline timer;

    /**
     * The penalty of time lost when the user doesn't click on a difference at all.
     */
    private final int WRONG_CLICK_PENALTY = 5;

    /**
     * How much time is left in the game. Works with the timer field to track the time portion of the game.
     */
    private int timeLeft;

    /**
     * Tracks if the game has ended. Needed for the timer field.
     */
    private boolean gameCompleted = false;

    /**
     * Initializes the selected set of images in the GUI.
     */
    @FXML
    public void initialize(){
        // Obtaining the image choice through the client manager to the server.
        int imageChoice = ClientManager.getInstance().getImageChoice();
        // Choosing the images (Original and changes) for the game.
        images = new Images(imageChoice);

        // Setting the username in the user text box.
        userNameTextBox.setText(ClientManager.getInstance().getUsername());

        // Creating the array and setting the capacity for it using the number of coords from the images object.
        foundDifferent = new boolean[images.getMinXCoordinates().length];
        // Setting the score in the score text box at the start of the game.
        scoreTextBox.setText(String.valueOf(score));

        // Setting the images for the image views.
        photoOriginal.setImage(images.getOriginalImage());
        photoChanges.setImage(images.getChangesImage());

        // Initializing the time left and time text box fields by communicating through the client manager.
        timeLeft = ClientManager.getInstance().getGameTimeSeconds();
        timeTextBox.setText(String.valueOf(timeLeft));

        // Creating the timer object using the timeline and keyframe class.
        timer = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    // Reducing the time left by one for every second, and putting it into the text box.
                    timeLeft--;

                    // Making sure the timer isn't a negative value.
                    if (timeLeft < 0) {
                        timeLeft = 0;
                    }

                    timeTextBox.setText(String.valueOf(timeLeft));

                    // If the time left is 0, then the game has completed for the client.
                    if (timeLeft == 0) {
                        playerGameFinished();
                    }
                })
        );

        // Setting the cycle of the timer and playing it.
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    /**
     * Records where the user clicked on the second image, and if the click matches with one of the coordinates of the
     * differences, or is close enough, will give points to the player.
     *
     * @param event When the user clicks on the changed image (Second image).
     */
    @FXML
    void clickOnImage(MouseEvent event) {
        if (gameCompleted) {
            return;
        }

        mouseXLocation = event.getX(); // X-Coordinate of player click
        mouseYLocation = event.getY(); // Y-Coordinate of player click

        // Gets arrays for the X,Y Coordinates of the differences for this image pair
        Double[] minXCoordinates = images.getMinXCoordinates();
        Double[] minYCoordinates = images.getMinYCoordinates();
        Double[] maxXCoordinates = images.getMaxXCoordinates();
        Double[] maxYCoordinates = images.getMaxYCoordinates();

        // Stores the number of differences this image pair has
        int numOfDiff = foundDifferent.length;

        boolean foundDifference = false;

        /* Loops through the differences to see if the coordinates for the
        mouse click were within the range of any differences
         */
        for(int i = 0; i < numOfDiff; i++){
            // Skipping the differences the player has already found.
            if (foundDifferent[i]) {
                continue;
            }

            // Checks if the click was within the allowed range
            if(mouseXLocation > minXCoordinates[i] && mouseXLocation < maxXCoordinates[i]
                    && mouseYLocation > minYCoordinates[i] && mouseYLocation < maxYCoordinates[i]) {
                // Mark that the spot has already been found.
                foundDifferent[i] = true;
                score++;
                scoreTextBox.setText(String.valueOf(score));
                foundDifference = true;

                // Add a marker only on this player's screen.
                addMarker(mouseXLocation, mouseYLocation);

                // TESTING
                System.out.printf("DIFFERENCE " + i + " FOUND");
                break;
            }
        }

        // If the player clicks the wrong spot
        if(!foundDifference){
            System.out.println("WRONG");

            // Reduce the time left by the penalty constant defined.
            timeLeft = timeLeft - WRONG_CLICK_PENALTY;

            // Resetting the timer back to 0 so it isn't a negative number.
            if (timeLeft < 0) {
                timeLeft = 0;
            }

            // editing the timer text box.
            timeTextBox.setText(String.valueOf(timeLeft));

            // IF the timer is at zero seconds, then finish the game.
            if (timeLeft == 0) {
                playerGameFinished();
            }
        }

        //testing:
        System.out.println("(" + mouseXLocation + ", " + mouseYLocation + ")");

        // If a player has found ALL spots, send message using client manager to server to complete the game.
        if (score == numOfDiff) {
            playerGameFinished();
        }

        // Updating the score box.
        scoreTextBox.setText(String.valueOf(score));
    }

    private void addMarker(double x, double y) {
        // Convert the click point from the ImageView's coordinates to scene coordinates.
        Point2D scenePoint = photoChanges.localToScene(x, y);

        // Convert the scene coordinates into the marker pane's coordinate system.
        Point2D markerPanePoint = markerPane.sceneToLocal(scenePoint);

        Circle marker = new Circle(12);

        marker.setCenterX(markerPanePoint.getX());
        marker.setCenterY(markerPanePoint.getY());

        marker.setFill(Color.TRANSPARENT);
        marker.setStroke(Color.RED);
        marker.setStrokeWidth(3);

        // Prevents the circle from blocking future clicks.
        marker.setMouseTransparent(true);

        markerPane.getChildren().add(marker);
    }

    /**
     * Helper function that goes through the process of finishing the game. IS called when the time left is 0,
     * or all the difference have been found.
     */
    private void playerGameFinished() {
        // Making sure the game hasn't already completed yet,
        if (gameCompleted) {
            return;
        }

        // Setting the game complete field to true.
        gameCompleted = true;

        // Making sure the timer object isn't null.
        if (timer != null) {
            timer.stop();
        }

        // Communicating to the client manager to tell the client that the game is over and sends score and remaining time
        ClientManager.getInstance().sendComplete(score, timeLeft);
    }

    private class Images {
        /**
         * original image chosen to play
         */
        private final Image originalImage;
        /**
         * changed image chosen to play
         */
        private final Image changesImage;
        /**
         * array of all the difference min x coordinates corresponding to the image chosen to play
         */
        private final Double[] minXCoordinates;
        /**
         * array of all the difference max x coordinates corresponding to the image chosen to play
         */
        private final Double[] maxXCoordinates;
        /**
         * array of all the difference min y coordinates corresponding to the image chosen to play
         */
        private final Double[] minYCoordinates;
        /**
         * array of all the difference max y coordinates corresponding to the image chosen to play
         */
        private final Double[] maxYCoordinates;

        /**
         * Array holding all possible image paths of original images
         */
        private static final String[] ORIGINAL_IMAGES = new String[]
                {"/spongeBobOriginal.png", "/foodOriginal.png", "/comicOriginal.png"};
        /**
         * Array holding all possible image paths of changed images
         */
        private static final String[] DIFFERENT_IMAGES = new String[]
                {"/spongeBobChanges.png", "/foodChanges.png", "/comicChanges.png"};
        /**
         * Array holding all possible min x coordinate of differences
         */
        private static final Double[][] MIN_X_COORDINATES = new Double[14][];

        /**
         * Array holding all possible max x coordinate of differences
         */
        private static final Double[][] MAX_X_COORDINATES = new Double[14][];

        /**
         * Array holding all possible min y coordinate of differences
         */
        private static final Double[][] MIN_Y_COORDINATES = new Double[14][];

        /**
         * Array holding all possible max y coordinate of differences
         */
        private static final Double[][] MAX_Y_COORDINATES = new Double[14][];

        /**
         * Implement coordinates and select chosen image
         */
        public Images(int choice){
            //initialize final arrays
            //spongebob
            MIN_X_COORDINATES[0]= new Double[]{300.0, 290.0, 205.0};
            MIN_Y_COORDINATES[0]= new Double[]{155.0, 30.0, 125.0};
            MAX_X_COORDINATES[0]= new Double[]{350.0, 330.0, 225.0};
            MAX_Y_COORDINATES[0]= new Double[]{203.0, 65.0, 145.0};

            //food
            MIN_X_COORDINATES[1]= new Double[]{85.0, 77.0, 205.0, 30.0, 180.0};
            MIN_Y_COORDINATES[1]= new Double[]{25.0, 83.0, 13.0, 160.0, 224.0};
            MAX_X_COORDINATES[1]= new Double[]{103.0, 103.0, 265.0, 70.0, 265.0};
            MAX_Y_COORDINATES[1]= new Double[]{43.0, 109.0, 66.0, 290.0, 365.0};

            //comic
            MIN_X_COORDINATES[2]= new Double[]{32.0, 0.0, 50.0, 150.0, 110.0, 195.0, 216.0, 206.0, 254.0, 19.0, 79.0, 210.0, 118.0, 102.0};
            MIN_Y_COORDINATES[2]= new Double[]{25.0, 72.0, 95.0, 80.0, 125.0, 172.0, 181.0, 237.0, 251.0, 199.0, 222.0, 332.0, 350.0, 361.0};
            MAX_X_COORDINATES[2]= new Double[]{52.0, 35.0, 65.0, 295.0, 130.0, 212.0, 223.0, 248.0, 299.0, 53.0, 124.0, 242.0, 130.0, 116.0};
            MAX_Y_COORDINATES[2]= new Double[]{46.0, 138.0, 170.0, 90.0, 146.0, 190.0, 193.0, 259.0, 268.0, 210.0, 266.0, 361.0, 357.0, 374.0};

            //choice selected image
            originalImage = new Image(getClass().getResource(ORIGINAL_IMAGES[choice]).toExternalForm());
            changesImage = new Image(getClass().getResource(DIFFERENT_IMAGES[choice]).toExternalForm());

            minXCoordinates = MIN_X_COORDINATES[choice];
            minYCoordinates = MIN_Y_COORDINATES[choice];
            maxXCoordinates = MAX_X_COORDINATES[choice];
            maxYCoordinates = MAX_Y_COORDINATES[choice];
        }

        /**
         * Returns the selected original image
         * @return the original JavaFX image
         */
        public Image getOriginalImage() {
            return originalImage;
        }
        /**
         * Returns the selected changed image
         * @return the changed JavaFX image
         */
        public Image getChangesImage() {
            return changesImage;
        }

        /**
         * Returns the min x coordinates of difference of selected image
         * @return double array containing all the min x coordinates of image differences
         */
        public Double[] getMinXCoordinates() {
            return minXCoordinates;
        }

        /**
         * Returns the max x coordinates of difference of selected image
         * @return double array containing all the max x coordinates of image differences
         */
        public Double[] getMaxXCoordinates() {
            return maxXCoordinates;
        }

        /**
         * Returns the min y coordinates of difference of selected image
         * @return double array containing all the min y coordinates of image differences
         */
        public Double[] getMinYCoordinates() {
            return minYCoordinates;
        }

        /**
         * Returns the max y coordinates of difference of selected image
         * @return double array containing all the max y coordinates of image differences
         */
        public Double[] getMaxYCoordinates(){
            return maxYCoordinates;
        }

    }


}
