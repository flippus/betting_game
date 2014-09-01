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
