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
package models;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.format.Formats;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

// in postgresql you need to use another name for the table, because user is
// reserved
@Entity
@Table(schema = "public")
public class User extends Model {

    private static final long serialVersionUID = 1L;

    @Required
    @Formats.NonEmpty
    public String firstname;
    @Required
    @Formats.NonEmpty
    public String lastname;
    @Id
    @Required
    @Email
    public String email;
    public boolean isAdmin;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_league")
    public Set<League> leagues;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<UserPoints> userPoints;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<Bet> bets;
    @Required
    @MinLength(8)
    public String password;
    private String salt;

    public void setNewPassword() {
        String newPassword = PasswordHelper.generateNewPassword();
        Mailer.sendMail(email, newPassword);
        this.password = PasswordHelper.getEncryptedPassword(newPassword, salt);
        this.save();
    }

    public void savePassword(String password) {
        this.password = PasswordHelper.getEncryptedPassword(password, salt);
        this.save();
    }

    public Set<League> getLeaguesByUser() {
        return leagues;
    }

    public String getName() {
        return firstname + " " + lastname;
    }

    public void deleteUser() {
        delete();
        Iterator<League> iterator = leagues.iterator();
        while (iterator.hasNext()) {
            League league = iterator.next();
            league.calculateBetPoints();
        }
    }

    public static Finder<String, User> find = new Finder<String, User>(
            String.class, User.class);

    public static List<User> all() {
        return find.all();
    }

    public static User authenticate(String email, String password) {
        User user = User.find.where().eq("email", email).findUnique();
        if (user == null) {
            return user;
        }
        String encryptedPassword = PasswordHelper.getEncryptedPassword(
                password, user.salt);
        return User.find.where().eq("email", email)
                .eq("password", encryptedPassword).findUnique();
    }

    public static User findByEmail(String email) {
        return User.find.where().eq("email", email).findUnique();
    }

    public static void create(User user) {
        user.salt = PasswordHelper.generateSalt();
        user.password = PasswordHelper.getEncryptedPassword(user.password,
                user.salt);
        user.save();
    }

    public boolean isUserMemberOfLeague(Long leagueId) {
        League league = League.find.byId(leagueId);
        return leagues.contains(league);
    }
}
