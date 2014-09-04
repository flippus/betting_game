package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.joda.time.DateTime;

import play.data.format.Formats;
import play.data.validation.Constraints.Min;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

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

@Entity
@Table(schema = "public")
public class League extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	public long id;

	@Required
	@Formats.NonEmpty
	public String name;

	@Required
	@Min(1)
	public int pointsForRightResult;

	@Required
	@Min(1)
	public int pointsForRightWinner;

	@Required
	@Min(1)
	public int matchdayCount;

	@ManyToMany(mappedBy = "leagues", cascade = CascadeType.ALL)
	public Set<User> users;

	@OneToMany(mappedBy = "league", cascade = CascadeType.ALL)
	public List<Team> teams;

	@OneToMany(mappedBy = "league", cascade = CascadeType.ALL)
	public List<Game> games;

	@OneToMany(cascade = CascadeType.ALL)
	public List<UserPoints> userPoints;

	public int actualMatchday;

	public LeagueCalculator leagueCalculator;

	public League() {
		leagueCalculator = new LeagueCalculator(this);
	}

	public static Finder<Long, League> find = new Finder<Long, League>(
			Long.class, League.class);

	public static List<League> all() {
		return find.all();
	}

	public void addUser(String email) {
		users.add(User.findByEmail(email));
		this.save();
		calculateBetPoints();
	}

	public void removeUser(String email) {
		users.remove(User.findByEmail(email));
		findUserPointsByUser(email, id).delete();
		this.save();
		calculateBetPoints();
	}

	public int getUserCount() {
		return users.size();
	}

	public int getTeamCount() {
		return Team.find.where().eq("league_id", id).findList().size();
	}

	public List<Team> getAllTeamsByLeague() {
		return Team.findAllTeamsByLeague(id);
	}

	public List<Game> getAllGamesByLeague() {
		return Game.findAllGamesByLeague(id);
	}

	public List<Game> getAllUnbetableGamesByMatchday(int matchday) {
		List<Game> gameList = Game.find.where().eq("league_id", id)
				.eq("matchday", matchday).findList();
		for (int i = 0; i < gameList.size(); i++) {
			Game game = gameList.get(i);
			if (game.matchDate.isAfter(DateTime.now()) && game.played == false) {
				gameList.remove(i);
				i--;
			}
		}
		return gameList;
	}

	public int getBetableGamesCountByMatchday(int matchday) {
		// -1 due to for-loop in view
		return getAllBetableGamesByMatchday(matchday).size() - 1;
	}

	public List<Game> getAllBetableGamesByMatchday(int matchday) {
		return Game.find.where().eq("league_id", id).eq("matchday", matchday)
				.gt("match_date", DateTime.now()).eq("played", false)
				.findList();
	}

	public Set<User> getAllUsersByLeague() {
		return users;
	}

	public List<Game> getAllGamesByMatchday(int matchday) {
		return Game.findAllGamesByLeagueAndMatchday(id, matchday);
	}

	public List<User> findAllUsersOutOfLeague() {
		List<User> allUsers = User.find.all();
		allUsers.removeAll(new ArrayList<>(users));

		return allUsers;
	}

	public List<Integer> getAllMatchdays() {
		List<Integer> matchdayList = new ArrayList<>();
		for (int i = 0; i < matchdayCount; i++) {
			matchdayList.add(i + 1);
		}
		return matchdayList;
	}

	public int getMatchday() {
		int gamesCount = getBetableGamesCountByMatchday(actualMatchday);

		if (gamesCount >= 0) {
			return actualMatchday;
		}

		return actualMatchday + 1;
	}

	public int getUserPoints(String userId) {
		UserPoints userPoints = findUserPointsByUser(userId, id);
		if (userPoints == null) {
			return 0;
		}
		return userPoints.points;
	}

	// TODO: move to league calculator?
	public void calculateBetPoints() {
		List<Game> playedGameList = Game.findAllGamesByLeague(id);
		List<User> userList = new ArrayList<>(users);

		for (int i = 0; i < userList.size(); i++) {
			int betPoints = 0;
			int rightWinnerCount = 0;
			int rightResultCount = 0;
			for (int j = 0; j < playedGameList.size(); j++) {
				Bet bet = null;
				if ((bet = Game.findBetByGameAndUser(userList.get(i).email,
						playedGameList.get(j).id)) != null) {
					if (playedGameList.get(j).getWinner() == bet.getBetWinner()) {
						if (bet.getBetPoints() == 1) {
							betPoints += pointsForRightResult;
							rightResultCount++;
						} else if (bet.getBetPoints() == 2) {
							betPoints += pointsForRightWinner;
							rightWinnerCount++;
						}
					}

				}
			}
			UserPoints.createOrUpdateUserPointsByUser(userList.get(i).email,
					betPoints, rightResultCount, rightWinnerCount, id);
		}
		if (userList.size() > 0) {
			leagueCalculator.calculateUserRanking();
		}
	}

	// TODO: move to league calculator?
	public void calculateLeagueTable() {
		// TODO: why not use findAllPlayedGames?!
		// maybe cause of missing default values in db?!
		// or resetting data after changes
		List<Game> playedGameList = Game.findAllGamesByLeague(id);
		List<Team> teamList = Team.findAllTeamsByLeague(id);

		int highestMatchday = 1;
		for (int i = 0; i < teamList.size(); i++) {
			int goals = 0;
			int goalsAgainst = 0;
			int wins = 0;
			int draws = 0;
			int losses = 0;
			int points = 0;
			if (playedGameList.size() == 0) {
				teamList.get(i).updateTeamPointsByLeague(wins, draws, losses,
						points, goals, goalsAgainst);
			} else {
				for (int j = 0; j < playedGameList.size(); j++) {
					Game game = playedGameList.get(j);
					if (game.played) {
						if (highestMatchday < game.matchday) {
							highestMatchday = game.matchday;
						}
						if (game.teamHome.id == teamList.get(i).id) {
							goals += game.goalsHomeTeam;
							goalsAgainst += game.goalsAwayTeam;
							if (game.getWinner() == 1) {
								wins++;
								points += 3;
							} else if (game.getWinner() == 2) {
								losses++;
							} else if (game.getWinner() == 3) {
								points++;
								draws++;
							}
						} else if (game.teamAway.id == teamList.get(i).id) {
							goals += game.goalsAwayTeam;
							goalsAgainst += game.goalsHomeTeam;
							if (game.getWinner() == 1) {
								losses++;
							} else if (game.getWinner() == 2) {
								wins++;
								points += 3;
							} else if (game.getWinner() == 3) {
								points++;
								draws++;
							}
						}
					}
					if (j + 1 == playedGameList.size()) {
						teamList.get(i).updateTeamPointsByLeague(wins, draws,
								losses, points, goals, goalsAgainst);
					}
				}
			}
		}
		actualMatchday = highestMatchday;
		save();
		leagueCalculator.calculateTeamRanking();
	}

	public int getUserPosition(String userEmail) {
		UserPoints userPoints = findUserPointsByUser(userEmail, id);
		if (userPoints == null) {
			return 0;
		}
		return userPoints.position;
	}

	public static UserPoints findUserPointsByUser(String userEmail,
			Long leagueId) {
		return UserPoints.find.where().eq("user_email", userEmail)
				.eq("league_id", leagueId).findUnique();
	}

	public void deleteLeague() {
		delete();
	}

}
