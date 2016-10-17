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

public class Session {
	public  PrivateKey      key;
	public  PublicKey       pub;
	private X509Certificate cert;

	boolean make(PrivateKey key, X509Certificate cert)
	{
		PublicKey pub = cert.getPublicKey();

		if (keysFormAPair(key, pub)) {
			this.cert= cert;
			this.key = key;
			this.pub = pub;
			return true;
		}

		throw new SecurityException("keys missmatch");
	}

	public Session(String file, String pass, String certFile, boolean inMemory)
		throws javax.crypto.BadPaddingException, InvalidKeySpecException,
						  CertificateException, IOException
	{
		FileHelper f = new FileHelper(file, Util.DESCipher(pass));
		key = PEM.readPrivateKey(f.br);
		f.close();

		InputStream in = inMemory?
			new ByteArrayInputStream(certFile.getBytes()):
			new FileInputStream(certFile);

		// load certificate
		cert = X509Certificate.getInstance(in);
		in.close();

		make(key, cert);
		cert.checkValidity(); // <- expirado
	}

	static boolean keysFormAPair(PrivateKey key, PublicKey pub)
	{
		byte[] challenge = new byte[64];

		try {
			Cipher pubCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			Cipher keyCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

			pubCipher.init(Cipher.ENCRYPT_MODE, pub);
			keyCipher.init(Cipher.DECRYPT_MODE, key);

			byte[] encriptedChallenge = pubCipher.doFinal(challenge);
			byte[] decriptedChallenge = keyCipher.doFinal(encriptedChallenge);

			return Arrays.equals(challenge, decriptedChallenge);
		} catch (NoSuchAlgorithmException  e) { e.printStackTrace();
		} catch (NoSuchPaddingException    e) { e.printStackTrace();
		} catch (InvalidKeyException       e) {
		} catch (BadPaddingException       e) {
		} catch (IllegalBlockSizeException e) {}

		return false;
	}

	public static void main(String args[])
		throws Exception
	{
		if (args.length < 3) {
			System.out.printf(
					"ERROR: this test expects 3 parameters:\n"+
					"\t- a DESWithSHA1PRNG file with a pkcs8 private key inside.\n"+
					"\t- the password for it.\n"+
					"\t- a digital certificate\n");
			System.exit(1);
		}
		Session s = new Session(args[0], args[1], args[2], false);
	}
}
