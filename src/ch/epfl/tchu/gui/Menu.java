package ch.epfl.tchu.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author Albert Troussard (330361)
 * @author Menelik Nouvellon (328132)
 */
public class Menu extends Application {
    static Button button;
    static Stage window;
    static TextField nameInput;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("TchuTchu Ma gueule");

        nameInput = new TextField();
        button = new Button("Ok");
        button.setOnAction(e -> {
            System.out.println(nameInput.getText());
            if (nameInput.getText().equals("Jouer")) {
                Stage11Test s = new Stage11Test();
                s.start(primaryStage);
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20,20,20,20));
        layout.getChildren().addAll(nameInput,button);

        Scene scene = new Scene(layout, 300, 250);
        window.setScene(scene);
        window.show();
    }
}
