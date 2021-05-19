package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Class creating the map view for the player's interface
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
class MapViewCreator {
    private static final int RADIUS = 3;
    private static final int CENTER_X = 12;
    private static final int CENTER_Y = 6;
    private static final int RECTANGLE_HEIGHT = 12;
    private static final int RECTANGLE_WIDTH = 36;

    /**
     * Create the map view of a player
     *
     * @param observableGameState       the ObservableGameState of a player
     * @param claimRouteHandlerProperty handler used when the player wants to claim a Route
     * @param cardChooser               way used to choose the cards to claim a route
     * @return The MapView (central part of the interface) that updates depending on <i>observableGameState</i>
     */
    public static Node createMapView(ObservableGameState observableGameState, ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHandlerProperty, CardChooser cardChooser) {

        ImageView view = new ImageView();
        ImageView planes = new ImageView("plane.png");
        view.getStyleClass().add("map");
        //planes.getStyleClass().add("plane");
        planes.setX(40);
        planes.setY(40);
        planes.setFitHeight(40);
        planes.setFitWidth(40);


        Pane gamePane = new Pane();
        gamePane.getChildren().add(view);
        gamePane.getStylesheets().addAll("map.css", "colors.css");
        gamePane.getChildren().add(planes);

        //routes
        for (Route route : ChMap.routes()) {

            List<Node> list = new ArrayList<>();

            for (int i = 1; i <= route.length(); i++) {
                Rectangle r2 = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
                r2.getStyleClass().add("filled");
                Circle c1 = new Circle(CENTER_X, CENTER_Y, RADIUS);
                Circle c2 = new Circle(CENTER_X * 2, CENTER_Y, RADIUS);

                Node wagonGroup = new Group(r2, c1, c2);
                wagonGroup.getStyleClass().add("car");

                Rectangle voie = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
                voie.getStyleClass().addAll("track", "filled");

                Node caseNode = new Group(voie, wagonGroup);


                caseNode.setId(new StringBuilder().append(route.id()).append("_").append(i).toString());
                list.add(caseNode);
            }
            Node routeNode = new Group(list);
            routeNode.setId(route.id());

            observableGameState.routesProperty(route).addListener((owner, old, newValue) -> {
                String p = newValue.name();
                routeNode.getStyleClass().add(p);
            });

            routeNode.getStyleClass().addAll("route", route.level().name(), route.color() == null ? "NEUTRAL" : route.color().name());
            routeNode.disableProperty().bind(claimRouteHandlerProperty.isNull().or(observableGameState.getClaimableRoute(route).not()));

            routeNode.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
                    List<SortedBag<Card>> possibleClaimCards = observableGameState.getPlayerState().possibleClaimCards(route);
                    ActionHandlers.ClaimRouteHandler claimRouteH = claimRouteHandlerProperty.get();

                    if (possibleClaimCards.size() == 1) {
                        claimRouteH.onClaimRoute(route, possibleClaimCards.get(0));
                    } else {
                        ActionHandlers.ChooseCardsHandler chooseCardsH =
                                chosenCards -> claimRouteH.onClaimRoute(route, chosenCards);
                        cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                    }
                }
            });


            gamePane.getChildren().add(routeNode);
        }

        return gamePane;
    }

    /**
     * Represent the way to choose the cards to claim a route
     */
    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ActionHandlers.ChooseCardsHandler handler);
    }
}
