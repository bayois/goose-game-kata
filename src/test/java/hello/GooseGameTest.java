package hello;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import org.junit.Test;

public class GooseGameTest{
	
	private GooseGame gose = new GooseGame();

	@Test
	public void greeterSaysHello() {
		assertThat(gose.sayHello(), containsString("Hello"));
	}

}
