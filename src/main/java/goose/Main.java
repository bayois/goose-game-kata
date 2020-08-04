package goose;

import org.joda.time.LocalTime;

public class Main{
	public static void main(String[] args) {
		LocalTime currentTime = new LocalTime();
		System.out.println("Welcome to the Goose Game v0.0.1 the current time is: " + currentTime + " enjoy your match");
		GooseGame gose = new GooseGame();
		gose.start();
		System.out.println(System.lineSeparator() + "Thank you for playing the Goose Game"); 
	}
}
