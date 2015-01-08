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
package controllers;

import models.User;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class SecurityController extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("email");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.SessionController.index());
    }

    public static boolean isAdmin() {
        if (User.findByEmail(Context.current().request().username()) == null) {
            return false;
        }
        return User.findByEmail(Context.current().request().username()).isAdmin;
    }

    public static boolean isMemberOfLeague(Long leagueId) {
        User user = User.findByEmail(Context.current().request().username());
        if (user == null) {
            return false;
        }
        return user.isUserMemberOfLeague(leagueId);
    }
}
