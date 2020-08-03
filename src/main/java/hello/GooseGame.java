package hello;

import java.util.ArrayList;

public class GooseGame {
	ArrayList<Player> players = new ArrayList<Player>();
	GoseCli goseCli = new GoseCli();

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void addPlayer(String name) {
		Player p = new Player(name);
		players.add(p);
	}

	void start() {
		boolean winner = false;
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
		String reply = null;

		// Add a player user request 
		if (text.startsWith("add player ")) {
			// Get user name
			String name = text.replace("add player ", "");
			// Add new player and reply with players list
			reply = addNewPlayer(name);
		} else {
		// Print help
			reply = System.lineSeparator()+"Command help" + System.lineSeparator() + 
					"add player [Name]: add a player to the game" + System.lineSeparator() +
					"help:              print command help"+
					"exit:              exit the game";
		}

		return reply;
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

}
