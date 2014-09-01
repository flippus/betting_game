package functionaltests;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import models.PasswordHelper;

import org.junit.Test;

public class PasswordHelperFunctionalTest {

	@Test
	public void testGetEncryptedPassword() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				String salt = PasswordHelper.generateSalt();

				String password1 = PasswordHelper.getEncryptedPassword(
						"password", salt);

				String password2 = PasswordHelper.getEncryptedPassword(
						"password", salt);

				assertEquals(password1, password2);

				String wrongPassword = PasswordHelper.getEncryptedPassword(
						"password", salt);

				assertNotSame(wrongPassword, password2);
			}
		});
	}

}
