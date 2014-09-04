package functionaltests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import models.User;

import org.junit.Test;

/**
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
