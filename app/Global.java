import java.util.HashSet;
import java.util.Set;

import models.Bet;
import models.Game;
import models.League;
import models.Team;
import models.User;

import org.joda.time.DateTime;

import play.Application;
import play.GlobalSettings;
import play.db.ebean.Transactional;

import com.avaje.ebean.Ebean;

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

public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		InitialData.insert(app);
	}

	static class InitialData {

		@Transactional
		public static void insert(Application app) {
			if (Ebean.find(User.class).findRowCount() == 0) {

				testObjects();

				Set<User> userSet = new HashSet<User>();

				User user = new User();
				user.email = "flp@unibz.it";
				user.password = "unibz";
				user.firstname = "flp";
				user.lastname = "unibz";
				user.isAdmin = true;
				userSet.add(user);
				User.create(user);

				user = new User();
				user.email = "flp@uni-bamberg.de";
				user.password = "uniba";
				user.firstname = "flp";
				user.lastname = "uniba";
				user.isAdmin = false;
				userSet.add(user);
				User.create(user);

				user = new User();
				user.email = "test@uni-bamberg.de";
				user.password = "uniba";
				user.firstname = "test";
				user.lastname = "uniba";
				user.isAdmin = false;
				userSet.add(user);
				User.create(user);

				user = new User();
				user.email = "admin@bet.com";
				user.password = "password";
				user.firstname = "ad";
				user.lastname = "min";
				user.isAdmin = true;
				userSet.add(user);
				User.create(user);

				League league = new League();
				league.name = "Bundesliga";
				league.pointsForRightResult = 5;
				league.pointsForRightWinner = 2;
				league.matchdayCount = 34;
				league.users = userSet;
				Ebean.save(league);

				Team fcb = new Team();
				fcb.name = "FC Bayern München";
				fcb.league = league;
				Ebean.save(fcb);

				Team fca = new Team();
				fca.name = "FC Augsburg";
				fca.league = league;
				Ebean.save(fca);

				Team gf = new Team();
				gf.name = "SpVgg Greuther Fürth";
				gf.league = league;
				Ebean.save(gf);

				Team fcn = new Team();
				fcn.name = "FC Nürnberg";
				fcn.league = league;
				Ebean.save(fcn);

				Game fcafcb = new Game();
				fcafcb.matchday = 1;
				fcafcb.teamHome = fca;
				fcafcb.teamAway = fcb;
				fcafcb.goalsHomeTeam = 0;
				fcafcb.goalsAwayTeam = 0;
				fcafcb.league = league;
				fcafcb.played = false;
				fcafcb.matchDate = DateTime.now().minusHours(2);
				Ebean.save(fcafcb);

				Game gffcn = new Game();
				gffcn.matchday = 1;
				gffcn.teamHome = gf;
				gffcn.teamAway = fcn;
				gffcn.goalsHomeTeam = 0;
				gffcn.goalsAwayTeam = 0;
				gffcn.league = league;
				gffcn.played = false;
				gffcn.matchDate = DateTime.now().minusHours(2);
				Ebean.save(gffcn);

				Game fcbfca = new Game();
				fcbfca.matchday = 2;
				fcbfca.teamHome = fcb;
				fcbfca.teamAway = fca;
				fcbfca.goalsHomeTeam = 0;
				fcbfca.goalsAwayTeam = 0;
				fcbfca.league = league;
				fcbfca.played = false;
				fcbfca.matchDate = DateTime.now().plusHours(2);
				Ebean.save(fcbfca);

				Game fcngf = new Game();
				fcngf.matchday = 2;
				fcngf.teamHome = fcn;
				fcngf.teamAway = gf;
				fcngf.goalsHomeTeam = 0;
				fcngf.goalsAwayTeam = 0;
				fcngf.league = league;
				fcngf.played = false;
				fcngf.matchDate = DateTime.now().plusHours(2);
				Ebean.save(fcngf);

				Bet bet = new Bet();
				bet.game = Game.find.byId(4l);
				bet.user = User.findByEmail("flp@unibz.it");
				bet.goalsAwayTeam = 0;
				bet.goalsHomeTeam = 1;
				Ebean.save(bet);

				bet = new Bet();
				bet.game = Game.find.byId(4l);
				bet.user = User.findByEmail("test@uni-bamberg.de");
				bet.goalsAwayTeam = 2;
				bet.goalsHomeTeam = 0;
				Ebean.save(bet);

				bet = new Bet();
				bet.game = Game.find.byId(4l);
				bet.user = User.findByEmail("flp@uni-bamberg.de");
				bet.goalsAwayTeam = 2;
				bet.goalsHomeTeam = 0;
				Ebean.save(bet);

				bet = new Bet();
				bet.game = Game.find.byId(4l);
				bet.user = User.findByEmail("admin@bet.com");
				bet.goalsAwayTeam = 2;
				bet.goalsHomeTeam = 0;
				Ebean.save(bet);

				bet = new Bet();
				bet.game = Game.find.byId(3l);
				bet.user = User.findByEmail("flp@uni-bamberg.de");
				bet.goalsAwayTeam = 2;
				bet.goalsHomeTeam = 1;
				Ebean.save(bet);

				bet = new Bet();
				bet.game = Game.find.byId(3l);
				bet.user = User.findByEmail("admin@bet.com");
				bet.goalsAwayTeam = 2;
				bet.goalsHomeTeam = 0;
				Ebean.save(bet);

				Game game = Game.find.byId(4l);
				game.goalsHomeTeam = 0;
				game.goalsAwayTeam = 2;
				game.played = true;
				Ebean.save(game);

				game = Game.find.byId(3l);
				game.goalsHomeTeam = 1;
				game.goalsAwayTeam = 2;
				game.played = true;
				Ebean.save(game);

				league.calculateBetPoints();
			}
		}
	}

	private static void testObjects() {
		Set<User> userSet = new HashSet<User>();

		User testUser = new User();
		testUser = new User();
		testUser.email = "admin@test.com";
		testUser.password = "password";
		testUser.firstname = "test";
		testUser.lastname = "admin";
		testUser.isAdmin = true;
		userSet.add(testUser);
		User.create(testUser);

		testUser = new User();
		testUser.email = "user@test.com";
		testUser.password = "password";
		testUser.firstname = "test";
		testUser.lastname = "user";
		testUser.isAdmin = false;
		userSet.add(testUser);
		User.create(testUser);

		League testLeague = new League();
		testLeague.name = "Test League";
		testLeague.pointsForRightResult = 3;
		testLeague.pointsForRightWinner = 1;
		testLeague.matchdayCount = 15;
		testLeague.users = userSet;
		Ebean.save(testLeague);

		Team testA = new Team();
		testA.name = "Test Team A";
		testA.league = testLeague;
		Ebean.save(testA);

		Team testB = new Team();
		testB.name = "Test Team B";
		testB.league = testLeague;
		Ebean.save(testB);

		Game testGame1 = new Game();
		testGame1.matchday = 1;
		testGame1.teamHome = testA;
		testGame1.teamAway = testB;
		testGame1.goalsHomeTeam = 0;
		testGame1.goalsAwayTeam = 0;
		testGame1.league = testLeague;
		testGame1.played = false;
		testGame1.matchDate = DateTime.now().plusYears(1);
		Ebean.save(testGame1);

		Game testGame2 = new Game();
		testGame2.matchday = 2;
		testGame2.teamHome = testB;
		testGame2.teamAway = testA;
		testGame2.goalsHomeTeam = 0;
		testGame2.goalsAwayTeam = 0;
		testGame2.league = testLeague;
		testGame2.played = false;
		testGame2.matchDate = DateTime.now().plusYears(1);
		Ebean.save(testGame2);

		testLeague.calculateBetPoints();
	}
}
