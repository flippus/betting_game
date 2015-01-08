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
package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.joda.time.DateTime;

import play.data.validation.Constraints.Min;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
@Table(schema = "public")
public class Game extends Model {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    public long id;

    @Required
    @ManyToOne
    public Team teamHome;

    @Required
    @ManyToOne
    public Team teamAway;

    @Required
    @Min(1)
    public int matchday;

    @Min(0)
    public int goalsHomeTeam;
    @Min(0)
    public int goalsAwayTeam;

    public boolean played;

    @ManyToOne
    public League league;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    public List<Bet> bets;

    @Required
    public DateTime matchDate;

    public static Finder<Long, Game> find = new Finder<Long, Game>(Long.class,
            Game.class);

    public static List<Game> all() {
        return find.all();
    }

    public Game findAllPlayedGamesByLeague(Long leagueId) {
        return Game.find.where().eq("played", true).eq("league_id", leagueId)
                .findUnique();
    }

    public static Bet findBetByGameAndUser(String email, Long gameId) {
        return Bet.find.where().eq("user_email", email).eq("game_id", gameId)
                .findUnique();
    }

    public Bet findBetByGameAndUser(String email) {
        return Bet.find.where().eq("user_email", email).eq("game_id", id)
                .findUnique();
    }

    public static List<Game> findAllGamesByLeague(Long id) {
        return Game.find.where().eq("league_id", id).findList();
    }

    public static List<Game> findAllGamesByLeagueAndMatchday(Long id,
            int matchday) {
        return Game.find.where().eq("league_id", id).eq("matchday", matchday)
                .findList();
    }

    public String getResult() {
        if (played) {
            return goalsHomeTeam + ":" + goalsAwayTeam;
        }
        return "-:-";
    }

    public int getWinner() {
        if (played) {
            if (goalsHomeTeam > goalsAwayTeam) {
                return 1;
            } else if (goalsAwayTeam > goalsHomeTeam) {
                return 2;
            } else {
                return 3;
            }
        }
        return 0;
    }

}
