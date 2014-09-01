package functionaltests;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import models.User;
import org.junit.Test;

public class SessionFunctionalTest {

	User user = null;

	public void createUser() {
		user = new User();
		user.email = "flp@test.it";
		user.firstname = "flp";
		user.lastname = "unibz";
		user.password = "password";
		user.isAdmin = true;
		User.create(user);
	}

	@Test
	public void testCorrectLogin() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				createUser();
				assertNotNull(User.authenticate(user.email, "password"));
			}
		});
	}

	@Test
	public void testLoginWithWrongPassword() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				createUser();
				assertNull(User.authenticate(user.email, "falschesPw"));
			}
		});
	}

	@Test
	public void testLoginWithWrongName() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				createUser();
				assertNull(User.authenticate("wrongLogin", "password"));
			}
		});
	}

	@Test
	public void testAttributeSetting() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				createUser();
				User newUser = User.authenticate(user.email, "password");
				assertNotNull(newUser);
			}
		});
	}
}
