package integrationtests;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import models.Game;
import models.League;
import models.Team;
import models.User;

import org.junit.Test;

import play.libs.F.Callback;
import play.test.TestBrowser;

public class LeagueAdministrationIntegrationTest {

	public void loginTestAdmin(TestBrowser browser) {
		browser.goTo("http://localhost:3333");
		assertThat(browser.url()).isEqualTo("http://localhost:3333/login");
		browser.fill("#email").with("admin@test.com");
		browser.fill("#password").with("password");
		browser.submit("#login");
		assertThat(browser.url()).isEqualTo("http://localhost:3333/");
		browser.goTo("http://localhost:3333/admin/leagues/");
		assertThat(browser.url()).isEqualTo(
				"http://localhost:3333/admin/leagues/");
	}

	@Test
	public void accessTest() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						loginTestAdmin(browser);

						browser.goTo("http://localhost:3333/admin/leagues/1");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/admin/leagues/1");
						browser.goTo("http://localhost:3333/admin/leagues/show/1");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/admin/leagues/show/1");
					}
				});
	}

	@Test
	public void createLeagueTest() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						loginTestAdmin(browser);

						browser.goTo("http://localhost:3333/admin/leagues/new");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/admin/leagues/new");
						browser.fill("#name").with("New Test League");
						browser.fill("#matchdayCount").with("87");
						browser.fill("#pointsForRightResult").with("6");
						browser.fill("#pointsForRightWinner").with("2");
						browser.submit("#createLeague");

						League newLeague = League.find.where()
								.eq("name", "New Test League").findUnique();
						assertNotNull(newLeague);
						assertEquals(87, newLeague.matchdayCount);
						assertEquals(6, newLeague.pointsForRightResult);
						assertEquals(2, newLeague.pointsForRightWinner);
					}
				});
	}

	@Test
	public void deleteLeagueTest() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						loginTestAdmin(browser);

						browser.goTo("http://localhost:3333/admin/leagues/1/delete/");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/admin/leagues/");
						assertNull(League.find.where().eq("id", 1l)
								.findUnique());
					}
				});
	}

	@Test
	public void createTeamTest() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						loginTestAdmin(browser);

						browser.goTo("http://localhost:3333/admin/leagues/show/1");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/admin/leagues/show/1");
						browser.goTo("http://localhost:3333/admin/leagues/1/teams/add");
						assertThat(browser.url())
								.isEqualTo(
										"http://localhost:3333/admin/leagues/1/teams/add");
						browser.fill("#name").with("Test Team New");
						browser.submit("#createTeam");
						assertNotNull(findTeamByName("Test Team New"));
					}
				});
	}

	@Test
	public void editTeamTest() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						loginTestAdmin(browser);

						browser.goTo("http://localhost:3333/admin/leagues/teams/1");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/admin/leagues/teams/1");
						browser.fill("#name").with("Test Team Edited");
						browser.submit("#editTeam");
						assertNotNull(findTeamByName("Test Team Edited"));
					}
				});
	}

	@Test
	public void deleteTeamTest() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						loginTestAdmin(browser);

						browser.goTo("http://localhost:3333/admin/leagues/teams/1/delete");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/admin/leagues/show/1");
						assertNull(Team.find.where().eq("id", 1l).findUnique());
					}
				});
	}

	@Test
	public void deleteGameTest() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						loginTestAdmin(browser);

						browser.goTo("http://localhost:3333/admin/leagues/games/1/delete");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/admin/leagues/show/1");
						assertNull(Game.find.where().eq("id", 1l).findUnique());
					}
				});
	}

	@Test
	public void editGameTest() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						loginTestAdmin(browser);

						browser.goTo("http://localhost:3333/admin/leagues/games/1");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/admin/leagues/games/1");
						browser.fill("#matchday").with("3");
						browser.submit("#editGame");
						Game testGame = Game.find.where().eq("id", 1l)
								.findUnique();
						assertEquals(3, testGame.matchday);
					}
				});
	}

	@Test
	public void addUserTest() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						loginTestAdmin(browser);

						League testLeague = League.find.where().eq("id", 1l)
								.findUnique();
						int userCount = testLeague.users.size();
						User user = User.findByEmail("admin@bet.com");
						testLeague.addUser("admin@bet.com");

						assertEquals(userCount + 1, testLeague.users.size());
						assertTrue(testLeague.users.contains(user));
					}
				});
	}

	@Test
	public void removeUserTest() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						loginTestAdmin(browser);

						League testLeague = League.find.where().eq("id", 1l)
								.findUnique();
						int userCount = testLeague.users.size();

						testLeague.removeUser("admin@test.com");

						assertEquals(userCount - 1, testLeague.users.size());
					}
				});
	}

	@Test
	public void validEditTest() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						loginTestAdmin(browser);

						browser.goTo("http://localhost:3333/admin/leagues/1");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/admin/leagues/1");

						browser.fill("#name").with("Edited");
						browser.fill("#matchdayCount").with("66");
						browser.fill("#pointsForRightResult").with("12");
						browser.fill("#pointsForRightWinner").with("3");
						browser.submit("#editLeague");

						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/admin/leagues/");

						League editedLeague = League.find.where().eq("id", 1l)
								.findUnique();
						assertNotNull(editedLeague);
						assertEquals(66, editedLeague.matchdayCount);
						assertEquals(12, editedLeague.pointsForRightResult);
						assertEquals(3, editedLeague.pointsForRightWinner);
					}
				});
	}

	private Team findTeamByName(String teamName) {
		return Team.find.where().eq("name", teamName).findUnique();
	}
}
