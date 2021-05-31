package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

/**
 * Class used to represent a GraphicalPlayer as a Player
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 * @see Player
 */
public final class GraphicalPlayerAdapter implements Player {
    private final int BLOCKING_QUEUE_CAPACITY = 1;

    private final BlockingQueue<SortedBag<Ticket>> ticketQueue;
    private final BlockingQueue<Integer> drawCardQueue;
    private final BlockingQueue<Route> routeQueue;
    private final BlockingQueue<SortedBag<Card>> cardQueue;

    private GraphicalPlayer graphicalPlayer;

    /**
     * Empty constructor (so the class can be instanced)
     */
    public GraphicalPlayerAdapter() {
        ticketQueue = new ArrayBlockingQueue(BLOCKING_QUEUE_CAPACITY);
        drawCardQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        routeQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        cardQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
    }

    /**
     * Set the GraphicalPlayer with the given parameters on the javafx thread
     *
     * @param ownId       player's identity
     * @param playerNames Map that maps a player to this name
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
    }

    /**
     * Calls the method of the same name of <i>graphicalPlayer</i> on the javafx thread
     *
     * @param info the string that contains the information (from class Info)
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**
     * Call the method setState of GraphicalPlayer on the javafx thread
     *
     * @param newState new game state that needs to be updated to
     * @param ownState state of the concerned player
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    /**
     * Call the method chooseTickets of GraphicalPlayer on the javafx thread
     * the ChooseTicketsHandler unique method consist here on just adding the sorted bag of tickets to a blockingQueue
     *
     * @param tickets that have been distributed
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.chooseTickets(tickets, ticketQueue::add));
    }

    /**
     * @return the element at the head of the queue(and remove it from the queue)
     * @throws Error
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return takeWithTryCatch(ticketQueue);
    }

    /**
     * Call the method startTurn of GraphicalPlayer on the javafx thread
     * the DrawTicketsHandler unique method consist here on adding the turnKind to a blockingQueue
     * the DrawCardsHandler   unique method consist here on adding the turnKind to a blockingQueue and the slot to an other blockingQueue
     * the ClaimRouteHandler  unique method consist here on adding the turnKind to a blockingQueue and the route and the cards to different blockingQueue
     *
     * @return the type of action the player wants to do (head of the blockingQueue)
     * @throws Error
     */
    @Override
    public TurnKind nextTurn() {
        BlockingQueue<TurnKind> turnKind = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);

        runLater(() -> graphicalPlayer.startTurn(() -> turnKind.add(TurnKind.DRAW_TICKETS)
                , slot -> {
                    turnKind.add(TurnKind.DRAW_CARDS);
                    drawCardQueue.add(slot);

                }, (route, cards) -> {
                    turnKind.add(TurnKind.CLAIM_ROUTE);
                    routeQueue.add(route);
                    cardQueue.add(cards);

                }
        ));

        return takeWithTryCatch(turnKind);
    }

    /**
     * call setInitialTicketChoice with <i>options</i>
     *
     * @param options the ticket's option given to the player
     * @return the element at the head of the queue
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        setInitialTicketChoice(options);
        return chooseInitialTickets();

    }

    /**
     * @return the slot of the card the player wants to take
     * @throws Error
     */
    @Override
    public int drawSlot() {
        if (drawCardQueue.isEmpty()) {
            runLater(() -> graphicalPlayer.drawCard(drawCardQueue::add));
        }
        return takeWithTryCatch(drawCardQueue);
    }

    /**
     * @return the route the player wants to claim
     */
    @Override
    public Route claimedRoute() {
        return takeWithTryCatch(routeQueue);
    }

    /**
     * @return the cards the player wants to use to initially claim  the route
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        return cardQueue.remove();
    }

    /**
     * Call chooseAdditionalCards of GraphicalPlayer on the javafx thread
     * the ChooseCardsHandler unique method consist here on just adding the cards to a blockingQueue
     * then return these cards
     *
     * @param options the possibilities given to the player
     * @return the cards choose by the player
     * @throws Error
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        assert (cardQueue.isEmpty());

        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, cardQueue::add));

        return takeWithTryCatch(cardQueue);
    }


    private <T> T takeWithTryCatch(BlockingQueue<T> t) {
        try {
            return t.take();
        } catch (Exception e) {
            throw new Error();
        }
    }
}
