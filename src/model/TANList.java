package model;

import java.io.*;
import java.util.*;
import java.security.*;
import javax.xml.bind.DatatypeConverter;

public class TANList extends SecureRandom {
	static final long serialVersionUID = 0;

	static final String SET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static final int    LEN = SET.length();
	static final int    N   = 10;

	String        entries[];
	List<Integer> indices;

	// create a TANList from a string from DB
	public TANList(String s)
	{
		super();
		unmarshal(s);
	}

	public TANList()
	{
		this(N);
	}

	public TANList(int n)
	{
		super();
		create(n);
	}

	public int nextIndex()
	{
		if (indices.size() == 0)
			create(N);

		Collections.shuffle(indices);
		System.out.println(indices.get(0));
		return indices.get(0);
	}

	public boolean check(String entry, int i)
	{
		System.out.println(i+":"+entries[i]+" "+entry);
		if (entries[i].equals(entry)) {
			indices.remove(0);
			return true;
		}
		return false;
	}

	public void saveToFile(String fname)
		throws Exception
	{
		FileWriter bw = new FileWriter(fname);
		for (String s: entries) {
			bw.write(s+"\n");
		}

		bw.close();
	}

	// util -------------------------------------------------------------------
	public String marshal()
	{
		String asString = "";
		for (int i=0; i<entries.length; ++i) {
			asString += entries[i]+"|";
		}

		asString += "#";

		for (Integer i: indices)
			asString += i+"|";

		return asString;
	}

	public void unmarshal(String tanList)
	{
		String pair[] = tanList.split("#");
		entries     = pair[0].split("\\|");
		String ss[] = pair[1].split("\\|");

		indices = new ArrayList<Integer>();
		for (String s: ss) {
			indices.add(Integer.parseInt(s));
		}
	}

	private void create(int n)
	{
		entries = new String[n];
		indices = new ArrayList<Integer>();

		for (int i=0; i<n; ++i) {
			entries[i] = makeEntry();
			indices.add(i);
		}
	}

	private String makeEntry()
	{
		StringBuilder entry = new StringBuilder();

		for (int i=0; i<4; ++i)
			entry.append(SET.charAt(next(Integer.SIZE-1) % LEN));

		return entry.toString();
	}

	public static void main(String args[])
		throws Exception
	{
		TANList a = new TANList();
		TANList b = new TANList();
		TANList x = new TANList("I2RR|AMSG|WTPU|0BHQ|TUY4|LI28|OCLE|UI16|F3PG|00MF|#8|6|0|3|9|4|5|1|2|7|");

		System.out.println(b.marshal());
		b.unmarshal(a.marshal()); // b is now equal to a!
		System.out.println(a.marshal());
		System.out.println(b.marshal());

		System.out.println(x.check("I2RR", 0)?"y":"n");
		System.out.println(x.check("I2RR", 1)?"y":"n");

		System.out.println(x.marshal());
		for (int i=0; i<50; ++i) {
			x.nextIndex();
		}
		x.saveToFile("tan.txt");
	}
}
