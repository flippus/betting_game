package functionaltests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.League;
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

public class UserLeagueFunctionalTest {

	private User user1;
	private User user2;
	private League league;

	private void startUp() {
		Set<League> leagueSet = new HashSet<League>();
		league = new League();
		league.save();
		leagueSet.add(league);

		user1 = new User();
		user1.email = "user1@test.it";
		user1.firstname = "flp";
		user1.lastname = "unibz";
		user1.password = "password";
		user1.isAdmin = true;
		user1.leagues = leagueSet;
		User.create(user1);

		user2 = new User();
		user2.email = "user2@test.it";
		user2.firstname = "flp";
		user2.lastname = "unibz";
		user2.password = "password";
		user2.isAdmin = true;
		User.create(user2);

		league = League.find.byId(league.id);
	}

	@Test
	public void testAllUserArePossible() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				League league = new League();

				List<User> userList = league.findAllUsersOutOfLeague();

				assertEquals(6, userList.size());
			}
		});
	}

	@Test
	public void testPossibleUsers() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				startUp();

				List<User> userList = league.findAllUsersOutOfLeague();

				assertEquals(7, userList.size());
				assertTrue(userList.contains(user2));

				assertEquals(1, league.users.size());
				assertEquals(user1.email,
						new ArrayList<User>(league.users).get(0).email);

			}
		});
	}

	@Test
	public void testIsMemberOfLeague() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			@Override
			public void run() {
				startUp();

				assertTrue(user1.isUserMemberOfLeague(league.id));

				assertFalse(user2.isUserMemberOfLeague(league.id));

				User falseUser = new User();
				falseUser.email = "wrong@email.com";

				assertFalse(falseUser.isUserMemberOfLeague(league.id));
			}
		});
	}
}
