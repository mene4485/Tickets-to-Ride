package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.Map;
import java.util.Random;

/**
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public class Main {
    public static void main(String[] args) {
        Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, "Albert", PlayerId.PLAYER_2, "Menelik");
        Player albert = new PlayerTestRandom(1, ChMap.routes());
        //Player albert = new PlayerTest();
        albert.initPlayers(PlayerId.PLAYER_1,playerNames);
        Player menelik = new PlayerTestRandom(1, ChMap.routes());
        //Player menelik = new PlayerTest();
        menelik.initPlayers(PlayerId.PLAYER_2,playerNames);
        Map<PlayerId, Player> players = Map.of(PlayerId.PLAYER_1,albert,PlayerId.PLAYER_2,menelik);
        Game.play(players,playerNames, SortedBag.of(ChMap.tickets()),new Random(1));
    }
}
