package functionaltests;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.util.List;

import models.League;
import models.Team;

import org.junit.Test;

public class TeamPointsFunctionalTest {

	private Team team1;
	private Team team2;
	private League league;

	private void createTeams() {
		team1 = new Team();
		team1.name = "FC Bayern München";
		team1.league = league;
		team1.save();

		team2 = new Team();
		team2.name = "FC Augsburg";
		team2.league = league;
		team2.save();
	}

	@Test
	public void testCreateTeamPoints() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				league = new League();
				league.save();

				assertEquals(league.teams.size(), 0);

				createTeams();
				team1.updateTeamPointsByLeague(1, 0, 0, 3, 1, 0);
				team2.updateTeamPointsByLeague(2, 0, 0, 2, 2, 1);
				List<Team> teamList = findAllTeamsByLeague(league.id);
				assertEquals(team1.name, teamList.get(0).name);
				assertEquals(3, teamList.get(0).points);
				assertEquals(1, teamList.get(0).goals);
				assertEquals(0, teamList.get(0).goalsAgainst);
				assertEquals(team2.name, teamList.get(1).name);
				assertEquals(2, teamList.get(1).points);
				assertEquals(2, teamList.get(1).goals);
				assertEquals(1, teamList.get(1).goalsAgainst);

			}
		});
	}

	@Test
	public void testUpdateTeamPoints() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				league = new League();
				league.save();

				assertEquals(league.teams.size(), 0);

				createTeams();
				team1.updateTeamPointsByLeague(1, 0, 0, 3, 1, 0);
				List<Team> teamList = findAllTeamsByLeague(league.id);
				assertEquals(team1.name, teamList.get(0).name);
				assertEquals(3, teamList.get(0).points);
				assertEquals(1, teamList.get(0).goals);
				assertEquals(0, teamList.get(0).goalsAgainst);
				team1.updateTeamPointsByLeague(1, 0, 0, 6, 3, 2);
				teamList = findAllTeamsByLeague(league.id);
				assertEquals(teamList.get(0).name, team1.name);
				assertEquals(6, teamList.get(0).points);
				assertEquals(3, teamList.get(0).goals);
				assertEquals(2, teamList.get(0).goalsAgainst);
			}
		});
	}

	private static List<Team> findAllTeamsByLeague(Long leagueId) {
		return Team.find.where().eq("league_id", leagueId)
				.orderBy("points desc").findList();
	}
}
