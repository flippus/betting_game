package integrationtests;

import static org.fest.assertions.Assertions.*;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import org.junit.Test;
import play.libs.F.Callback;
import play.test.TestBrowser;

public class AdminAccessIntegrationTest {

	public void loginTestUser(TestBrowser browser) {
		browser.goTo("http://localhost:3333");
		assertThat(browser.url()).isEqualTo("http://localhost:3333/login");
		browser.fill("#email").with("user@test.com");
		browser.fill("#password").with("password");
		browser.submit("#login");
		assertThat(browser.url()).isEqualTo("http://localhost:3333/");
	}

	public void loginTestAdmin(TestBrowser browser) {
		browser.goTo("http://localhost:3333");
		assertThat(browser.url()).isEqualTo("http://localhost:3333/login");
		browser.fill("#email").with("admin@test.com");
		browser.fill("#password").with("password");
		browser.submit("#login");
		assertThat(browser.url()).isEqualTo("http://localhost:3333/");
	}

	@Test
	public void authorizedAccessTest() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						loginTestAdmin(browser);

						browser.goTo("http://localhost:3333/admin/leagues/");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/admin/leagues/");
						browser.goTo("http://localhost:3333/admin/users/");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/admin/users/");
					}
				});
	}

	@Test
	public void unauthorizedAccessTest() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						loginTestUser(browser);

						browser.goTo("http://localhost:3333/admin/leagues/");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/");
						browser.goTo("http://localhost:3333/admin/users/");
						assertThat(browser.url()).isEqualTo(
								"http://localhost:3333/");
					}
				});
	}
}
