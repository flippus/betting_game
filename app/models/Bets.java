package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;

import play.db.ebean.Model;

public class Bets extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@OneToMany
	public List<Bet> betList = new ArrayList<>();

	public int matchday;

}
