package controllers;

import models.Session;
import models.StringForm;
import models.User;
import play.data.Form;
import static play.data.Form.form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.session.login;
import views.html.session.register;
import views.html.session.newpassword;

public class SessionController extends Controller {

	public static Result index() {
		return ok(login.render(form(Session.class)));
	}

	public static Result login() {
		Form<Session> loginForm = form(Session.class).bindFromRequest();
		if (loginForm.hasErrors()) {
			return badRequest(login.render(loginForm));
		}
		session("email", loginForm.get().email);
		return redirect(routes.ApplicationController.index());
	}

	public static Result logout() {
		session().clear();
		return redirect(routes.SessionController.index());
	}

	public static Result openRegistration() {
		return ok(register.render(form(User.class)));
	}

	public static Result register() {
		Form<User> userForm = form(User.class).bindFromRequest();

		if (!userForm.field("password").value()
				.equals(userForm.field("confirmPassword").value())) {
			userForm.reject("confirmPassword", "Password doesn't match");
		}
		if (userForm.hasErrors()) {
			return badRequest(register.render(userForm));
		}

		User.create(userForm.get());
		flash("success", "Account " + userForm.get().getName()
				+ " has been created");
		return redirect(routes.SessionController.index());
	}

	public static Result requestPassword() {
		return ok(newpassword.render(form(StringForm.class)));
	}

	public static Result getNewPassword() {
		Form<StringForm> userForm = form(StringForm.class).bindFromRequest();

		if (User.findByEmail(userForm.get().email) == null) {
			flash("error", "This email does not exist");
			return badRequest(newpassword.render(userForm));
		}

		User.findByEmail(userForm.get().email).setNewPassword();
		flash("success", "New Password has been sent");
		return redirect(routes.SessionController.index());
	}
}
