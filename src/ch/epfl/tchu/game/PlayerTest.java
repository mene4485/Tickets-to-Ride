package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public class PlayerTest implements Player {
    private PlayerId ownId;
    private Map<PlayerId, String> playerNames;
    private static Scanner scanner = new Scanner(System.in);
    private SortedBag<Ticket> choosenTickets;
    private PlayerState state;
    private Route actualRoute;
    private PublicGameState gameState;
    int turnCount=0;

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        this.ownId = ownId;
        this.playerNames = playerNames;
    }

    @Override
    public void receiveInfo(String info) {
        System.out.println(playerNames.get(ownId) + ": " + info);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        gameState=newState;
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

        int d = scanner.nextInt();
        switch (d) {
            case 123:
                choosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t2).add(t3).build();
                break;
            case 124:
                choosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t2).add(t4).build();
                break;
            case 125:
                choosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t2).add(t5).build();
                break;
            case 134:
                choosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t3).add(t4).build();
                break;
            case 135:
                choosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t3).add(t5).build();
                break;
            case 145:
                choosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t4).add(t5).build();
                break;
            case 234:
                choosenTickets = new SortedBag.Builder<Ticket>().add(t2).add(t3).add(t4).build();
                break;
            case 235:
                choosenTickets = new SortedBag.Builder<Ticket>().add(t2).add(t3).add(t5).build();
                break;
            case 245:
                choosenTickets = new SortedBag.Builder<Ticket>().add(t2).add(t4).add(t5).build();
                break;
            case 345:
                choosenTickets = new SortedBag.Builder<Ticket>().add(t3).add(t4).add(t5).build();
                break;
            case 1234:
                choosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t2).add(t3).add(t4).build();
                break;
            case 1235:
                choosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t2).add(t3).add(t5).build();
                break;
            case 1245:
                choosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t2).add(t4).add(t5).build();
                break;
            case 1345:
                choosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t3).add(t4).add(t5).build();
                break;
            case 2345:
                choosenTickets = new SortedBag.Builder<Ticket>().add(t2).add(t3).add(t4).add(t5).build();
                break;
            case 12345:
                choosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t2).add(t3).add(t4).add(t5).build();
                break;
        }

    }
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return choosenTickets;
    }

    @Override
    public TurnKind nextTurn() {
        turnCount++;
        System.out.println("C'est ton tour n°"+turnCount+"! :3");
        System.out.println(playerNames.get(ownId) + " a toi de jouer! \nQue veux tu faire pour ce tour ? \nc pour piocher , r pour prendre une route(ou au moins essayer ;) ) ou t pour ticket");
        System.out.println("Tu as "+state.carCount()+" wagons ! "+playerNames.get(ownId.next())+" en a quand à lui "+gameState.playerState(ownId.next()).carCount()+". ");
        System.out.println("Pour rappel tu tu possède ces cartes: " + state.cards().toString());
        System.out.println("Et tes tickets sont :");
        for (Ticket t :state.tickets()){
            System.out.print(t.text()+" ,");
        }
        System.out.println();
        if(state.routes().isEmpty()){
            System.out.println("Tu n'as pas encore de routes !");
        }else {
            System.out.println("Tes routes sont :");
            for (Route r : state.routes()) {
                System.out.print(r.station1() + "-" + r.station2() + " ,");
            }
            System.out.println();
        }
        String s;
        do{
             s = scanner.next();
        }while (!s.equals("c")&&!s.equals("t")&&!s.equals("r"));

        switch (s) {
            case "c":
                return TurnKind.DRAW_CARDS;
            case "t":
                return TurnKind.DRAW_TICKETS;
            case "r":
                return TurnKind.CLAIM_ROUTE;
        }
        return TurnKind.DRAW_CARDS;
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        System.out.println("Quelles tickets? (Au moins 1)");
        Ticket t1 = options.get(0);
        System.out.println("Ticket 1" + t1.text());
        Ticket t2 = options.get(1);
        System.out.println("Ticket 2" + t2.text());
        Ticket t3 = options.get(2);
        System.out.println("Ticket 3" + t3.text());

        int d = scanner.nextInt();
        switch (d) {
            case 1:
                return new SortedBag.Builder<Ticket>().add(t1).build();
            case 2:
                return new SortedBag.Builder<Ticket>().add(t2).build();
            case 3:
                return new SortedBag.Builder<Ticket>().add(t3).build();
            case 12:
                return new SortedBag.Builder<Ticket>().add(t1).add(t2).build();
            case 13:
                return new SortedBag.Builder<Ticket>().add(t1).add(t3).build();
            case 23:
                return new SortedBag.Builder<Ticket>().add(t2).add(t3).build();
            case 123:
                return new SortedBag.Builder<Ticket>().add(t1).add(t2).add(t3).build();
        }
        return null;
    }

    @Override
    public int drawSlot() {
        System.out.println("Les face up cards sont :");
        for (Card c: gameState.cardState().faceUpCards()) {
            System.out.println(c);
        }
        System.out.println();
        return scanner.nextInt();
    }

    @Override
    public Route claimedRoute() {
        System.out.println("Entrez ville depart: ");
        String s1 = scanner.next();
        System.out.println("Entrez ville d'arrivée: ");
        String s2 = scanner.next();
        List<Route> routes=new ArrayList<>();
        for (Route r : ChMap.routes()) {
            String r1 = r.station1().toString();
            String r2 = r.station2().toString();
            if ((r1.equals(s1)) && (r.station2().toString().equals(s2))&&state.canClaimRoute(r)) {
                routes.add(r);
            }
            if ((r.station2().toString().equals(s1)) && (r.station1().toString().equals(s2))&&state.canClaimRoute(r)) {
                routes.add(r);
            }

        }
        if (routes.size()==1) {
            actualRoute=routes.get(0);
            return routes.get(0);
        }

        System.out.println("quelle couleur? Entrez: ");
        System.out.println("0 pour "+routes.get(0).color());
        System.out.println("ou 1 pour"+routes.get(1).color());
        int s=scanner.nextInt();
        actualRoute=routes.get(s);
        return routes.get(s);
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        System.out.println("Quelles cartes voulez vous utiliser?");
        List<SortedBag<Card>> possibleCards = new ArrayList<>();
        System.out.println("choix possibles: ");
        for (int i = 0; i < state.possibleClaimCards(actualRoute).size(); i++) {
            System.out.println("Choix " + i + ": ");
            System.out.println(state.possibleClaimCards(actualRoute).get(i).toString());
            possibleCards.add(state.possibleClaimCards(actualRoute).get(i));
        }

        return possibleCards.get(scanner.nextInt());
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        if(options.isEmpty()){
            System.out.println("vous ne pouvez malheureusement pas prendre la route :( SADD BROooo:3");
            return SortedBag.of();
        }
        System.out.println("Pour rappel vos cartes sont:"+ state.cards().toString());
        System.out.println("Quelles cartes voulez vous utiliser?");
        List<SortedBag<Card>> possibleCards = new ArrayList<>();
        System.out.println("choix possibles: ");
        for (int i = 0; i < options.size(); i++) {
            System.out.println("Choix " + i + ": ");
            System.out.println(options.get(i).toString());
            possibleCards.add(options.get(i));
        }
        System.out.println("entrez -1 si vous ne souhaitez pas prendre la route");
        int i=scanner.nextInt();
        if(i==-1) return SortedBag.of();
        return possibleCards.get(i);

    }
}
