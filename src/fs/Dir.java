package fs;

import java.io.*;
import java.security.*;
import java.util.*;

class Dir {
	Session s;
	Map<String, Entry> files;

	class Entry {
		public String cryptedname;
		public String realName;
		public String desPassword;
		public String group;

		public Entry(String s) {
			String ss[] = s.split(" ");
			cryptedname = ss[0];
			realName    = ss[1];
			desPassword = ss[2];
			group       = ss[3];
		}
	}

	public Dir(Session s, String path)
		throws Exception
	{
		files = new HashMap<String, Entry>();

		// TODO: decrypt index and load into Map
		BufferedReader br = new File(s, path + "index").asBufferedReader();

		String line;
		while ((line=br.readLine()) != null) {
			Entry e = new Entry(line);
			files.put(e.realName, e);
		}

		br.close();
	}

	public Map<String, Dir.Entry> list()
	{
		return files;
	}

	public static void main(String args[])
		throws Exception
	{
		Session s = new Session(
				"data/Keys/userpriv-pkcs8-pem-des.key", "teste123",
				"data/Keys/usercert-x509.crt");

		Dir root = new Dir(s, "./data/Files/");

		for (Map.Entry<String, Dir.Entry> pair: root.list().entrySet()) {
			System.out.printf("%s\t%s\n", pair.getKey(), pair.getValue().group);
		}
	}
}
