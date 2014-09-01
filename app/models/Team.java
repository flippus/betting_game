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
