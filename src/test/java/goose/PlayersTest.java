package goose;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

public class PlayersTest {

	GooseGame goseGame;

	@Before
    public void init() {
		goseGame = new GooseGame();
    }	

	@Test
	public void testAddPlayer() {
		ArrayList<Player> players = goseGame.getPlayers();
		assertTrue(players.isEmpty());

		System.out.println();
		assertEquals(goseGame.eval("add player Pippo"), "players: Pippo"); 
		assertEquals(goseGame.eval("add player Pluto"), "players: Pippo, Pluto");
		assertEquals(goseGame.eval("add player Pape-rino"), "players: Pippo, Pluto, Pape-rino");
		// Typo error
		assertNotEquals(goseGame.eval("add playerPippo"), "players: Pippo"); 
	}

	@Test
	public void duplicatePlayer(){
		assertEquals(goseGame.eval("add player Pippo"), "players: Pippo"); 
		assertEquals(goseGame.eval("add player Pippo"), "Pippo: already existing player"); 
		assertEquals(goseGame.eval("add player Pippo Pippo"), "players: Pippo, Pippo Pippo"); 
	}

	//As a player, I want to move the marker on the board to make the game progress
	@Test
	public void movePlayer(){
		// If there are two participants "Pippo" and "Pluto" on space "Start"
		assertEquals(goseGame.eval("add player Pippo"), "players: Pippo"); 
		assertEquals(goseGame.eval("add player Pluto"), "players: Pippo, Pluto"); 

		assertEquals(goseGame.eval("move Pippo 4, 3"),"Pippo rolls 4, 3. Pippo moves from Start to 7");
		assertEquals(goseGame.eval("move Pluto 2, 2"),"Pluto rolls 2, 2. Pluto moves from Start to 4");
		assertEquals(goseGame.eval("move Pippo 2, 3"),"Pippo rolls 2, 3. Pippo moves from 7 to 12");
		assertEquals(goseGame.getPlayers().get(0).getPosition(), 12);

		assertEquals(goseGame.eval("move Pippo 10, 7"),
					"Pippo rolls 10, 7. Invalid dice pair, Pippo don't try to cheat");
	}

	//As a player, I win the game if I land on space "63"
	@Test
	public void playerWin(){
		assertEquals(goseGame.eval("add player Pippo"), "players: Pippo"); 

		goseGame.getPlayers().get(0).setPosition(60);
		assertEquals(goseGame.eval("move Pippo 1, 2"),"Pippo rolls 1, 2. Pippo moves from 60 to 63. Pippo Wins!!");

		goseGame.getPlayers().get(0).setPosition(60);
		assertEquals(goseGame.eval("move Pippo 3, 2"),
						 "Pippo rolls 3, 2. Pippo moves from 60 to 63. Pippo bounces! Pippo returns to 61");

		goseGame.getPlayers().get(0).setPosition(58);
		assertEquals(goseGame.eval("move Pippo 3, 3"),
 		   			 "Pippo rolls 3, 3. Pippo moves from 58 to 63. Pippo bounces! Pippo returns to 62");


	}

	// Search first match of numbers pair: 1, 4
	public String getDice(String s) {
        Pattern p = Pattern.compile("(\\d, \\d)");
		Matcher m = p.matcher(s);
		String r = "";
        while(m.find()) {
			//System.out.println(m.group());
			r = m.group();
		}
		return r;
	}

