package integrationtests;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;
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
