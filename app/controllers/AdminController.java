package controllers;

import static play.data.Form.form;

import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.BooleanForm;
import models.Game;
import models.League;
import models.StringForm;
import models.Team;
import models.User;
import models.UserPoints;

import org.joda.time.DateTime;

import play.data.Form;
import play.data.format.Formatters;
import play.data.format.Formatters.SimpleFormatter;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.editUserForm;
import views.html.admin.userIndex;
import views.html.league.createForm;
import views.html.league.editForm;
import views.html.league.index;
import views.html.league.show;
import views.html.league.game.createGameForm;
import views.html.league.game.editGameForm;
import views.html.league.team.createTeamForm;
import views.html.league.team.editTeamForm;

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

@Security.Authenticated(SecurityController.class)
public class AdminController extends Controller {

	public static Result GO_HOME = redirect(routes.AdminController.index());

	public static User getCurrentUser() {
		return User.findByEmail(session().get("email"));
	}

	public static Result checkAdmin() {
		if (!SecurityController.isAdmin()) {
			return redirect(routes.ApplicationController.index());
		}
		return null;
	}

	public static Result index() {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		return ok(index.render(League.all(), getCurrentUser()));
	}

	public static Result show(Long id) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		League league = League.find.byId(id);
		Form<StringForm> userForm = form(StringForm.class);
		return ok(show.render(league, userForm, getCurrentUser()));
	}

	public static Result edit(Long id) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		Form<League> leagueForm = form(League.class).fill(League.find.byId(id));
		return ok(editForm.render(id, leagueForm, getCurrentUser()));
	}

	public static Result update(Long id) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		Form<League> leagueForm = form(League.class).bindFromRequest();
		try {
			if (leagueForm.get().pointsForRightWinner >= leagueForm.get().pointsForRightResult) {
				leagueForm
						.reject("pointsForRightWinner",
								"You have to choose lower points than for right result!");
			}
		} catch (IllegalStateException e) {

		}
		if (leagueForm.hasErrors()) {
			return badRequest(editForm.render(id, leagueForm, getCurrentUser()));
		}
		List<Game> gameList = Game.find.where().eq("league_id", id)
				.orderBy("matchday desc").findList();
		if (gameList.size() > 0) {
			int matchday = gameList.get(0).matchday;
			if (leagueForm.get().matchdayCount < matchday) {
				leagueForm.get().matchdayCount = matchday;
			}
		}
		leagueForm.get().update(id);
		if (UserPoints.find.where().eq("league_id", id).findList().size() > 0) {
			League.find.byId(id).calculateBetPoints();
		}
		flash("success", "League " + leagueForm.get().name
				+ " has been updated");
		return GO_HOME;
	}

	public static Result create() {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		Form<League> leagueForm = form(League.class);
		return ok(createForm.render(leagueForm, getCurrentUser()));
	}

	public static Result save() {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		Form<League> leagueForm = form(League.class).bindFromRequest();
		try {
			if (leagueForm.get().pointsForRightWinner >= leagueForm.get().pointsForRightResult) {
				leagueForm
						.reject("pointsForRightWinner",
								"You have to choose lower points than for right result!");
			}
		} catch (IllegalStateException e) {

		}
		if (leagueForm.hasErrors()) {
			return badRequest(createForm.render(leagueForm, getCurrentUser()));
		}
		leagueForm.get().save();
		League.find.byId(leagueForm.get().id).calculateBetPoints();
		flash("success", "League " + leagueForm.get().name
				+ " has been created");
		return GO_HOME;
	}

	public static Result delete(Long id) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		League.find.byId(id).deleteLeague();
		flash("success", "League has been deleted");
		return GO_HOME;
	}

	public static Result createTeam(Long id) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		Form<Team> teamForm = form(Team.class);
		return ok(createTeamForm.render(id, teamForm, getCurrentUser()));
	}

	public static Result saveTeam(Long id) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		Form<Team> teamForm = form(Team.class).bindFromRequest();
		if (teamForm.hasErrors()) {
			return badRequest(createTeamForm.render(id, teamForm,
					getCurrentUser()));
		}
		teamForm.get().save();
		flash("success", "Team " + teamForm.get().name + " has been created");
		return redirect(routes.AdminController.show(id));
	}

	public static Result editTeam(Long teamId) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		Form<Team> teamForm = form(Team.class).fill(Team.find.byId(teamId));
		Long leagueId = Team.find.byId(teamId).league.id;
		return ok(editTeamForm.render(teamId, leagueId, teamForm,
				getCurrentUser()));
	}

	public static Result updateTeam(Long teamId) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		Long leagueId = Team.find.byId(teamId).league.id;
		Form<Team> teamForm = form(Team.class).bindFromRequest();
		User user = getCurrentUser();
		if (teamForm.hasErrors()) {
			return badRequest(editTeamForm.render(teamId, leagueId, teamForm,
					user));
		}
		teamForm.get().update(teamId);
		flash("success", "Team " + teamForm.get().name + " has been updated");
		return redirect(routes.AdminController.show(leagueId));
	}

	public static Result deleteTeam(Long teamId) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		League league = Team.find.ref(teamId).league;
		Team.find.byId(teamId).delete();
		league.calculateBetPoints();
		league.calculateLeagueTable();
		flash("success", "Team has been deleted");
		return redirect(routes.AdminController.show(league.id));
	}

	public static Result createGame(Long id) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		Game game = new Game();
		game.goalsHomeTeam = 0;
		game.goalsAwayTeam = 0;
		Form<Game> gameForm = form(Game.class).fill(game);
		List<Team> teams = Team.findAllTeamsByLeague(id);
		return ok(createGameForm.render(id, teams, gameForm, getCurrentUser()));
	}

	public static Result saveGame(Long id) {

		Formatters.register(DateTime.class, new DateFormatter());

		if (checkAdmin() != null) {
			return checkAdmin();
		}
		Form<Game> gameForm = form(Game.class).bindFromRequest();
		Map<String, String> map = gameForm.data();
		Object[] keys = gameForm.data().keySet().toArray();
		String matchdayKey = null;
		String leagueKey = null;
		String teamAwayKey = null;
		String teamHomeKey = null;
		for (int i = 0; i < map.size(); i++) {
			String keyString = (String) keys[i];
			if (keyString.contains("matchday")) {
				matchdayKey = (String) keys[i];
			} else if (keyString.contains("league")) {
				leagueKey = (String) keys[i];
			} else if (keyString.contains("teamAway")) {
				teamAwayKey = (String) keys[i];
			} else if (keyString.contains("teamHome")) {
				teamHomeKey = (String) keys[i];
			}
		}
		try {
			if (Integer.valueOf(map.get(matchdayKey)) > League.find.byId(Long
					.valueOf(map.get(leagueKey))).matchdayCount) {
				gameForm.reject("matchday",
						"Matchday can't be higher than matchday count of league");
			}
		} catch (NumberFormatException e) {

		}
		try {
			if (Integer.valueOf(map.get(teamAwayKey)) == Integer.valueOf(map
					.get(teamHomeKey))) {
				gameForm.reject("teamHome",
						"You must choose two different teams!");
				gameForm.reject("teamAway",
						"You must choose two different teams!");
			}
		} catch (NumberFormatException e) {

		}
		if (gameForm.hasErrors()) {
			List<Team> teams = Team.findAllTeamsByLeague(id);
			return badRequest(createGameForm.render(id, teams, gameForm,
					getCurrentUser()));
		}
		gameForm.get().save();
		if (gameForm.get().played) {
			League league = League.find.byId(gameForm.get().league.id);
			league.calculateBetPoints();
			league.calculateLeagueTable();
		}
		flash("success", "Game "
				+ Team.find.byId(gameForm.get().teamHome.id).name + " - "
				+ Team.find.byId(gameForm.get().teamAway.id).name
				+ " has been created");
		return redirect(routes.AdminController.show(id));
	}

	public static Result editGame(Long gameId) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}

		Formatters.register(DateTime.class, new DateFormatter());

		Form<Game> gameForm = form(Game.class).fill(Game.find.byId(gameId));
		Long leagueId = Game.find.byId(gameId).league.id;
		List<Team> teams = Team.findAllTeamsByLeague(leagueId);
		return ok(editGameForm.render(gameId, leagueId, teams, gameForm,
				getCurrentUser()));
	}

	public static Result updateGame(Long gameId) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}

		Formatters.register(DateTime.class, new DateFormatter());

		Long leagueId = Game.find.byId(gameId).league.id;
		Form<Game> gameForm = form(Game.class).bindFromRequest();
		int matchday = gameForm.get().matchday;
		int matchdayCount = Game.find.byId(gameId).league.matchdayCount;
		try {
			if (matchday > matchdayCount) {
				gameForm.reject("matchday",
						"Matchday can't be higher than matchday count of league");
			}
		} catch (IllegalStateException e) {

		}
		Long homeTeamId = gameForm.get().teamHome.id;
		Long awayTeamId = gameForm.get().teamAway.id;
		try {
			if (homeTeamId == awayTeamId) {
				gameForm.reject("teamHome",
						"You must choose two different teams!");
				gameForm.reject("teamAway",
						"You must choose two different teams!");
			}
		} catch (IllegalStateException e) {

		}
		if (gameForm.hasErrors()) {
			List<Team> teams = Team.findAllTeamsByLeague(leagueId);
			return badRequest(editGameForm.render(gameId, leagueId, teams,
					gameForm, getCurrentUser()));
		}
		gameForm.get().update(gameId);
		League league = League.find.byId(gameForm.get().league.id);
		league.calculateBetPoints();
		league.calculateLeagueTable();
		flash("success", "Game "
				+ Team.find.byId(gameForm.get().teamHome.id).name + " - "
				+ Team.find.byId(gameForm.get().teamAway.id).name
				+ " has been updated");
		return redirect(routes.AdminController.show(league.id));
	}

	public static Result deleteGame(Long gameId) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		League league = Game.find.ref(gameId).league;
		Game.find.ref(gameId).delete();
		league.calculateBetPoints();
		league.calculateLeagueTable();
		flash("success", "Game has been deleted");
		return redirect(routes.AdminController.show(league.id));
	}

	public static Result addUser(Long id) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		Form<StringForm> userForm = form(StringForm.class).bindFromRequest();
		League league = League.find.byId(id);
		if (!userForm.get().email.isEmpty()) {
			league.addUser(userForm.get().email);
			flash("success", "User " + userForm.get().email + " has been added");
		} else {
			flash("error", "There is no user to adding");
		}
		return redirect(routes.AdminController.show(id));
	}

	public static Result removeUser(Long id, String email) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		League.find.byId(id).removeUser(email);
		return redirect(routes.AdminController.show(id));
	}

	public static Result userIndex() {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		List<User> userList = User.find.all();
		return ok(userIndex.render(userList, getCurrentUser()));
	}

	public static Result editUser(String email) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		if (email.equals(getCurrentUser().email)) {
			flash("error", "You are not allowed to edit yourself!");
			return redirect(routes.AdminController.userIndex());
		}
		User user = User.findByEmail(email);
		Form<BooleanForm> userForm = form(BooleanForm.class).fill(
				new BooleanForm(user.isAdmin));
		return ok(editUserForm.render(user, userForm, getCurrentUser()));
	}

	public static Result updateUser(String email) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		Form<BooleanForm> userForm = form(BooleanForm.class).bindFromRequest();
		User user = User.findByEmail(email);
		if (userForm.hasErrors()) {
			return badRequest(editUserForm.render(user, userForm,
					getCurrentUser()));
		}
		user.isAdmin = userForm.get().isAdmin;
		user.save();
		flash("success", "User " + user.getName() + " has been updated");
		return redirect(routes.AdminController.userIndex());
	}

	public static Result deleteUser(String email) {
		if (checkAdmin() != null) {
			return checkAdmin();
		}
		if (email.equals(getCurrentUser().email)) {
			flash("error", "You can't delete yourself!");
			return redirect(routes.AdminController.userIndex());
		}
		User.find.ref(email).deleteUser();
		flash("success", "User has been deleted");
		return redirect(routes.AdminController.userIndex());
	}

	private static class DateFormatter extends SimpleFormatter<DateTime> {

		private Pattern timePattern = Pattern
				.compile("^([1-9]|0[1-9]|([1-3]\\d))[.](0[1-9]|[1-9]|(1[0-2]))[.]([0-9]{4})\\s(\\d|([0-2]\\d))[:]([0-5]\\d)$");

		@Override
		public DateTime parse(String input, Locale l) throws ParseException {
			Matcher m = timePattern.matcher(input);
			if (!m.find()) {
				throw new ParseException("No valid Input", 0);
			}
			int day = Integer.valueOf(m.group(1));
			int month = Integer.valueOf(m.group(3));
			int year = Integer.valueOf(m.group(5));
			int hour = Integer.valueOf(m.group(6));
			int min = Integer.valueOf(m.group(8));
			return new DateTime(year, month, day, hour, min);
		}

		@Override
		public String print(DateTime dateTime, Locale l) {
			return dateTime.toString("dd.MM.yyyy HH:mm");
		}
	}

}
