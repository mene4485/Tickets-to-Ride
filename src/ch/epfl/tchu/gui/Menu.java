package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerClient;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

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
        window.setTitle("TchuTchu");


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
        server.setFont(StringsFr.font(15, "Light"));
        server.getStyleClass().add("server");


        ToggleButton client = new ToggleButton();
        client.setText("Client");
        client.setFont(StringsFr.font(15, "Light"));
        client.getStyleClass().add("client");


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
        serverBox.getChildren().addAll(server, joueur1Input, joueur2Input);

        //Client

        ipAdressInput.setMaxWidth(150);
        portInput.setMaxWidth(150);
        ipAdressText.setText("Ip Adress :");
        portText.setText("Port : ");

        ipAdress.getChildren().addAll(ipAdressText, ipAdressInput);
        port.getChildren().addAll(portText, portInput);

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


        Text t = new Text("tCHu 2.0");
        t.setFont(StringsFr.font(50, "Light"));
        t.setTextAlignment(TextAlignment.CENTER);

        VBox clientBox = new VBox();
        clientBox.getChildren().addAll(client, ipAdress, port);


        gridPane.addRow(0, serverBox, clientBox);
        gridPane.setAlignment(Pos.TOP_CENTER);
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(t, gridPane, jouer);

        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 600, 400);
        scene.getStylesheets().add("backGround.css");
        scene.getStylesheets().add("togglebuttonStyle.css");


        window.setScene(scene);
        window.getIcons().add(new Image("file:resources/icone.png"));
        window.show();


        jouer.setOnAction(s -> {
            if (server.isSelected()) {
                Platform.setImplicitExit(false);
                Platform.runLater(() -> System.out.println("Inside Platform.runLater()"));
                window.close();
                String j1 = name1input.getText();
                String j2 = name2input.getText();

                String player1name = j1.equals("") ? "Ada" : j1;
                String player2name = j2.equals("") ? "Charles" : j2;

                SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
                Random rng = new Random();
                ServerSocket serverSocket;
                try {
                    serverSocket = new ServerSocket(5108);
                    Socket socket = serverSocket.accept();
                    Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, player1name, PlayerId.PLAYER_2, player2name);

                    Map<PlayerId, Player> players =
                            Map.of(PLAYER_1, new GraphicalPlayerAdapter(),
                                    PLAYER_2, new RemotePlayerProxy(socket));
                    System.out.println("test1");
                    new Thread(() -> Game.play(players, playerNames, tickets, rng))
                            .start();
                    System.out.println("test2");

                } catch (IOException e) {
                    System.out.println("fv");
                    e.printStackTrace();
                }

            } else if (client.isSelected()) {
                Platform.setImplicitExit(false);
                Platform.runLater(() -> System.out.println("Inside Platform.runLater()"));
                window.close();
                String ipString = ipAdressInput.getText();
                String portString = portInput.getText();


                String hostName = ipString.equals("") ? "localhost" : ipString;
                int port1 = portString.equals("") ? 5108 : Integer.parseInt(portString);


                RemotePlayerClient playerClient =
                        new RemotePlayerClient(new GraphicalPlayerAdapter(),
                                hostName,
                                port1);

                new Thread(playerClient::run).start();

            }
        });
    }
}
