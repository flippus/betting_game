package integrationtests;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import org.junit.Test;

import play.libs.F.Callback;
import play.test.TestBrowser;

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

public class ChangePWIntegrationTest {

	public void loginTestUser(TestBrowser browser, String password) {
		browser.goTo("http://localhost:3333");
		assertThat(browser.url()).isEqualTo("http://localhost:3333/login");
		browser.fill("#email").with("user@test.com");
		browser.fill("#password").with(password);
		browser.submit("#login");
		assertThat(browser.url()).isEqualTo("http://localhost:3333/");
	}

	@Test
	public void validChange() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						loginTestUser(browser, "password");

						browser.goTo("http://localhost:3333/changepassword");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/changepassword");

						browser.fill("#password").with("testnewpw");
						browser.fill("#confirmPassword").with("testnewpw");
						browser.submit("#changePW");

						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/");

						browser.goTo("http://localhost:3333/logout");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/login");

						loginTestUser(browser, "testnewpw");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/");
					}
				});
	}

	@Test
	public void invalidConfirmChange() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						loginTestUser(browser, "password");

						browser.goTo("http://localhost:3333/changepassword");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/changepassword");

						browser.fill("#password").with("testnewpw");
						browser.fill("#confirmPassword").with("testwrongpw");
						browser.submit("#changePW");

						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/setpassword");

						browser.goTo("http://localhost:3333/logout");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/login");

						loginTestUser(browser, "password");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/");
					}
				});
	}

	@Test
	public void invalidLengthChange() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						loginTestUser(browser, "password");

						browser.goTo("http://localhost:3333/changepassword");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/changepassword");

						browser.fill("#password").with("hello");
						browser.fill("#confirmPassword").with("hello");
						browser.submit("#changePW");

						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/setpassword");

						browser.goTo("http://localhost:3333/logout");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/login");

						loginTestUser(browser, "password");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/");
					}
				});
	}
}
