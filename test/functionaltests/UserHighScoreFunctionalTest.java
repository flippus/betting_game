package functionaltests;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.util.List;

import models.League;
import models.User;
import models.UserPoints;

import org.junit.Test;

public class UserHighScoreFunctionalTest {

	private User user1;
	private User user2;
	private User user3;
	private User user4;
	private League league;

	public void createUsers() {
		league = new League();
		league.save();

		user1 = new User();
		user1.email = "user1@test.it";
		user1.firstname = "flp";
		user1.lastname = "unibz";
		user1.password = "password";
		user1.isAdmin = true;
		User.create(user1);

		user2 = new User();
		user2.email = "user2@test.it";
		user2.firstname = "flp";
		user2.lastname = "unibz";
		user2.password = "password";
		user2.isAdmin = true;
		User.create(user2);

		user3 = new User();
		user3.email = "user3@test.it";
		user3.firstname = "flp";
		user3.lastname = "unibz";
		user3.password = "password";
		user3.isAdmin = true;
		User.create(user3);

		user4 = new User();
		user4.email = "user4@test.it";
		user4.firstname = "flp";
		user4.lastname = "unibz";
		user4.password = "password";
		user4.isAdmin = true;
		User.create(user4);
	}

	@Test
	public void testSamePositions() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				createUsers();
				UserPoints.createOrUpdateUserPointsByUser(user1.email, 0, 0, 0,
						league.id);
				UserPoints.createOrUpdateUserPointsByUser(user2.email, 0, 0, 0,
						league.id);
				UserPoints.createOrUpdateUserPointsByUser(user3.email, 0, 0, 0,
						league.id);
				UserPoints.createOrUpdateUserPointsByUser(user4.email, 0, 0, 0,
						league.id);
				assertEquals(league.leagueCalculator.getOrderedUserPointsCount(),
						3);
				league.leagueCalculator.calculateUserRanking();
				List<UserPoints> rankedUserList = league.leagueCalculator
						.getOrderedUserPoints();
				assertEquals(rankedUserList.get(0).user.email, user1.email);
				assertEquals(rankedUserList.get(0).position, 1);
				assertEquals(rankedUserList.get(1).user.email, user2.email);
				assertEquals(rankedUserList.get(1).position, 1);
				assertEquals(rankedUserList.get(2).user.email, user3.email);
				assertEquals(rankedUserList.get(2).position, 1);
				assertEquals(rankedUserList.get(3).user.email, user4.email);
				assertEquals(rankedUserList.get(3).position, 1);
			}
		});
	}

	@Test
	public void testSameAndDifferentPositions() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				createUsers();
				UserPoints.createOrUpdateUserPointsByUser(user1.email, 6, 1, 0,
						league.id);
				UserPoints.createOrUpdateUserPointsByUser(user2.email, 6, 1, 0,
						league.id);
				UserPoints.createOrUpdateUserPointsByUser(user3.email, 6, 0, 3,
						league.id);
				UserPoints.createOrUpdateUserPointsByUser(user4.email, 6, 0, 3,
						league.id);
				assertEquals(league.leagueCalculator.getOrderedUserPointsCount(),
						3);
				league.leagueCalculator.calculateUserRanking();
				List<UserPoints> rankedUserList = league.leagueCalculator
						.getOrderedUserPoints();
				assertEquals(rankedUserList.get(0).user.email, user1.email);
				assertEquals(rankedUserList.get(0).position, 1);
				assertEquals(rankedUserList.get(1).user.email, user2.email);
				assertEquals(rankedUserList.get(1).position, 1);
				assertEquals(rankedUserList.get(2).user.email, user3.email);
				assertEquals(rankedUserList.get(2).position, 3);
				assertEquals(rankedUserList.get(3).user.email, user4.email);
				assertEquals(rankedUserList.get(3).position, 3);
			}
		});
	}

	@Test
	public void testDifferentPositions() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				createUsers();
				UserPoints.createOrUpdateUserPointsByUser(user1.email, 10, 2,
						0, league.id);
				UserPoints.createOrUpdateUserPointsByUser(user2.email, 7, 1, 2,
						league.id);
				UserPoints.createOrUpdateUserPointsByUser(user3.email, 6, 1, 1,
						league.id);
				UserPoints.createOrUpdateUserPointsByUser(user4.email, 5, 1, 0,
						league.id);
				assertEquals(league.leagueCalculator.getOrderedUserPointsCount(),
						3);
				league.leagueCalculator.calculateUserRanking();
				List<UserPoints> rankedUserList = league.leagueCalculator
						.getOrderedUserPoints();
				assertEquals(rankedUserList.get(0).user.email, user1.email);
				assertEquals(rankedUserList.get(0).position, 1);
				assertEquals(rankedUserList.get(1).user.email, user2.email);
				assertEquals(rankedUserList.get(1).position, 2);
				assertEquals(rankedUserList.get(2).user.email, user3.email);
				assertEquals(rankedUserList.get(2).position, 3);
				assertEquals(rankedUserList.get(3).user.email, user4.email);
				assertEquals(rankedUserList.get(3).position, 4);
			}
		});
	}
}
