package functionaltests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import models.PasswordHelper;

import org.junit.Test;

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
