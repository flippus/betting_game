package functionaltests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.util.HashSet;
import java.util.Set;

import models.League;
import models.User;
import models.UserPoints;

import org.junit.Test;

public class UserPointsFunctionalTest {

	private League league;
	private User user1;
	private User user2;

	public void createData() {
		league = new League();
		league.save();
		Set<League> leagueSet = new HashSet<>();
		leagueSet.add(league);

		user1 = new User();
		user1.email = "user1@test.it";
		user1.firstname = "flp";
		user1.lastname = "unibz";
		user1.password = "password";
		user1.leagues = leagueSet;
		user1.isAdmin = true;
		User.create(user1);

		user2 = new User();
		user2.email = "user2@test.it";
		user2.firstname = "flp";
		user2.lastname = "unibz";
		user2.password = "password";
		user2.leagues = leagueSet;
		user2.isAdmin = true;
		User.create(user2);
	}

	@Test
	public void testCreateUserPoints() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				createData();
				assertNull(League.findUserPointsByUser(user1.email, league.id));
				assertNull(League.findUserPointsByUser(user2.email, league.id));
				UserPoints.createOrUpdateUserPointsByUser(user1.email, 10, 1,
						1, league.id);
				UserPoints.createOrUpdateUserPointsByUser(user2.email, 5, 1, 1,
						league.id);
				assertEquals(
						10,
						League.findUserPointsByUser(user1.email, league.id).points);
				assertEquals(
						1,
						League.findUserPointsByUser(user1.email, league.id).rightResultCount);
				assertEquals(
						1,
						League.findUserPointsByUser(user1.email, league.id).rightWinnerCount);
				assertEquals(
						5,
						League.findUserPointsByUser(user2.email, league.id).points);
				assertEquals(
						1,
						League.findUserPointsByUser(user2.email, league.id).rightResultCount);
				assertEquals(
						1,
						League.findUserPointsByUser(user2.email, league.id).rightWinnerCount);
			}
		});
	}

	@Test
	public void testUpdateUserPoints() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				createData();
				assertNull(League.findUserPointsByUser(user1.email, league.id));
				UserPoints.createOrUpdateUserPointsByUser(user1.email, 10, 1,
						1, league.id);
				assertEquals(
						10,
						League.findUserPointsByUser(user1.email, league.id).points);
				assertEquals(
						1,
						League.findUserPointsByUser(user1.email, league.id).rightResultCount);
				assertEquals(
						1,
						League.findUserPointsByUser(user1.email, league.id).rightWinnerCount);
				UserPoints.createOrUpdateUserPointsByUser(user1.email, 5, 1, 2,
						league.id);
				assertEquals(
						5,
						League.findUserPointsByUser(user1.email, league.id).points);
				assertEquals(
						1,
						League.findUserPointsByUser(user1.email, league.id).rightResultCount);
				assertEquals(
						2,
						League.findUserPointsByUser(user1.email, league.id).rightWinnerCount);
			}
		});
	}
}
