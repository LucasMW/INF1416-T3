package model;

import java.util.*;
import java.math.*;
import java.security.*;
import javax.xml.bind.DatatypeConverter;

public class Password extends SecureRandom {
	static final long serialVersionUID = 0;

	static final String SET = "0123456789";
	static final int    LEN = SET.length();

	private byte[] md5;
	private String salt;

	Password()
	{
		super();
	}

	Password(String md5hex, String salt)
	{
		this.md5 = DatatypeConverter.parseHexBinary(md5hex);
		this.salt= salt;
	}

	Password(byte[] md5, String salt)
	{
		this.md5 = md5;
		this.salt= salt;
	}

	Password(String s)
	{
		super();
		unmarshal(s);
	}

	public static Password newPassword(String password)
	{
		Password p = new Password();
		p.makeSalt();

		try {
			p.md5 = MessageDigest
				.getInstance("MD5")
				.digest((password + p.salt).getBytes());
		} catch (NoSuchAlgorithmException e) { // will never fail.
			e.printStackTrace();
			System.exit(1);
		}

		return p;
	}

	public boolean verify(String password)
	{
		byte[] pass = null;

		try {
			pass = MessageDigest
				.getInstance("MD5")
				.digest((password + salt).getBytes());
		} catch (NoSuchAlgorithmException e) { // will never fail.
			e.printStackTrace();
			System.exit(1);
		}

		return Arrays.equals(md5, pass);
	}

	byte[] md5()  { return md5; }
	String salt() { return salt; }

	void makeSalt()
	{
		StringBuilder salt = new StringBuilder();

		for (int i=0; i<9; ++i)
			salt.append(SET.charAt(next(Integer.SIZE-1) % LEN));

		this.salt = salt.toString();
	}

	// util -------------------------------------------------------------------
	public String marshal()
	{
		String asString = "";
		return DatatypeConverter.printHexBinary(md5)+"|"+salt;

	}

	public void unmarshal(String s)
	{
		String[] ss = s.split("\\|");
		this.md5 = DatatypeConverter.parseHexBinary(ss[0]);
		this.salt= ss[1];
	}

	public static void main(String args[]) {

		Password a = new Password("hello");
		Password b = new Password("12345");

		System.out.print  (a.verify("hello")?"y":"n");
		System.out.print  (a.verify("12345")?"y":"n");
		System.out.print  (b.verify("hello")?"y":"n");
		System.out.println(b.verify("12345")?"y":"n");

		System.out.println(b.marshal());
		b.unmarshal(a.marshal()); // b is now equal to a!
		System.out.println(a.marshal());
		System.out.println(b.marshal());
	}
}
