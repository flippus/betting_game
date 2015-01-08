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
package integrationtests;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import models.Bet;
import models.Game;
import models.User;

import org.junit.Test;

import play.libs.F.Callback;
import play.test.TestBrowser;

public class BetIntegrationTest {

    private void loginTestUser(TestBrowser browser) {
        browser.goTo("http://localhost:3333");
        assertThat(browser.url()).isEqualTo("http://localhost:3333/login");
        browser.fill("#email").with("user@test.com");
        browser.fill("#password").with("password");
        browser.submit("#login");
        assertThat(browser.url()).isEqualTo("http://localhost:3333/");
    }

    private void loginTestAdmin(TestBrowser browser) {
        browser.goTo("http://localhost:3333");
        assertThat(browser.url()).isEqualTo("http://localhost:3333/login");
        browser.fill("#email").with("admin@test.com");
        browser.fill("#password").with("password");
        browser.submit("#login");
        assertThat(browser.url()).isEqualTo("http://localhost:3333/");
    }

    @Test
    public void acceessBetViewsTest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())),
                HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                loginTestUser(browser);

                browser.goTo("http://localhost:3333/league/1/bets");
                assertThat(browser.url()).isEqualTo(
                        "http://localhost:3333/league/1/bets");
                browser.goTo("http://localhost:3333/league/1/matchday?matchday=1");
                assertThat(browser.url())
                .isEqualTo(
                        "http://localhost:3333/league/1/matchday?matchday=1");
                browser.goTo("http://localhost:3333/league/1/highscore");
                assertThat(browser.url()).isEqualTo(
                        "http://localhost:3333/league/1/highscore");
                browser.goTo("http://localhost:3333/league/1/table");
                assertThat(browser.url()).isEqualTo(
                        "http://localhost:3333/league/1/table");

            }
        });
    }

    @Test
    public void validUserBetTest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())),
                HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                loginTestUser(browser);

                browser.goTo("http://localhost:3333/league/1/bets");
                browser.goTo("http://localhost:3333/league/1/matchday?matchday=1");
                assertThat(browser.url())
                .isEqualTo(
                        "http://localhost:3333/league/1/matchday?matchday=1");

                browser.fill("#bets_0_goalsHomeTeam").with("1");
                browser.fill("#bets_0_goalsAwayTeam").with("0");
                browser.submit("#submitBet");

                assertThat(browser.url()).isEqualTo(
                        "http://localhost:3333/league/betting/1");

                Bet bet = Game
                        .findBetByGameAndUser("user@test.com", 1l);
                assertNotNull(bet);
                assertEquals(bet.game.id, 1l);
                assertEquals(bet.user,
                        User.findByEmail("user@test.com"));
                assertEquals(bet.goalsHomeTeam, 1);
                assertEquals(bet.goalsAwayTeam, 0);

            }
        });
    }

    @Test
    public void invalidUserBetTest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())),
                HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                loginTestUser(browser);

                browser.goTo("http://localhost:3333/league/1/bets");
                browser.goTo("http://localhost:3333/league/1/matchday?matchday=1");
                assertThat(browser.url())
                .isEqualTo(
                        "http://localhost:3333/league/1/matchday?matchday=1");

                browser.fill("#bets_0_goalsHomeTeam").with("1");
                browser.fill("#bets_0_goalsAwayTeam").with("-1");
                browser.submit("#submitBet");

                assertThat(browser.url()).isEqualTo(
                        "http://localhost:3333/league/betting/1");

                assertNull(Game.findBetByGameAndUser("user@test.com",
                        1l));
            }
        });
    }

    @Test
    public void noNumberUserBetTest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())),
                HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                loginTestUser(browser);

                browser.goTo("http://localhost:3333/league/1/bets");
                browser.goTo("http://localhost:3333/league/1/matchday?matchday=1");
                assertThat(browser.url())
                .isEqualTo(
                        "http://localhost:3333/league/1/matchday?matchday=1");

                browser.fill("#bets_0_goalsHomeTeam").with("1");
                browser.fill("#bets_0_goalsAwayTeam").with("asd");
                browser.submit("#submitBet");

                assertThat(browser.url()).isEqualTo(
                        "http://localhost:3333/league/betting/1");

                assertNull(Game.findBetByGameAndUser("user@test.com",
                        1l));
            }
        });
    }

    @Test
    public void validAdminBetTest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())),
                HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                loginTestAdmin(browser);

                browser.goTo("http://localhost:3333/league/1/bets");
                browser.goTo("http://localhost:3333/league/1/matchday?matchday=2");
                assertThat(browser.url())
                .isEqualTo(
                        "http://localhost:3333/league/1/matchday?matchday=2");

                browser.fill("#bets_0_goalsHomeTeam").with("5");
                browser.fill("#bets_0_goalsAwayTeam").with("2");
                browser.submit("#submitBet");

                assertThat(browser.url()).isEqualTo(
                        "http://localhost:3333/league/betting/1");

                Bet bet = Game.findBetByGameAndUser("admin@test.com",
                        2l);
                assertNotNull(bet);
                assertEquals(bet.game.id, 2l);
                assertEquals(bet.user,
                        User.findByEmail("admin@test.com"));
                assertEquals(bet.goalsHomeTeam, 5);
                assertEquals(bet.goalsAwayTeam, 2);
            }
        });
    }

    @Test
    public void invalidAdminBetTest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())),
                HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                loginTestAdmin(browser);

                browser.goTo("http://localhost:3333/league/1/bets");
                browser.goTo("http://localhost:3333/league/1/matchday?matchday=2");
                assertThat(browser.url())
                .isEqualTo(
                        "http://localhost:3333/league/1/matchday?matchday=2");

                browser.fill("#bets_0_goalsHomeTeam").with("-1");
                browser.fill("#bets_0_goalsAwayTeam").with("0");
                browser.submit("#submitBet");

                assertThat(browser.url()).isEqualTo(
                        "http://localhost:3333/league/betting/1");

                assertNull(Game.findBetByGameAndUser("admin@test.com",
                        2l));
            }
        });
    }

    @Test
    public void noNumberAdminBetTest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())),
                HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                loginTestAdmin(browser);

                browser.goTo("http://localhost:3333/league/1/bets");
                browser.goTo("http://localhost:3333/league/1/matchday?matchday=2");
                assertThat(browser.url())
                .isEqualTo(
                        "http://localhost:3333/league/1/matchday?matchday=2");

                browser.fill("#bets_0_goalsHomeTeam").with("franz");
                browser.fill("#bets_0_goalsAwayTeam").with("joseph");
                browser.submit("#submitBet");

                assertThat(browser.url()).isEqualTo(
                        "http://localhost:3333/league/betting/1");

                assertNull(Game.findBetByGameAndUser("admin@test.com",
                        2l));
            }
        });
    }
}
