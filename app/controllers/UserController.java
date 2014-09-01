package controllers;

import models.PasswordForm;
import models.User;
import play.data.Form;
import static play.data.Form.form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.user.changepassword;

@Security.Authenticated(SecurityController.class)
public class UserController extends Controller {

	public static Result changePassword() {
		if (User.findByEmail(session().get("email")) == null) {
			session().clear();
			return redirect(routes.SessionController.index());
		}
		return ok(changepassword.render(form(PasswordForm.class),
				User.findByEmail(session().get("email"))));
	}

	public static Result setPassword() {
		if (User.findByEmail(session().get("email")) == null) {
			session().clear();
			return redirect(routes.SessionController.index());
		}
		Form<PasswordForm> userForm = form(PasswordForm.class)
				.bindFromRequest();

		if (userForm.field("password").value().length() < 8) {
			userForm.reject("password", "Password must have 8 characters");
		}
		if (!userForm.field("password").value()
				.equals(userForm.field("confirmPassword").value())) {
			userForm.reject("confirmPassword", "Password doesn't match");
		}
		if (userForm.hasErrors()) {
			return badRequest(changepassword.render(userForm,
					User.findByEmail(session().get("email"))));
		}

		User.findByEmail(session().get("email")).savePassword(
				userForm.get().password);
		flash("success", "New Password has been set");
		return redirect(routes.ApplicationController.index());
	}
}
