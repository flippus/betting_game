/**
 *
 * Betting game realized with PlayFramework to bet different sport results with
 * other persons to determine the best better
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
package functionaltests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.util.List;

import models.Bet;
import models.Game;
import models.League;
import models.Team;
import models.User;

import org.joda.time.DateTime;
import org.junit.Test;

public class LeagueFunctionalTest {

    private League league;
    private User user;
    private Team team1;
    private Team team2;
    private Game game1;
    private Game game3;

    private void createData() {
        league = new League();
        league.name = "Bundesliga";
        league.pointsForRightResult = 5;
        league.pointsForRightWinner = 2;
        league.matchdayCount = 10;
        league.save();

        user = new User();
        user.email = "user4@test.it";
        user.firstname = "flp";
        user.lastname = "unibz";
        user.password = "password";
        user.isAdmin = true;
        User.create(user);

    }

    @Test
    public void testAddUser() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            @Override
            public void run() {
                createData();
                assertEquals(user.leagues.size(), 0);
                league.addUser(user.email);
                user = User.findByEmail(user.email);
                assertEquals(user.leagues.size(), 1);
            }
        });
    }

    private void createMoreData() {
        team1 = new Team();
        team1.name = "FC Bayern München";
        team1.league = league;
        team1.save();

        team2 = new Team();
        team2.name = "FC Augsburg";
        team2.league = league;
        team2.save();

        Game game = new Game();
        game.matchday = 2;
        game.teamHome = team1;
        game.teamAway = team2;
        game.goalsHomeTeam = 0;
        game.goalsAwayTeam = 0;
        game.league = league;
        game.played = false;
        game.matchDate = DateTime.now().plusHours(2);
        game.save();

        Bet bet = new Bet();
        bet.user = user;
        bet.game = game;
        bet.goalsHomeTeam = 0;
        bet.goalsAwayTeam = 0;
        bet.save();
    }

    @Test
    public void testRemoveUser() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            @Override
            public void run() {
                createData();
                league.addUser(user.email);
                createMoreData();
                league.calculateBetPoints();
                assertNotNull(League
                        .findUserPointsByUser(user.email, league.id));
                league.removeUser(user.email);
                assertEquals(user.leagues.size(), 0);
                assertNull(League.findUserPointsByUser(user.email, league.id));
            }
        });
    }

    public void createSomeGames() {
        Game game = new Game();
        game.matchday = 1;
        game.teamHome = team2;
        game.teamAway = team1;
        game.goalsHomeTeam = 3;
        game.goalsAwayTeam = 3;
        game.league = league;
        game.played = false;
        game.matchDate = DateTime.now().minusHours(2);
        game.save();

        game1 = new Game();
        game1.matchday = 2;
        game1.teamHome = team2;
        game1.teamAway = team1;
        game1.goalsHomeTeam = 1;
        game1.goalsAwayTeam = 0;
        game1.league = league;
        game1.played = false;
        game1.matchDate = DateTime.now().minusHours(2);
        game1.save();

        game3 = new Game();
        game3.matchday = 1;
        game3.teamHome = team2;
        game3.teamAway = team1;
        game3.goalsHomeTeam = 4;
        game3.goalsAwayTeam = 0;
        game3.league = league;
        game3.played = true;
        game3.matchDate = DateTime.now().plusHours(2);
        game3.save();

        game = new Game();
        game.matchday = 2;
        game.teamHome = team2;
        game.teamAway = team1;
        game.goalsHomeTeam = 1;
        game.goalsAwayTeam = 2;
        game.league = league;
        game.played = false;
        game.matchDate = DateTime.now().plusHours(2);
        game.save();

    }

    @Test
    public void testGetUnbetableGames() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            @Override
            public void run() {
                createData();
                createMoreData();
                createSomeGames();
                assertEquals(league.getAllUnbetableGamesByMatchday(1).size(), 2);
                assertEquals(league.getAllBetableGamesByMatchday(1).size(), 0);
                assertEquals(league.getAllUnbetableGamesByMatchday(2).size(), 1);
                assertEquals(league.getAllBetableGamesByMatchday(2).size(), 2);
            }
        });
    }

    @Test
    public void testCalculateBetPoints() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            @Override
            public void run() {
                League league = new League();
                league.name = "Bundesliga";
                league.pointsForRightResult = 5;
                league.pointsForRightWinner = 2;
                league.matchdayCount = 10;
                league.save();

                User user = new User();
                user.email = "user4@test.it";
                user.firstname = "flp";
                user.lastname = "unibz";
                user.password = "password";
                user.isAdmin = true;
                User.create(user);

                league.addUser(user.email);

                Team team1 = new Team();
                team1.name = "FC Bayern München";
                team1.league = league;
                team1.save();

                Team team2 = new Team();
                team2.name = "FC Augsburg";
                team2.league = league;
                team2.save();

                Game game = new Game();
                game.matchday = 1;
                game.teamHome = team1;
                game.teamAway = team2;
                game.goalsHomeTeam = 2;
                game.goalsAwayTeam = 0;
                game.league = league;
                game.played = true;
                game.matchDate = DateTime.now().minusHours(2);
                game.save();

                Game game1 = new Game();
                game1.matchday = 1;
                game1.teamHome = team2;
                game1.teamAway = team1;
                game1.goalsHomeTeam = 3;
                game1.goalsAwayTeam = 0;
                game1.league = league;
                game1.played = true;
                game1.matchDate = DateTime.now().minusHours(2);
                game1.save();

                Game game2 = new Game();
                game2.matchday = 1;
                game2.teamHome = team2;
                game2.teamAway = team1;
                game2.goalsHomeTeam = 3;
                game2.goalsAwayTeam = 3;
                game2.league = league;
                game2.played = true;
                game2.matchDate = DateTime.now().minusHours(2);
                game2.save();

                Bet bet0 = new Bet();
                bet0.user = user;
                bet0.game = game;
                bet0.goalsHomeTeam = 2;
                bet0.goalsAwayTeam = 0;
                bet0.save();

                league.calculateBetPoints();

                assertEquals(
                        League.findUserPointsByUser(user.email, league.id).points,
                        5);

                Bet bet1 = new Bet();
                bet1.user = user;
                bet1.game = game1;
                bet1.goalsHomeTeam = 2;
                bet1.goalsAwayTeam = 0;
                bet1.save();

                league.calculateBetPoints();
                assertEquals(
                        League.findUserPointsByUser(user.email, league.id).points,
                        7);

                Bet bet2 = new Bet();
                bet2.user = user;
                bet2.game = game2;
                bet2.goalsHomeTeam = 0;
                bet2.goalsAwayTeam = 1;
                bet2.save();

                league.calculateBetPoints();
                assertEquals(
                        League.findUserPointsByUser(user.email, league.id).points,
                        7);
            }
        });
    }

    @Test
    public void testCalculateLeagueTable() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            @Override
            public void run() {
                League league = new League();
                league.name = "Bundesliga";
                league.pointsForRightResult = 5;
                league.pointsForRightWinner = 2;
                league.matchdayCount = 10;
                league.save();

                User user = new User();
                user.email = "user4@test.it";
                user.firstname = "flp";
                user.lastname = "unibz";
                user.password = "password";
                user.isAdmin = true;
                User.create(user);

                league.addUser(user.email);

                Team team1 = new Team();
                team1.name = "FC Bayern München";
                team1.league = league;
                team1.save();

                Team team2 = new Team();
                team2.name = "FC Augsburg";
                team2.league = league;
                team2.save();

                Game game = new Game();
                game.matchday = 1;
                game.teamHome = team1;
                game.teamAway = team2;
                game.goalsHomeTeam = 3;
                game.goalsAwayTeam = 0;
                game.league = league;
                game.played = true;
                game.matchDate = DateTime.now().minusHours(2);
                game.save();

                Game game1 = new Game();
                game1.matchday = 1;
                game1.teamHome = team2;
                game1.teamAway = team1;
                game1.goalsHomeTeam = 2;
                game1.goalsAwayTeam = 0;
                game1.league = league;
                game1.played = true;
                game1.matchDate = DateTime.now().minusHours(2);
                game1.save();

                Game game2 = new Game();
                game2.matchday = 2;
                game2.teamHome = team2;
                game2.teamAway = team1;
                game2.goalsHomeTeam = 3;
                game2.goalsAwayTeam = 3;
                game2.league = league;
                game2.played = true;
                game2.matchDate = DateTime.now().minusHours(2);
                game2.save();

                Game game3 = new Game();
                game3.matchday = 2;
                game3.teamHome = team2;
                game3.teamAway = team1;
                game3.goalsHomeTeam = 0;
                game3.goalsAwayTeam = 1;
                game3.league = league;
                game3.played = true;
                game3.matchDate = DateTime.now().minusHours(2);
                game3.save();

                assertEquals(league.getAllUnbetableGamesByMatchday(1).size(), 2);
                assertEquals(league.getAllUnbetableGamesByMatchday(2).size(), 2);
                league.calculateLeagueTable();

                assertEquals(league.leagueCalculator.getOrderedTeamsCount(), 1);
                List<Team> teamList = league.leagueCalculator.getOrderedTeams();
                assertEquals(teamList.get(0).matches, 4);
                assertEquals(teamList.get(0).wins, 2);
                assertEquals(teamList.get(0).draws, 1);
                assertEquals(teamList.get(0).losses, 1);
                assertEquals(teamList.get(0).goals, 7);
                assertEquals(teamList.get(0).goalsAgainst, 5);
                assertEquals(teamList.get(0).points, 7);
                assertEquals(teamList.get(1).matches, 4);
                assertEquals(teamList.get(1).wins, 1);
                assertEquals(teamList.get(1).draws, 1);
                assertEquals(teamList.get(1).losses, 2);
                assertEquals(teamList.get(1).goals, 5);
                assertEquals(teamList.get(1).goalsAgainst, 7);
                assertEquals(teamList.get(1).points, 4);
            }
        });
    }
}
