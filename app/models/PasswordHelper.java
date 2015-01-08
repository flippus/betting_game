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
            // do nothing
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
