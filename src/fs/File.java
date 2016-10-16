package fs;

import java.io.*;
import java.security.*;
import java.util.*;
import javax.crypto.*;
import javax.security.cert.*; // using java.security.cert.* fails to compile

public class File {
	static final String envSuffix = ".env"; // digital envelope
	static final String encSuffix = ".enc"; // encoded data file
	static final String asdSuffix = ".asd"; // digital signature

	Session userCredentials;

	private SecretKey key;
	private byte[]    asd;
	private byte[]   data;

	// .env - digital envelope ------------------------------------------------
	public SecretKey loadDESKey(String file)
		throws Exception
	{
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, userCredentials.key);

		FileHelper f = new FileHelper(file, cipher);
		String pass = f.br.readLine();
		f.close();

		SecureRandom rng = SecureRandom.getInstance("SHA1PRNG");
		rng.setSeed(pass.getBytes());

		KeyGenerator keygen = KeyGenerator.getInstance("DES");
		keygen.init(56, rng);

		return keygen.generateKey();
	}

	public File(Session userCredentials, String criptedFile)
		throws Exception
	{
		//System.out.println(criptedFile + asdSuffix);
		this.userCredentials = userCredentials;

		this.key = loadDESKey(criptedFile + envSuffix);
		this.asd = FileHelper.readAllBytes(criptedFile + asdSuffix);
		this.data= FileHelper.readAllBytes(criptedFile + encSuffix, Util.DESCipher(key));

		Signature sig = Signature.getInstance("MD5WithRSA");
		sig.initVerify(userCredentials.pub);
		sig.update(data);

		if (sig.verify(asd) == false)
			throw new SecurityException();
	}

	public void save(String fileName)
	{
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			fos.write(data);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public InputStream asInputStream()
	{
		return new ByteArrayInputStream(data);
	}

	public BufferedReader asBufferedReader()
	{
		return new BufferedReader(
				new InputStreamReader(
					new ByteArrayInputStream(data)));
	}

	public static void main(String args[])
		throws Exception
	{
		Session s = new Session(
				"data/Keys/userpriv-pkcs8-pem-des.key", "teste123",
				"data/Keys/usercert-x509.crt", false);

		File f = new File(s, "./data/Files/index");
	}
}
