package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.format.Formats;
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
public class Team extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	public long id;

	@Required
	@Formats.NonEmpty
	public String name;

	@Required
	@ManyToOne
	public League league;

	@OneToMany(mappedBy = "teamAway", cascade = CascadeType.ALL)
	public List<Game> awayGames;

	@OneToMany(mappedBy = "teamHome", cascade = CascadeType.ALL)
	public List<Game> homeGames;

	public int matches;
	public int wins;
	public int draws;
	public int losses;
	public int points;
	public int goals;
	public int goalsAgainst;
	public int goalsDifference;
	public int position;

	public static Finder<Long, Team> find = new Finder<Long, Team>(Long.class,
			Team.class);

	public static List<Team> all() {
		return find.all();
	}

	public static List<Team> findAllTeamsByLeague(Long id) {
		return Team.find.where().eq("league_id", id).findList();
	}

	public void updateTeamPointsByLeague(int wins, int draws, int losses,
			int points, int goals, int goalsAgainst) {
		this.matches = wins + draws + losses;
		this.wins = wins;
		this.draws = draws;
		this.losses = losses;
		this.points = points;
		this.goalsDifference = goals - goalsAgainst;
		this.goals = goals;
		this.goalsAgainst = goalsAgainst;
		this.save();
	}

}
