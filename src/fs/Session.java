package fs;

import java.io.*;
import java.security.*;
import java.util.*;
import javax.crypto.*;
import javax.security.cert.*; // using java.security.cert.* fails to compile

public class Session {
	public  PrivateKey      key;
	public  PublicKey       pub;
	private X509Certificate cert;

	boolean make(PrivateKey key, X509Certificate cert)
		throws Exception
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

	Session(String file, String pass, String certFile, boolean inMemory)
		throws Exception
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

		// TODO: validate cert
		//cert.checkValidity(); // <- expirado

		make(key, cert);
	}

	static boolean keysFormAPair(PrivateKey key, PublicKey pub)
		throws Exception
	{
		byte[] challenge = new byte[64];

		Cipher pubCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		Cipher keyCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

		pubCipher.init(Cipher.ENCRYPT_MODE, pub);
		keyCipher.init(Cipher.DECRYPT_MODE, key);

		byte[] encriptedChallenge = pubCipher.doFinal(challenge);
		byte[] decriptedChallenge = keyCipher.doFinal(encriptedChallenge);

		return Arrays.equals(challenge, decriptedChallenge);
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
