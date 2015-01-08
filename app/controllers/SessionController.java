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

import static play.data.Form.form;
import models.Session;
import models.StringForm;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.session.login;
import views.html.session.newpassword;
import views.html.session.register;

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
