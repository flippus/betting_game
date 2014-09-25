package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.ebean.Model;

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
