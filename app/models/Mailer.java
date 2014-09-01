package models;

import com.typesafe.plugin.*;

public class Mailer {

	public static void sendMail(String email, String password) {
		MailerAPI mail = play.Play.application().plugin(MailerPlugin.class)
				.email();
		mail.setSubject("New password");
		mail.addRecipient(email);
		mail.addFrom("bettinggame@unibz.it");
		String text = "Here is your new password: " + password;
		mail.send(text);
	}
}
