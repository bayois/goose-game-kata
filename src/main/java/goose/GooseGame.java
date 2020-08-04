package goose;

import java.util.ArrayList;
import java.util.Random;

public class GooseGame {
	ArrayList<Player> players = new ArrayList<Player>();
	GooseCli goseCli = new GooseCli();
	int newPosition;
	String reply;

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void addPlayer(String name) {
		Player p = new Player(name);
		players.add(p);
	}

	void start() {
		String outputText = "Add one or more player to start, move your player or write help for command list";
		String userText = "";

		// Loop on user command until winner was found or exit request
		while (! outputText.contains("Wins") && ! userText.equals("exit")) {
			// Write output and Read user text
			userText = goseCli.readCommand(outputText + System.lineSeparator());
			// Eval user text
			outputText = eval(userText);
		}
		goseCli.write(outputText);
	}

	// Read User Input and evaluate Action
	String eval(String text) {
		this.reply = "";

		// Add a player user request 
		if (text.startsWith("add player ")) {
			// Get user name
			String playerName = text.replace("add player ", "");
			this.reply = addNewPlayer(playerName);
		} 
		// Move player user request 
		else if (text.startsWith("move ")) 
		{
			// Get move details
			String move = text.replace("move ", "");
			this.reply = movePlayer(move);
		} 
		// Move player user request 
		else if (text.startsWith("show ")) 
		{
			this.reply = showPlayers();
		} 
		
		else {
		// Print help
			this.reply = System.lineSeparator()+"Command help" + System.lineSeparator() + 
					"add player <Name>          : add a player to the game" + System.lineSeparator() +
					"move <Name> [dice1,dice2]  : move player to next position giving dice value " + System.lineSeparator() +
					"move <Name>                : move player to next position with automatic dice value " + System.lineSeparator() +
					"show                       : show players positions " + System.lineSeparator() +
					"help                       : print command help" + System.lineSeparator() +
					"exit                       : exit the game" + System.lineSeparator() ;
		}
		return this.reply;
	}

	/*
	* Add new player to the game
	*/
	String addNewPlayer(String name) {
		// Loop on player names
		String command = "players: ";
		for (int i = 0; i < getPlayers().size(); i++) {
			command += getPlayers().get(i).getName() + ", ";
			// Check if already exist
			if ( getPlayers().get(i).getName().equals(name))
				return name + ": already existing player";
		}
		// If not found add new player
		addPlayer(name);
		command += name;

		return command;
	}

	/*
	* Return two random dicenumber from 1 to 6
	*/
	String[] throwDice(){
		String[] dice = { 
			""+(new Random().nextInt(5) +1), 
			""+(new Random().nextInt(5) +1) 
		};
		return dice;
	}

	/*
	 Check the player landing position and apply Goose Rules:
	  As a player, I win the game if I land on space "63"
	  As a player, when I get to the space 6, "The Bridge", I jump to the space "12"
	  As a player, when I get to a space with a picture of "The Goose", I move forward again by the sum of the two dice rolled before
	  The spaces 5, 9, 14, 18, 23, 27 have a picture of "The Goose"
	 */
	public void checkUserPosition(String[] dice, Player p) {
		String name = p.getName();
		int position = p.getPosition();
		// Wins or Bounce
		if (this.newPosition >= 63)
		{
			this.reply += name + " moves from " + position + " to 63. ";
			if (newPosition > 63) {
				this.newPosition = 63 - (this.newPosition - 63);
				this.reply += name + " bounces! " + name + " returns to " + this.newPosition;
			} else {
				this.reply += name + " Wins!!";
			}
		} // The Bridge 
		else if (this.newPosition == 6)
		{
			this.newPosition = 12;
			this.reply += name + " moves from " + position + " to 6. " + 
			 			  name + " jumps to " + this.newPosition;
		} // The Goose
		else if ( this.newPosition == 5 || this.newPosition == 9 || this.newPosition == 14 || 
				  this.newPosition == 18 || this.newPosition ==  23 || this.newPosition == 27 ) 
		{
			this.reply += name + " moves from " + position + " to " + this.newPosition + ", The Goose. ";
			this.newPosition += Integer.parseInt(dice[0].trim()) + Integer.parseInt(dice[1].trim());
			this.reply += name + " moves again and goes to " + this.newPosition;
			// Multiple Jump: TODO possible recursion
			if ( this.newPosition == 5 || this.newPosition == 9 || this.newPosition == 14 || 
				 this.newPosition == 18 || this.newPosition ==  23 || this.newPosition == 27 ) 
			{
				this.newPosition += Integer.parseInt(dice[0].trim()) + Integer.parseInt(dice[1].trim());
				this.reply += ", The Goose. " + name + " moves again and goes to " + this.newPosition;
			}
		} // Move
		else {
			this.reply += name + " moves from " + position + " to " + this.newPosition;
		}
		// Update player position
		p.setPosition(this.newPosition);
	}

	/*
	* Move the required player
	*/
	String movePlayer(String moveCommand) {
		// Loop on players list 
		for (int i = 0; i < getPlayers().size(); i++) {
			String name = getPlayers().get(i).getName();
			// Search requested player
			if (moveCommand.startsWith(name)){
				// Get dice numbers
				String[] dice = moveCommand.replace(name, "").trim().split(",");
				// Check dice user value 
				if ( dice.length < 2){
					dice = throwDice();
				}

				// Get new position
				int d1 = Integer.parseInt(dice[0].trim()); 
				int d2 = Integer.parseInt(dice[1].trim());
				this.newPosition = getPlayers().get(i).getPosition() + d1 + d2;

				// Move Message, user rolls
				this.reply = name + " rolls " + dice[0].trim() + ", " + dice[1].trim() + ". ";
				// Check dice values
				if (d1 <= 6 && d2 <= 6){
					checkUserPosition(dice, getPlayers().get(i));

					// Fix user output text
					this.reply = this.reply.replace("from 0", "from Start");
					this.reply = this.reply.replace("to 6.", "to The Bridge.");
				}else{
					this.reply +=  "Invalid dice pair, "+ name + " don't try to cheat";
				}
			}

		}

		return this.reply;
	}
	/*
	* Show players positions
	*/
	public String showPlayers(){
		for (int i = 0; i < getPlayers().size(); i++) {
			this.reply += "Player name: " + getPlayers().get(i).getName() +
						 " position: " + getPlayers().get(i).getPosition() + 
						 System.lineSeparator(); 
			
		}
		return this.reply;
	}


}
