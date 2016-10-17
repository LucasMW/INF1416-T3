package fs;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;
import java.util.Base64.Decoder;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.spec.DESKeySpec;
import javax.security.cert.*; // using java.security.cert.* fails to compile
import java.security.spec.PKCS8EncodedKeySpec;

class Util {

	static SecretKey DESWithSHA1PRNGFromPassword(String password)
	{
		try {
			// retrieve DES secret from password
			byte[] privKeySpec = new byte[8];
			SecureRandom rng = SecureRandom.getInstance("SHA1PRNG");
			rng.setSeed(password.getBytes());
			rng.nextBytes(privKeySpec);

			// retreive the DES key from the secret.
			return SecretKeyFactory
				.getInstance("DES")
				.generateSecret(new DESKeySpec(privKeySpec));
		} catch (NoSuchAlgorithmException e) {
		} catch (InvalidKeySpecException  e) {
			e.printStackTrace();
		} catch (InvalidKeyException      e) {
			e.printStackTrace();
		}
		return null;
	}

	static Cipher DESCipher(SecretKey k)
	{
		try {
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, k);

			return cipher;
		} catch (NoSuchAlgorithmException e) {
		} catch (NoSuchPaddingException   e) {
		} catch (InvalidKeyException      e) {
			e.printStackTrace();
		}
		return null;
	}

	static Cipher DESCipher(String password)
	{
		return DESCipher(DESWithSHA1PRNGFromPassword(password));
	}
}
