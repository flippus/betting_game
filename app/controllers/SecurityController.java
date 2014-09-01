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
