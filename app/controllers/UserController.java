package controllers;

import static play.data.Form.form;
import models.PasswordForm;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.user.changepassword;

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
