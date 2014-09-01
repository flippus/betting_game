package controllers;

import static play.data.Form.form;

import java.util.Map;

import models.Bet;
import models.Bets;
import models.Game;
import models.League;
import models.LeagueCalculator;
import models.User;

import org.joda.time.DateTime;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.bet.highscore;
import views.html.bet.index;
import views.html.bet.table;

@Security.Authenticated(SecurityController.class)
public class BetController extends Controller {

	public static User getCurrentUser() {
		return User.findByEmail(session().get("email"));
	}

	public static Result checkMemberOfLeague(Long leagueId) {
		if (!SecurityController.isMemberOfLeague(leagueId)) {
			return redirect(routes.ApplicationController.index());
		}
		return null;
	}

	public static Result index(Long leagueId) {
		if (checkMemberOfLeague(leagueId) != null) {
			return checkMemberOfLeague(leagueId);
		}
		Form<Bets> betForm = form(Bets.class);
		League league = League.find.byId(leagueId);
		return ok(index.render(league, betForm, getCurrentUser(),
				league.getMatchday()));
	}

	public static Result changeMatchday(Long leagueId) {
		if (checkMemberOfLeague(leagueId) != null) {
			return checkMemberOfLeague(leagueId);
		}
		Form<Bets> betForm = form(Bets.class).bindFromRequest();
		Map<String, String> map = betForm.data();
		Object matchdayKey = betForm.data().keySet().toArray()[0];
		League league = League.find.byId(leagueId);
		int matchday = league.getMatchday();
		if (map.get(matchdayKey) != "") {
			matchday = Integer.valueOf(map.get(matchdayKey));
		} else {
			flash("error", "You must select a matchday");
		}
		return ok(index.render(league, betForm, getCurrentUser(), matchday));
	}

	public static Result save(Long leagueId) {
		if (checkMemberOfLeague(leagueId) != null) {
			return checkMemberOfLeague(leagueId);
		}
		Form<Bets> betForm = form(Bets.class).bindFromRequest();
		League league = League.find.byId(leagueId);
		Map<String, String> map = betForm.data();
		Object[] keys = betForm.data().keySet().toArray();
		int matchday = 0;
		try {
			for (int i = 0; i < map.size() / 4; i++) {

				// [0] = user; [1] = game; [2] = goalsHome; [3] = goalsAway;
				String[] keyArray = new String[4];
				for (int j = 0; j < map.size(); j++) {
					String keyString = (String) keys[j];
					String searchString = "[" + i + "]";
					if (keyString.contains("day")) {
						matchday = Integer.valueOf(map.get(keyString));
					}
					if (keyString.contains(searchString)) {
						if (keyString.contains("user")) {
							keyArray[0] = map.get(keyString);
						} else if (keyString.contains("game")) {
							keyArray[1] = map.get(keyString);
						} else if (keyString.contains("goalsHome")) {
							keyArray[2] = map.get(keyString);
						} else if (keyString.contains("goalsAway")) {
							keyArray[3] = map.get(keyString);
						}
					}
					if (j == map.size() - 1) {
						Game game = Game.find.byId(Long.valueOf(keyArray[1]));
						User user = User.findByEmail(keyArray[0]);
						Bet bet = new Bet();
						if (game.findBetByGameAndUser(user.email) != null) {
							bet = game.findBetByGameAndUser(user.email);
						}
						bet.user = user;
						bet.game = game;
						bet.goalsHomeTeam = Integer.valueOf(keyArray[2]);
						bet.goalsAwayTeam = Integer.valueOf(keyArray[3]);
						if (bet.goalsHomeTeam < 0 || bet.goalsAwayTeam < 0) {
							throw new IllegalArgumentException(
									"Goals must be positive");
						}
						// prevents that you can bet a game after game
						// start if you looked at the website before
						if (game.matchDate.isAfter(DateTime.now())) {
							bet.save();
						}
					}
				}
			}
		} catch (IllegalArgumentException e) {
			flash("error", "Goals must be real and positive numbers!");
			return badRequest(index.render(league, betForm, getCurrentUser(),
					matchday));
		}
		flash("success", "Bets have been created");
		return ok(index.render(league, betForm, getCurrentUser(), matchday));
	}

	public static Result showTable(Long leagueId) {
		if (checkMemberOfLeague(leagueId) != null) {
			return checkMemberOfLeague(leagueId);
		}
		LeagueCalculator leagueCalculator = League.find.byId(leagueId).leagueCalculator;
		return ok(table.render(leagueCalculator.league.name, leagueCalculator,
				getCurrentUser()));
	}

	public static Result showHighscore(Long leagueId) {
		if (checkMemberOfLeague(leagueId) != null) {
			return checkMemberOfLeague(leagueId);
		}
		League league = League.find.byId(leagueId);
		league.calculateBetPoints();
		return ok(highscore.render(league.name, league.leagueCalculator,
				getCurrentUser()));
	}

}
