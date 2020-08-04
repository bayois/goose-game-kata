package goose;

import java.util.ArrayList;
import java.util.Random;

public class GooseGame {
	ArrayList<Player> players = new ArrayList<Player>();

	public ArrayList<Player> getPlayers() {
		return players;
	}

	/*
	* Add a single player to the list
	*/
	public void addPlayer(String name) {
		Player p = new Player(name);
		players.add(p);
	}

	/*
	* Start the Game, play until winner or exit found
	*/
	void start() {
		GooseCli goseCli = new GooseCli();

		String outputText = "Add one or more player to start, move your player or write help for command list";
		String userText = "";

		// Loop on user command until winner was found or exit request
		while (! outputText.contains("Wins") && ! userText.equals("exit")) {
			try {
				// Write output and Read user text
				userText = goseCli.readCommand(outputText + System.lineSeparator());
				// Eval user text
				outputText = eval(userText);
			} catch (Exception e) {
				outputText = getHelp();
			}
		}
		goseCli.write(outputText);
	}

	/*
	* Help command
	*/
	String getHelp(){
		return System.lineSeparator()+"Command help" + System.lineSeparator() + 
				"add player <Name>          : add a player to the game" + System.lineSeparator() +
				"move <Name> [dice1,dice2]  : move player to next position giving dice value " + System.lineSeparator() +
				"move <Name>                : move player to next position with automatic dice value " + System.lineSeparator() +
				"show players               : show players positions " + System.lineSeparator() +
				"help                       : print command help" + System.lineSeparator() +
				"exit                       : exit the game" + System.lineSeparator() ;
	}

	/*
	 * Read User Input from cli and evaluate Game Action. 
	 * 
	 * @param playerText
	 * @return result message
	*/
	String eval(String playerText) {
		String outputMessage = "";

		if (playerText.startsWith("add player ")) { // Add a player user request 
			// Get user name
			String playerName = playerText.replace("add player ", "");
			outputMessage = addNewPlayer(playerName);
		} 
		else if (playerText.startsWith("move ")) // Move player user request 
		{
			// Get move details
			String playerName = playerText.replace("move ", "");
			outputMessage = movePlayer(playerName);
		} 
		else if (playerText.equals("show players")) // Show players positions
		{
			outputMessage = showPlayers();
		} 
		else { // Print help
			outputMessage = getHelp();
		}

		return outputMessage;
	}

	/*
	* Add new player to the game
	*/
	String addNewPlayer(String name) {
		// Loop on player names
		String outpuMessage = "players: ";
		for (Player p : getPlayers() ){
			outpuMessage += p.getName() + ", ";
			// Check if already exist
			if ( p.getName().equals(name))
				return name + ": already existing player";
		}
		// If not found add new player
		addPlayer(name);
		outpuMessage += name;

		return outpuMessage;
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
	* Check the player landing position and update next position according to Goose Rules:
	* As a player, I win the game if I land on space "63"
	* As a player, when I get to the space 6, "The Bridge", I jump to the space "12"
	* As a player, when I get to a space with a picture of "The Goose", I move forward again by the sum of the two dice rolled before
	* The spaces 5, 9, 14, 18, 23, 27 have a picture of "The Goose"
    * If there are two participants "Pippo" and "Pluto" respectively on spaces "15" and "17"
	*
	* @return ouputMessage
	*/
	public String updatePlayerPosition(String[] dice, Player p) {
		String outputMessage = "";
		String name = p.getName();
		int position = p.getPosition();

		int d1 = Integer.parseInt(dice[0].trim()); 
		int d2 = Integer.parseInt(dice[1].trim());
		int nextPosition = position + d1 + d2;

		// Check dice values
		outputMessage = name + " rolls " + dice[0].trim() + ", " + dice[1].trim() + ". ";
		if (d1 > 6 || d2 > 6 || d1 < 1 || d2 < 1 ){
			outputMessage +=  "Invalid dice pair, "+ name + " don't try to cheat";
			return outputMessage;
		}
		// Default Move message
		String defaultMove = name + " moves from " + position + " to " + nextPosition; 

		// Wins or Bounce
		if (nextPosition >= 63)
		{
			outputMessage += name + " moves from " + position + " to 63. ";
			if (nextPosition > 63) {
				nextPosition = 63 - (nextPosition - 63);
				outputMessage += name + " bounces! " + name + " returns to " + nextPosition;
			} else {
				outputMessage += name + " Wins!!";
			}
		} // The Bridge 
		else if (nextPosition == 6)
		{
			outputMessage += defaultMove; 
			nextPosition = 12;
			outputMessage += ". " + name + " jumps to " + nextPosition;
		} // The Goose, Single Jump
		else if ( isGoosePicture(nextPosition) )
		{
			outputMessage += defaultMove; 
			nextPosition += d1 + d2;
			outputMessage += ", The Goose. " + name + " moves again and goes to " + nextPosition;
			// Multiple Jump: TODO possible recursion
			if ( isGoosePicture(nextPosition) )
			{
				nextPosition += d1 + d2;
				outputMessage += ", The Goose. " + name + " moves again and goes to " + nextPosition;
			}
		} // Move
		else {
			outputMessage += defaultMove; 
			// Another player on same position goes back
			for (Player p2: getPlayers() ){
				if ( ! p2.equals(p) && p2.getPosition() == nextPosition ){
					p2.setPosition(position);
					outputMessage += ". On " + nextPosition + " there is " + p2.getName() + 
									 ", who returns to " + position; 
				}
			}
		}
		// Update player position
		p.setPosition(nextPosition);

		return outputMessage;
	}

	/*
	* Check if positin is Goose
	*/
	boolean isGoosePicture(int nextPosition){
		if (nextPosition == 5 || nextPosition == 9 || nextPosition == 14 || 
			nextPosition == 18 || nextPosition ==  23 || nextPosition == 27 ) 
		{
			return true;
		}else{
			return false;
		}
	}

	/*
	* Move command, update the required player position
	*
	* @return outputMessage
	*/
	String movePlayer(String moveCommand) {
		String outputMessage = "";
		// Loop on players list 
		for (Player p : getPlayers() ) 
		{
			String name = p.getName();
			// Search requested player
			if (moveCommand.contains(name))
			{
				// Get dice numbers
				String[] dice = moveCommand.replace(name, "").trim().split(",");
				// Check dice user value 
				if ( dice.length < 2){
					dice = throwDice();
				}
				// Update player position and verify Goose rules
				outputMessage += updatePlayerPosition(dice, p)
								.replace("from 0", "from Start")
								.replace("to 6.", "to The Bridge.");
			}

		}

		return outputMessage;
	}
	/*
	* Show players positions
	*/
	public String showPlayers(){
		String outputMessage = "";
		for (Player p : getPlayers() ){ 
			outputMessage += "Player name: " + p.getName() + " position: " + p.getPosition() + 
						 System.lineSeparator(); 
			
		}
		return outputMessage;
	}
}
