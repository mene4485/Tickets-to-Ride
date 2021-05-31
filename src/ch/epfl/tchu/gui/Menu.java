package ch.epfl.tchu.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
        GridPane gridPane = new GridPane();

        //Serveur
        TextField name1input = new TextField();
        TextField name2input = new TextField();
        name1input.setMaxWidth(150);
        name2input.setMaxWidth(150);
        Text joueur1 = new Text();
        Text joueur2 = new Text();
        joueur1.setText("Joueur 1");
        joueur2.setText("Joueur 2");

        TextField ipAdressInput = new TextField();
        TextField portInput = new TextField();
        Text ipAdressText = new Text();
        Text portText = new Text();
        VBox ipAdress = new VBox();
        VBox port = new VBox();

        VBox joueur1Input = new VBox();
        joueur1Input.getChildren().addAll(joueur1, name1input);
        VBox joueur2Input = new VBox();
        joueur2Input.getChildren().addAll(joueur2, name2input);

        ToggleButton server = new ToggleButton();
        server.setText("Server");
        ToggleButton client = new ToggleButton();
        client.setText("Client");

        joueur1Input.setVisible(false);
        joueur2Input.setVisible(false);

        server.setOnAction(s -> {
            joueur1Input.setVisible(server.isSelected());
            joueur2Input.setVisible(server.isSelected());
            client.setSelected(false);
            ipAdress.setVisible(false);
            port.setVisible(false);
        });

        VBox serverBox = new VBox();
        serverBox.getChildren().addAll(server,joueur1Input,joueur2Input);

        //Client

        ipAdressInput.setMaxWidth(150);
        portInput.setMaxWidth(150);
        ipAdressText.setText("Ip Adress :");
        portText.setText("Port : ");

        ipAdress.getChildren().addAll(ipAdressText, ipAdressInput);
        port.getChildren().addAll(portText,portInput);

        ipAdress.setVisible(false);
        port.setVisible(false);

        client.setOnAction(s -> {
            ipAdress.setVisible(client.isSelected());
            port.setVisible(client.isSelected());
            server.setSelected(false);
            joueur1Input.setVisible(false);
            joueur2Input.setVisible(false);
        });


        Button jouer = new Button();
        jouer.setText("Jouer !");

        jouer.setOnAction(s->{
            if(server.isSelected()){

            }
        });

        VBox clientBox = new VBox();
        clientBox.getChildren().addAll(client,ipAdress,port);

        gridPane.addRow(0,serverBox,clientBox);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(gridPane);

        Scene scene = new Scene(layout, 500, 500);
        window.setScene(scene);
        window.show();
    }
}
