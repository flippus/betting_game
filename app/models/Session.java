package models;

import play.db.ebean.Model;

public class Session extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String email;
	public String password;

	public String validate() {
		User user = User.authenticate(email, password);
		if (user == null) {
			return "Invalid user or password";
		}
		return null;
	}

}
