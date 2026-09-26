import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Starts the client-side JavaFX application.
 */
public class ClientDriver extends Application{
    /**
     * Automatically runs when the ClientDriver class is called.
     *
     * @param args Command Line Arguments.
     */
    public static void main (String[] args) {
        launch(args);
    }

    /**
     * Starts the JavaFX application by loading the StartScreen.fxml layout,
     * creating the scene, and displaying the starting application window.
     *
     * @param stage The stage for this JavaFX application.
     * @throws Exception If the FXML file cannot be loaded.
     */
    @Override
    public void start (Stage stage) throws Exception {
        SceneManager.setStage(stage);

        Parent root = FXMLLoader.load(getClass().getResource("StartScreen.fxml"));
        Scene scene = new Scene(root, 1200, 800);

        // Preparing the window by setting the scene.
        stage.setScene(scene);

        // Sets the minimum width and height of the window
        stage.setMinWidth(1200);
        stage.setMinHeight(800);

        // Has the GUI open in fullscreen
        stage.setFullScreen(false);

        // Making it so that
        stage.setOnCloseRequest(event -> {
            ClientManager.getInstance().stopClient();
            Platform.exit();
            System.exit(0);
        });

        // Show the window to the user.
        stage.show();
    }
}
