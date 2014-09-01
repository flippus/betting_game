package models;

import play.db.ebean.Model;

public class BooleanForm extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean isAdmin;

	public BooleanForm() {
	}

	public BooleanForm(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

}
