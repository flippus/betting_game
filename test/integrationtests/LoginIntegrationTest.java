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

public class LoginIntegrationTest {

	@Test
	public void validLogin() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {

						browser.goTo("http://localhost:3333");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/login");

						browser.fill("#email").with("flp@unibz.it");
						browser.fill("#password").with("unibz");
						browser.submit("#login");

						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/");
					}
				});
	}

	@Test
	public void invalidLogin() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {

						browser.goTo("http://localhost:3333");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/login");

						browser.fill("#email").with("adsf");
						browser.fill("#password").with("unz");
						browser.submit("#login");

						assertThat(browser.url()).isNotEqualTo(
								"http://localhost:3333");
					}
				});
	}

}
