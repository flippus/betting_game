package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity
@Table(schema = "public")
public class UserPoints extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	public long id;
	@ManyToOne
	public User user;
	public int points;
	public int rightResultCount;
	public int rightWinnerCount;
	public int position;
	@ManyToOne
	public League league;

	public static Finder<Long, UserPoints> find = new Finder<Long, UserPoints>(
			Long.class, UserPoints.class);

	public static List<UserPoints> all() {
		return find.all();
	}

	public static void createOrUpdateUserPointsByUser(String userEmail,
			int points, int rightResultCount, int rightWinnerCount,
			Long leagueId) {
		UserPoints userPoints = null;
		if ((userPoints = UserPoints.find.where().eq("user_email", userEmail)
				.eq("league_id", leagueId).findUnique()) != null) {
		} else {
			userPoints = new UserPoints();
			userPoints.league = League.find.byId(leagueId);
			userPoints.user = User.findByEmail(userEmail);
		}
		userPoints.points = points;
		userPoints.rightResultCount = rightResultCount;
		userPoints.rightWinnerCount = rightWinnerCount;
		userPoints.save();
	}

}
