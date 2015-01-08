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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import models.User;

import org.junit.Test;

import play.libs.F.Callback;
import play.test.TestBrowser;

public class RegistrationIntegrationTest {

    @Test
    public void validRegistration() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())),
                HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {

                browser.goTo("http://localhost:3333/register");
                assertThat(browser.url()).isEqualTo(
                        "http://localhost:3333/register");

                browser.fill("#email").with("test@register.com");
                browser.fill("#password").with("password");
                browser.fill("#confirmPassword").with("password");
                browser.fill("#firstname").with("test");
                browser.fill("#lastname").with("registration");
                browser.submit("#register");

                assertThat(browser.url()).isEqualTo(
                        "http://localhost:3333/login");
                assertNotNull(User.findByEmail("test@register.com"));
            }
        });
    }

    @Test
    public void invalidConfirmRegistration() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())),
                HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {

                browser.goTo("http://localhost:3333/register");
                assertThat(browser.url()).isEqualTo(
                        "http://localhost:3333/register");

                browser.fill("#email").with("test@register.com");
                browser.fill("#password").with("password");
                browser.fill("#confirmPassword").with("password2");
                browser.fill("#firstname").with("test");
                browser.fill("#lastname").with("registration");
                browser.submit("#register");

                assertThat(browser.url()).isEqualTo(
                        "http://localhost:3333/register");
                assertNull(User.findByEmail("test@register.com"));
            }
        });
    }

    @Test
    public void testEmptyRegistration() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())),
                HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {

                browser.goTo("http://localhost:3333/register");
                assertThat(browser.url()).isEqualTo(
                        "http://localhost:3333/register");

                browser.submit("#register");

                assertThat(browser.url()).isEqualTo(
                        "http://localhost:3333/register");
            }
        });
    }

    @Test
    public void testWrongRegistration() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())),
                HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {

                browser.goTo("http://localhost:3333/register");
                assertThat(browser.url()).isEqualTo(
                        "http://localhost:3333/register");

                browser.fill("#email").with("test");
                browser.fill("#password").with("password");
                browser.fill("#confirmPassword").with("password");
                browser.fill("#firstname").with("test");
                browser.fill("#lastname").with("registration");
                browser.submit("#register");

                assertThat(browser.url()).isEqualTo(
                        "http://localhost:3333/register");
            }
        });
    }

}
