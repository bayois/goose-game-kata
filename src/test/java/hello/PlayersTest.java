package hello;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class PlayersTest {
	
	@Test
	public void addPlayer() {
		GooseGame goseGame = new GooseGame();

		ArrayList<Player> players = goseGame.getPlayers();
		assertTrue(players.isEmpty());

		System.out.println();
		assertEquals(goseGame.eval("add player Pippo"), "players: Pippo"); 
		assertEquals(goseGame.eval("add player Pluto"), "players: Pippo, Pluto");
		assertEquals(goseGame.eval("add player Pape-rino"), "players: Pippo, Pluto, Pape-rino");

		assertNotEquals(goseGame.eval("add playerPippo"), "players: Pippo"); 

		assertEquals(goseGame.eval("add player Pippo"), "Pippo: already existing player"); 
	}

	@Test
	public void duplicatePlayer(){

	}

}
