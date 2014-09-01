package functionaltests;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import models.Game;

import org.junit.Test;

public class GameFunctionalTest {

	Game game = null;

	public void createData() {

		game = new Game();
		game.played = false;
		game.goalsAwayTeam = 1;
		game.goalsHomeTeam = 3;
	}

	@Test
	public void testGetResult() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				createData();

				assertEquals(game.getResult(), "-:-");

				game.played = true;
				game.save();
				assertEquals(game.getResult(), "3:1");
			}
		});
	}

	@Test
	public void testGetWinner() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				createData();

				assertEquals(game.getWinner(), 0);

				game.played = true;
				game.save();
				assertEquals(game.getWinner(), 1);

				game.goalsHomeTeam = 0;
				game.save();
				assertEquals(game.getWinner(), 2);

				game.goalsAwayTeam = 0;
				game.save();
				assertEquals(game.getWinner(), 3);
			}
		});
	}
}
