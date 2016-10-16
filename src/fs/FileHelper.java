package fs;

import java.io.*;
import javax.crypto.*;

class FileHelper {
	private FileInputStream   fis = null;
	private CipherInputStream cis = null;
	private InputStreamReader isr = null;
	public  BufferedReader     br = null;

	public FileHelper(String file, Cipher cipher)
		throws Exception
	{
		fis = new FileInputStream(file);
		cis = new CipherInputStream(fis, cipher);
		isr = new InputStreamReader(cis);
		br  = new BufferedReader(isr);
	}

	public static byte[] readAllBytes(String file, Cipher cipher)
		throws Exception
	{
		FileInputStream      fis = new FileInputStream(file);
		CipherInputStream    cis = new CipherInputStream(fis, cipher);
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		int n;
		byte[] data = new byte[4096];

		while ((n = cis.read(data, 0, data.length)) != -1) {
			os.write(data, 0, n);
		}

		fis.close();
		cis.close();
		return os.toByteArray();
	}

	public static byte[] readAllBytes(String file)
		throws Exception
	{
		FileInputStream      fis = new FileInputStream(file);
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		int n;
		byte[] data = new byte[4096];

		while ((n = fis.read(data, 0, data.length)) != -1) {
			os.write(data, 0, n);
		}

		fis.close();
		return os.toByteArray();
	}

	//public void write(String file)
	//	throws Exception
	//{
	//	FileInputStream      fis = new FileInputStream(file);
	//	ByteArrayOutputStream os = new ByteArrayOutputStream();

	//	int n;
	//	byte[] data = new byte[4096];

	//	while ((n = fis.read(data, 0, data.length)) != -1) {
	//		os.write(data, 0, n);
	//	}

	//	fis.close();
	//	return os.toByteArray();
	//}

	public void close()
		throws Exception
	{
		fis.close();
		cis.close();
		isr.close();
		br.close ();
	}
}
