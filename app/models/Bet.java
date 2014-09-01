package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.Constraints.Min;
import play.db.ebean.Model;

@Entity
@Table(schema = "public")
public class Bet extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	public Long id;
	@ManyToOne
	public Game game;
	@ManyToOne
	public User user;
	@Min(0)
	public int goalsHomeTeam;
	@Min(0)
	public int goalsAwayTeam;

	public int getBetWinner() {
		if (goalsHomeTeam > goalsAwayTeam) {
			return 1;
		} else if (goalsAwayTeam > goalsHomeTeam) {
			return 2;
		} else {
			return 3;
		}
	}

	public int getBetPoints() {
		if (game.played) {
			if (goalsHomeTeam == game.goalsHomeTeam
					&& goalsAwayTeam == game.goalsAwayTeam) {
				return 1;
			} else {
				return 2;
			}
		} else {
			return 0;
		}
	}

	public static Finder<Long, Bet> find = new Finder<Long, Bet>(Long.class,
			Bet.class);

	public static List<Bet> all() {
		return find.all();
	}
}
