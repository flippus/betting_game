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
