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
package functionaltests;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import models.Bet;
import models.Game;
import models.User;

import org.junit.Test;

public class BetFunctionalTest {

    User user = null;
    Game game = null;
    Bet bet = null;

    public void createData() {
        game = new Game();
        game.played = false;
        game.goalsAwayTeam = 1;
        game.goalsHomeTeam = 3;
        game.save();
        bet = new Bet();
        bet.game = game;
        bet.user = user;
        bet.save();
    }

    @Test
    public void testBetWinner() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            @Override
            public void run() {
                createData();
                bet.goalsHomeTeam = 3;
                bet.goalsAwayTeam = 1;
                bet.save();

                assertEquals(bet.getBetWinner(), 1);

                bet.goalsHomeTeam = 0;
                bet.save();

                assertEquals(bet.getBetWinner(), 2);

                bet.goalsAwayTeam = 0;
                bet.save();

                assertEquals(bet.getBetWinner(), 3);
            }
        });
    }

    @Test
    public void testBetPoints() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            @Override
            public void run() {
                createData();

                assertEquals(bet.getBetPoints(), 0);

                game.played = true;
                game.save();

                bet.goalsHomeTeam = 3;
                bet.goalsAwayTeam = 1;
                bet.save();

                assertEquals(bet.getBetPoints(), 1);

                bet.goalsAwayTeam = 0;
                bet.save();

                assertEquals(bet.getBetPoints(), 2);
            }
        });
    }

}
