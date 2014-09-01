package functionaltests;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import models.Bet;
import models.Game;
import models.User;

import org.junit.Test;

public class BetFunctionalTest {

	User user = null;
	Game game = null;
	Bet bet = null;

	public void createData() {
		game = new Game();
		game.played = false;
		game.goalsAwayTeam = 1;
		game.goalsHomeTeam = 3;
		game.save();
		bet = new Bet();
		bet.game = game;
		bet.user = user;
		bet.save();
	}

	@Test
	public void testBetWinner() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				createData();
				bet.goalsHomeTeam = 3;
				bet.goalsAwayTeam = 1;
				bet.save();

				assertEquals(bet.getBetWinner(), 1);

				bet.goalsHomeTeam = 0;
				bet.save();

				assertEquals(bet.getBetWinner(), 2);

				bet.goalsAwayTeam = 0;
				bet.save();

				assertEquals(bet.getBetWinner(), 3);
			}
		});
	}

	@Test
	public void testBetPoints() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				createData();

				assertEquals(bet.getBetPoints(), 0);

				game.played = true;
				game.save();

				bet.goalsHomeTeam = 3;
				bet.goalsAwayTeam = 1;
				bet.save();

				assertEquals(bet.getBetPoints(), 1);

				bet.goalsAwayTeam = 0;
				bet.save();

				assertEquals(bet.getBetPoints(), 2);
			}
		});
	}

}