	// As a player, I want the game throws the dice for me to save effort
	@Test
	public void throwDice(){
		assertEquals(goseGame.eval("add player Pippo"), "players: Pippo"); 

		goseGame.getPlayers().get(0).setPosition(4);
		String output = goseGame.eval("move Pippo 5, 5"); 
		// Read dice values	
		String[] dice = getDice(output).split(",");
		int d1 = Integer.parseInt(dice[0].trim());
		int d2 = Integer.parseInt(dice[1].trim());
		int newPosition = 4 + d1+d2;
		assertTrue(output.contains("Pippo rolls " +d1+ ", " +d2+ ". Pippo moves from 4 to 14")); // goose picture

		goseGame.getPlayers().get(0).setPosition(4);
		output = goseGame.eval("move Pippo"); 
		// Read dice values	
		dice = getDice(output).split(",");
		d1 = Integer.parseInt(dice[0].trim());
		d2 = Integer.parseInt(dice[1].trim());
		newPosition = 4 + d1+d2;
		assertTrue(output.contains("Pippo rolls " +d1+ ", " +d2+ ". Pippo moves from 4 to " + newPosition));

		// Test another player and position
		assertEquals(goseGame.eval("add player Pluto"), "players: Pippo, Pluto"); 
		goseGame.getPlayers().get(1).setPosition(10);
		output = goseGame.eval("move Pluto"); 
		dice = getDice(output).split(",");
		d1 = Integer.parseInt(dice[0].trim());
		d2 = Integer.parseInt(dice[1].trim());
		newPosition = 10 + d1+d2;
		assertTrue(output.contains("Pluto rolls " +d1+ ", " +d2+ ". Pluto moves from 10 to " + newPosition));
	}

	// As a player, when I get to the space 6, "The Bridge", I jump to the space "12"
	// As a player, when I get to a space with a picture of "The Goose", I move forward again by the sum of the two dice rolled before
	// The spaces 5, 9, 14, 18, 23, 27 have a picture of "The Goose"
	@Test
	public void landOnPicture(){
		// Bridge
		assertEquals(goseGame.eval("add player Pippo"), "players: Pippo"); 
		goseGame.getPlayers().get(0).setPosition(4);
		assertEquals(goseGame.eval("move Pippo 1, 1"),"Pippo rolls 1, 1. Pippo moves from 4 to The Bridge. Pippo jumps to 12");
		assertEquals(goseGame.getPlayers().get(0).getPosition(), 12);
		
		// Single Jump
		assertEquals(goseGame.eval("add player Pluto"), "players: Pippo, Pluto"); 
		goseGame.getPlayers().get(1).setPosition(3);
		assertEquals(goseGame.eval("move Pluto 1, 1"), 
					 "Pluto rolls 1, 1. Pluto moves from 3 to 5, The Goose. Pluto moves again and goes to 7");
		assertEquals(goseGame.getPlayers().get(1).getPosition(), 7);

		goseGame.getPlayers().get(0).setPosition(6);
		assertEquals(goseGame.eval("move Pippo 2, 1"), 
					 "Pippo rolls 2, 1. Pippo moves from 6 to 9, The Goose. Pippo moves again and goes to 12");
		assertEquals(goseGame.getPlayers().get(0).getPosition(), 12);

		// Multiple Jump, from 14 to 18
		goseGame.getPlayers().get(0).setPosition(10);
		assertEquals(goseGame.eval("move Pippo 2, 2"), 
					"Pippo rolls 2, 2. Pippo moves from 10 to 14, The Goose. Pippo moves again and goes to 18, The Goose. "+
					"Pippo moves again and goes to 22");
		assertEquals(goseGame.getPlayers().get(0).getPosition(), 22);
		// from 9 to 14
		goseGame.getPlayers().get(1).setPosition(4);
		assertEquals(goseGame.eval("move Pluto 3, 2"), 
					"Pluto rolls 3, 2. Pluto moves from 4 to 9, The Goose. Pluto moves again and goes to 14, The Goose. "+
					"Pluto moves again and goes to 19");
		assertEquals(goseGame.getPlayers().get(1).getPosition(), 19);
	}

	// As a player, when I land on a space occupied by another player, I send him to my previous position so that the game can be more entertaining.
	@Test
	public void prankPlayer() {
		/*
		If there are two participants "Pippo" and "Pluto" respectively on spaces "15" and "17"
		assuming that the dice get 1 and 1
		when the user writes: "move Pippo"
		the system responds: "Pippo rolls 1, 1. Pippo moves from 15 to 17. On 17 there is Pluto, who returns to 15"
		*/
		
	}

}
