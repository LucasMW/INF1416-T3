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

class PEM {

	static final String PEM_PRIVATE_START = "-----BEGIN PRIVATE KEY-----";
	static final String PEM_PRIVATE_END   = "-----END PRIVATE KEY-----";

	public static PrivateKey readPrivateKey(BufferedReader br)
		throws Exception
	{
		String b64 = "",
			   line= "";

		while ((line=br.readLine()) != null) {
			if (line.equals(PEM_PRIVATE_START)) { break; }
		}
		while ((line=br.readLine()) != null) {
			if (line.equals(PEM_PRIVATE_END))   { break; }
			b64 += line;
		}

		//System.out.print(b64);
		PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(
				java.util.Base64.getDecoder().decode(b64));
		return KeyFactory.getInstance("RSA").generatePrivate(pkcs8);
	}

	public static void main(String args[])
		throws Exception
	{
		if (args.length < 2) {
			System.out.printf(
					"ERROR: this test expects 2 parameters:\n"+
					"\t- a DESWithSHA1PRNG file with a pkcs8 private key inside.\n"+
					"\t- the password for it.\n");
			System.exit(1);
		}
		FileHelper  f  = new FileHelper(args[0], Util.DESCipher(args[1]));
		PrivateKey key = PEM.readPrivateKey(f.br);
		f.close();
	}
}
