package functionaltests;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.util.List;

import models.Game;
import models.League;
import models.Team;

import org.joda.time.DateTime;
import org.junit.Test;

/**
 *
 * Copyright (C) 2014 Philipp Neugebauer, Florian Klement
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

public class LeagueTableFunctionalTest {

	private Team team1;
	private Team team2;
	private Team team3;
	private Team team4;
	private League league;

	public void createTeams() {
		league = new League();
		league.save();

		team1 = new Team();
		team1.name = "FC Bayern München";
		team1.league = league;
		team1.save();

		team2 = new Team();
		team2.name = "FC Augsburg";
		team2.league = league;
		team2.save();

		team3 = new Team();
		team3.name = "Borussia Dortmund";
		team3.league = league;
		team3.save();

		team4 = new Team();
		team4.name = "FC Schalke";
		team4.league = league;
		team4.save();
	}

	@Test
	public void testSamePositions() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				createTeams();
				team1.updateTeamPointsByLeague(1, 0, 0, 3, 1, 0);
				team2.updateTeamPointsByLeague(1, 0, 0, 3, 1, 0);
				team3.updateTeamPointsByLeague(1, 0, 0, 3, 1, 0);
				team4.updateTeamPointsByLeague(1, 0, 0, 3, 1, 0);
				assertEquals(league.leagueCalculator.getOrderedTeamsCount(), 3);
				league.leagueCalculator.calculateTeamRanking();
				List<Team> rankedTeamList = league.leagueCalculator
						.getOrderedTeams();
				assertEquals(rankedTeamList.get(0).name, team1.name);
				assertEquals(rankedTeamList.get(0).position, 1);
				assertEquals(rankedTeamList.get(1).name, team2.name);
				assertEquals(rankedTeamList.get(1).position, 1);
				assertEquals(rankedTeamList.get(2).name, team3.name);
				assertEquals(rankedTeamList.get(2).position, 1);
				assertEquals(rankedTeamList.get(3).name, team4.name);
				assertEquals(rankedTeamList.get(3).position, 1);
			}
		});
	}

	@Test
	public void testSameAndDifferentPositions() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				createTeams();
				team1.updateTeamPointsByLeague(2, 0, 0, 6, 4, 1);
				team2.updateTeamPointsByLeague(2, 0, 0, 6, 4, 1);
				team3.updateTeamPointsByLeague(2, 0, 0, 6, 3, 0);
				team4.updateTeamPointsByLeague(2, 0, 0, 6, 2, 0);
				assertEquals(league.leagueCalculator.getOrderedTeamsCount(), 3);
				league.leagueCalculator.calculateTeamRanking();
				List<Team> rankedTeamList = league.leagueCalculator
						.getOrderedTeams();
				assertEquals(rankedTeamList.get(0).name, team1.name);
				assertEquals(rankedTeamList.get(0).position, 1);
				assertEquals(rankedTeamList.get(1).name, team2.name);
				assertEquals(rankedTeamList.get(1).position, 1);
				assertEquals(rankedTeamList.get(2).name, team3.name);
				assertEquals(rankedTeamList.get(2).position, 3);
				assertEquals(rankedTeamList.get(3).name, team4.name);
				assertEquals(rankedTeamList.get(3).position, 4);
			}
		});
	}

	@Test
	public void testDifferentPositions() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				createTeams();
				team1.updateTeamPointsByLeague(3, 0, 0, 9, 3, 0);
				team2.updateTeamPointsByLeague(3, 0, 0, 7, 2, 0);
				team3.updateTeamPointsByLeague(3, 0, 0, 4, 1, 0);
				team4.updateTeamPointsByLeague(3, 0, 0, 3, 1, 0);
				assertEquals(league.leagueCalculator.getOrderedTeamsCount(), 3);
				league.leagueCalculator.calculateTeamRanking();
				List<Team> rankedTeamList = league.leagueCalculator
						.getOrderedTeams();
				assertEquals(rankedTeamList.get(0).name, team1.name);
				assertEquals(rankedTeamList.get(0).position, 1);
				assertEquals(rankedTeamList.get(1).name, team2.name);
				assertEquals(rankedTeamList.get(1).position, 2);
				assertEquals(rankedTeamList.get(2).name, team3.name);
				assertEquals(rankedTeamList.get(2).position, 3);
				assertEquals(rankedTeamList.get(3).name, team4.name);
				assertEquals(rankedTeamList.get(3).position, 4);
			}
		});
	}

	private Game createGame() {
		Game game = new Game();
		game.league = league;
		game.matchday = 1;
		game.played = false;
		game.teamHome = team1;
		game.teamAway = team2;
		game.matchDate = DateTime.now().plusHours(2);
		game.goalsHomeTeam = 0;
		game.goalsAwayTeam = 0;
		game.save();

		return game;
	}

	@Test
	public void testGetMatchday() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				createTeams();
				Game game = createGame();

				assertEquals(league.getMatchday(), 1);

				game.played = true;
				game.update();

				league.calculateLeagueTable();

				assertEquals(league.getMatchday(), 2);
			}
		});
	}

}
