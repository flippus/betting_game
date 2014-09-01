package controllers;

import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.welcome.*;

@Security.Authenticated(SecurityController.class)
public class ApplicationController extends Controller {

	public static Result index() {
		User user = User.findByEmail(session().get("email"));
		if (user == null) {
			session().clear();
			return redirect(routes.SessionController.index());
		}
		return ok(index.render(user));
	}
}
