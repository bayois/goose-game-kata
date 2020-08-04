package hello;

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
		while (! outputText.equals("winner") && ! userText.equals("exit")) {
			// Write output and Read user text
			userText = goseCli.readCommand(outputText + System.lineSeparator());
			// Eval user text
			outputText = eval(userText);
		}
	}

	// Evaluate User Action
	String eval(String text) {
		this.reply = null;

		// Add a player user request 
		if (text.startsWith("add player ")) {
			// Get user name
			String name = text.replace("add player ", "");
			// Add new player and reply with players list
			this.reply = addNewPlayer(name);
		// Add a player user request 
		} else 
		if (text.startsWith("move ")) {
			// Get user name
			String move = text.replace("move ", "");
			// Add new player and reply with players list
			this.reply = movePlayer(move);
		} else {
		// Print help
			this.reply = System.lineSeparator()+"Command help" + System.lineSeparator() + 
					"add player [Name]: add a player to the game" + System.lineSeparator() +
					"help:              print command help" + System.lineSeparator() +
					"exit:              exit the game" + System.lineSeparator() ;
		}
		return this.reply;
	}

	// Add new player to the game
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

	// Return two random number from 1 to 6
	String[] throwDice(){
		String[] dice = { 
			""+(new Random().nextInt(5) +1), 
			""+(new Random().nextInt(5) +1)
		};
		return dice;
	}

	// Check the landing position and apply Goose Rules
	public void checkUserPosition(String name, int i) {
		// Wins
		if (newPosition == 63){
			this.reply += name + " moves from " + getPlayers().get(i).getPosition() + " to 63. " +
						name + " Wins!!";
		} // Bounce 
		else if (newPosition > 63) {
			this.newPosition = 63 - (this.newPosition - 63);
			this.reply += name + " moves from " + getPlayers().get(i).getPosition() + " to 63. " +
						name + " bounces! " + name + " returns to " + this.newPosition;
		} // Move
		else {
			this.reply += name + " moves from " + getPlayers().get(i).getPosition() + " to " + this.newPosition;
		}
	}

	// Add new player to the game
	String movePlayer(String moveName) {
		// Loop on players list 
		for (int i = 0; i < getPlayers().size(); i++) {
			String name = getPlayers().get(i).getName();
			// Search requested player
			if (moveName.startsWith(name)){
				// Get dice numbers
				String[] dice = moveName.replace(name, "").trim().split(",");
				// Check dice user value 
				if ( dice.length < 2){
					dice = throwDice();
				}
				// Get new position
				this.newPosition = getPlayers().get(i).getPosition() + 
								  Integer.parseInt(dice[0].trim()) + 
								  Integer.parseInt(dice[1].trim());
				// Move Message, user rolls
				this.reply = name + " rolls " + dice[0].trim() + ", " + dice[1].trim() + ". ";

				// Check this.newPosition and Goose rules and reply
				checkUserPosition(name, i);

				// Update players position
				getPlayers().get(i).setPosition(this.newPosition);
				this.reply = this.reply.replace("from 0", "from Start");
			}

		}

		return this.reply;
	}

}
