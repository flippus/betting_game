package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.Constraints.Min;
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
