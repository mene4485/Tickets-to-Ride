package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


/**
 * Class creating the decks view for the player's interface
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
class DecksViewCreator {
    private static final int RECTANGLE_HEIGHT = 70;
    private static final int RECTANGLE_WIDTH = 40;

    /**
     * Create the hand view of a player
     *
     * @param observableGameState the ObservableGameState of the player
     * @return a hand view (the bottom part of the interface) that updates depending on <i>observableGameState</i>
     */
    public static HBox createHandView(ObservableGameState observableGameState) {

        ListView<Ticket> view = new ListView<>(observableGameState.getPlayerTickets());
        view.setId("tickets");

        HBox hBox = new HBox();
        hBox.getChildren().add(view);
        hBox.getStylesheets().addAll("decks.css", "colors.css");

        HBox hBox1 = new HBox();
        hBox1.setId("hand-pane");


        for (Card card : Card.ALL) {
            Text counter = new Text();
            counter.getStyleClass().add("count");
            ReadOnlyIntegerProperty count = observableGameState.playerCardsCountProperty(card);
            counter.textProperty().bind(Bindings.convert(count));
            counter.visibleProperty().bind(Bindings.greaterThan(count, 1));

            StackPane stackPane = createRectangle();
            String color = card.color() == null ? "NEUTRAL" : card.color().name();
            stackPane.getStyleClass().addAll(color, "card");
            stackPane.visibleProperty().bind(Bindings.greaterThan(count, 0));

            stackPane.getChildren().add(counter);

            hBox1.getChildren().add(stackPane);
        }
        hBox.getChildren().add(hBox1);

        return hBox;
    }


    /**
     * Create the cards view of a player
     *
     * @param observableGameState the ObservableGameState of the player
     * @param ticketHandler       handler used when the player wants to draw tickets
     * @param cardHandler         handler used when the player wants to draw cards
     * @return a Cards view (right part of the interface) that updates depending on <i>observableGameState</i>
     */
    public static Node createCardsView(ObservableGameState observableGameState, ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketHandler, ObjectProperty<ActionHandlers.DrawCardHandler> cardHandler) {
        VBox vBox = new VBox();
        vBox.setId("card-pane");
        vBox.getStylesheets().addAll("decks.css", "colors.css");

        //add the ticket's button
        Button buttonGraphicTicket = buttonGraphicCreatorTicket(observableGameState);
        buttonGraphicTicket.getStyleClass().add("gauged");
        buttonGraphicTicket.setText(StringsFr.TICKETS);
        buttonGraphicTicket.disableProperty().bind(ticketHandler.isNull());

        buttonGraphicTicket.setOnMouseClicked(event -> ticketHandler.get().onDrawnTickets());

        vBox.getChildren().add(buttonGraphicTicket);

        //add the face up cards
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
            ReadOnlyObjectProperty<Card> card = observableGameState.cardStateFUC(i);

            StackPane stackPane = createRectangle();

            card.addListener((owner, old, newValue) -> {
                String color = newValue.color() == null ? "NEUTRAL" : newValue.name();
                stackPane.getStyleClass().set(0, color);
            });
            stackPane.getStyleClass().addAll("null", "card");
            int j = i;
            stackPane.setOnMouseClicked(e -> cardHandler.get().onDrawCard(j));


            vBox.getChildren().add(stackPane);
            stackPane.disableProperty().bind(cardHandler.isNull());
        }


        //add the cards deck's button
        Button buttonGraphicCard = buttonGraphicCreatorDeck(observableGameState);

        buttonGraphicCard.getStyleClass().add("gauged");
        buttonGraphicCard.setText(StringsFr.CARDS);
        buttonGraphicCard.setOnMouseClicked(e -> cardHandler.get().onDrawCard(Constants.DECK_SLOT));

        buttonGraphicCard.disableProperty().bind(cardHandler.isNull());

        vBox.getChildren().add(buttonGraphicCard);


        return vBox;
    }

    private static StackPane createRectangle() {
        Rectangle outside = new Rectangle(60, 90);
        outside.getStyleClass().add("outside");

        Rectangle inside = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
        inside.getStyleClass().addAll("filled", "inside");

        Rectangle trainImage = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
        trainImage.getStyleClass().add("train-image");

        return new StackPane(outside, inside, trainImage);
    }

    private static Button buttonGraphicCreatorTicket(ObservableGameState observableGameState) {
        ReadOnlyIntegerProperty percentageProperty = observableGameState.gameTicketsPercentageProperty();
        return buttonGraphicCreatorBase(percentageProperty);
    }

    private static Button buttonGraphicCreatorDeck(ObservableGameState observableGameState) {
        ReadOnlyIntegerProperty percentageProperty = observableGameState.cardStateDeckPercentageProperty();
        return buttonGraphicCreatorBase(percentageProperty);
    }

    private static Button buttonGraphicCreatorBase(ReadOnlyIntegerProperty percentageProperty) {
        Rectangle background = new Rectangle(50, 5);
        background.getStyleClass().add("background");
        Rectangle foreground = new Rectangle(50, 5);
        foreground.getStyleClass().add("foreground");

        foreground.widthProperty().bind(percentageProperty.multiply(50).divide(100));

        Group buttonGraphic = new Group(background, foreground);
        Button b = new Button();
        b.setGraphic(buttonGraphic);
        return b;
    }

}
