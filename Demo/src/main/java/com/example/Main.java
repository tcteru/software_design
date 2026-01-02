package com.example;

import com.example.View.View;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main application class for the JavaFX Application.
 */
public class Main extends Application {

    public static void main(String[] args) {

        // Käynnistää JavaFX
        launch(args);
    }

    /**
     * This method is called when the application starts.
     * @param stage the primary stage for this application
     */
    @Override
    public void start(Stage stage) {
        // create the view, the model, and the controller
        View view = new View();
        //ViewController controller = new ViewController(view);
        // Model model = new Model(view);
        // Controller controller = new Controller(view, model);

        Scene scene = new Scene(view, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final String WINDOW_TITLE = "Chart";
}