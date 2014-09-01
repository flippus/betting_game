package models;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHelper {

	public static String getEncryptedPassword(String password, String salt) {
		String algorithm = "PBKDF2WithHmacSHA1";
		int derivedKeyLength = 160;
		int iterations = 50000;

		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(),
				iterations, derivedKeyLength);

		SecretKeyFactory f = null;

		String encryptedPassword = "";
		try {
			f = SecretKeyFactory.getInstance(algorithm);
			byte[] passwordArray = f.generateSecret(spec).getEncoded();
			for (int i = 0; i < passwordArray.length; i++) {
				encryptedPassword += passwordArray[i];
			}
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			// do nothing
		}

		return encryptedPassword;
	}

	public static String generateSalt() {
		SecureRandom random = null;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
		}

		byte[] salt = random.generateSeed(8);
		String saltString = "";
		for (int i = 0; i < salt.length; i++) {
			saltString += salt[i];
		}

		return saltString;
	}

	public static String generateNewPassword() {
		SecureRandom random = new SecureRandom();
		byte passwordArray[] = new byte[10];

		random.nextBytes(passwordArray);
		String newPassword = "";
		for (int i = 0; i < passwordArray.length; i++) {
			newPassword += passwordArray[i];
		}

		return newPassword;
	}

}
