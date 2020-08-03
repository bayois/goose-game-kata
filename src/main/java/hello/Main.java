package hello;

import org.joda.time.LocalTime;

public class Main{
	public static void main(String[] args) {
		LocalTime currentTime = new LocalTime();
		System.out.println("The current time is: " + currentTime);
		GooseGame gose = new GooseGame();
		gose.start();
	}
}
