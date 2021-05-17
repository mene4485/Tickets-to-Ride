package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */

public final class PlayerTestRandom implements Player {
    private static final int TURN_LIMIT = 1000;
    private final Random rand;
    private PlayerId ownId;
    private Map<PlayerId, String> playerNames;
    private static Scanner scanner = new Scanner(System.in);
    private SortedBag<Ticket> choosenTickets;
    private PlayerState state;
    private Route actualRoute;
    private PublicGameState gameState;
    int turnCount;
    private List<Route> allRoutes;

    PlayerTestRandom(long randomseed, List<Route> allRoutes) {
        this.rand = new Random(randomseed);
        this.allRoutes = allRoutes;
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        this.ownId = ownId;
        this.playerNames = playerNames;
        this.turnCount = 0;
    }

    @Override
    public void receiveInfo(String info) {
        System.out.println(playerNames.get(ownId) + ": " + info);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        gameState = newState;
        state = ownState;
        System.out.println("<>/*/*/*/*/*/*/<>STATE UPDATED<>/*/*/*/*/*/*/*/*/*/<>");
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        System.out.println("Quelles tickets? (Au moins 3)");
        Ticket t1 = tickets.get(0);
        System.out.println("Ticket 1 " + t1.text());
        Ticket t2 = tickets.get(1);
        System.out.println("Ticket 2 " + t2.text());
        Ticket t3 = tickets.get(2);
        System.out.println("Ticket 3 " + t3.text());
        Ticket t4 = tickets.get(3);
        System.out.println("Ticket 4 " + t4.text());
        Ticket t5 = tickets.get(4);
        System.out.println("Ticket 5 " + t5.text());
        List<Ticket> aux = tickets.toList();

        List<Ticket> ticketsChosen = new ArrayList<>();

        int numberOfElements = rand.nextInt(aux.size() - 1) + 1;
        for (int i = 0; i < numberOfElements; i++) {
            int randomIndex = rand.nextInt(aux.size());
            Ticket randomTicket = aux.get(randomIndex);
            ticketsChosen.add(randomTicket);
            aux.remove(randomIndex);
        }

        choosenTickets = SortedBag.of(ticketsChosen);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return choosenTickets;
    }

    @Override
    public TurnKind nextTurn() {
        turnCount++;
        System.out.println("C'est le tour n°" + turnCount + "! :3");
        System.out.println(playerNames.get(ownId) + " a toi de jouer!");
        System.out.println("Nb wagons : " + state.carCount() + " (" + playerNames.get(ownId.next()) + " en a quand à lui " + gameState.playerState(ownId.next()).carCount() + "). ");
        System.out.println("Cartes: " + state.cards().toString());
        System.out.print("Tickets : ");
        for (Ticket t : state.tickets()) {
            System.out.print(t.text() + " ,");
        }
        System.out.println();
        if (state.routes().isEmpty()) {
            System.out.println("Tu n'as pas encore de routes !");
        } else {
            System.out.println("Tes routes sont :");
            for (Route r : state.routes()) {
                System.out.print(r.station1() + "-" + r.station2() + " ,");
            }
            System.out.println();
        }
        if (turnCount > TURN_LIMIT)
            throw new Error("Trop de tours joués !");

        List<String> aux = List.of("c", "t", "r","r");
        String s = aux.get(rand.nextInt(aux.size()));

        switch (s) {
            case "c":
                if (!gameState.canDrawCards()) return TurnKind.CLAIM_ROUTE;
                return TurnKind.DRAW_CARDS;
            case "t":
                if (gameState.ticketsCount() == 0) return TurnKind.DRAW_CARDS;
                return TurnKind.DRAW_TICKETS;
            case "r":
                List<Route> claimableRoutes = claimableRoutes();
                if (claimableRoutes.isEmpty()) return TurnKind.DRAW_CARDS;
                return TurnKind.CLAIM_ROUTE;
        }
        return TurnKind.DRAW_CARDS;
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        List<Ticket> aux = options.toList();

        List<Ticket> ticketsChosen = new ArrayList<>();

        int numberOfElements = rand.nextInt(aux.size() - 1) + 1;
        for (int i = 0; i < numberOfElements; i++) {
            int randomIndex = rand.nextInt(aux.size());
            Ticket randomTicket = aux.get(randomIndex);
            ticketsChosen.add(randomTicket);
            aux.remove(randomIndex);
        }
        return SortedBag.of(ticketsChosen);
    }

    @Override
    public int drawSlot() {
        System.out.println("Les face up cards sont :");
        for (Card c : gameState.cardState().faceUpCards()) {
            System.out.println(c);
        }
        System.out.println();
        List<Integer> aux = List.of(-1, 0, 1, 2, 3, 4);
        return aux.get(rand.nextInt(aux.size()));
    }

    @Override
    public Route claimedRoute() {
        List<Route> claimableRoutes = claimableRoutes();
        int routeIndex = rand.nextInt(claimableRoutes.size());
        Route route = claimableRoutes.get(routeIndex);
        List<SortedBag<Card>> cards = state.possibleClaimCards(route);
        actualRoute = route;
        return route;
    }

    private List<Route> claimableRoutes() {
        List<Route> aux = new ArrayList<>();
        for (Route rt : allRoutes) {
            if (rt.length() <= state.cardCount() && rt.length() <= state.carCount()) {
                List<SortedBag<Card>> possibleCards = state.possibleClaimCards(rt);
                if (possibleCards.size() != 0) aux.add(rt);
            }
        }
        return aux;
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        System.out.println("Quelles cartes voulez vous utiliser?");
        List<SortedBag<Card>> possibleCards = List.copyOf(state.possibleClaimCards(actualRoute));

        /*System.out.println("choix possibles: ");
        for (int i = 0; i < state.possibleClaimCards(actualRoute).size(); i++) {
            System.out.println("Choix " + i + ": ");
            System.out.println(state.possibleClaimCards(actualRoute).get(i).toString());
            possibleCards.add(state.possibleClaimCards(actualRoute).get(i));
        }*/
        return possibleCards.get(rand.nextInt(possibleCards.size()));
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        if (options.isEmpty()) {
            System.out.println("vous ne pouvez malheureusement pas prendre la route :( SADD BROooo:3");
            return SortedBag.of();
        }
        //System.out.println("Pour rappel vos cartes sont:" + state.cards().toString());
        //System.out.println("Quelles cartes voulez vous utiliser?");
        //List<SortedBag<Card>> possibleCards = List.copyOf(options);
        /*System.out.println("choix possibles: ");
        for (int i = 0; i < options.size(); i++) {
            System.out.println("Choix " + i + ": ");
            System.out.println(options.get(i).toString());
            possibleCards.add(options.get(i));
        }*/
        return options.get(rand.nextInt(options.size()));

    }
}
