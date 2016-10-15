package model;

import java.util.*;
import java.math.*;
import java.security.*;
import javax.xml.bind.DatatypeConverter;

class Password extends SecureRandom {
	static final long serialVersionUID = 0;

	static final String SET = "0123456789";
	static final int    LEN = SET.length();

	private byte[] md5;
	private String salt;

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

	Password(String password)
	{
		super();
		makeSalt();

		try {
			md5 = MessageDigest
				.getInstance("MD5")
				.digest((password + salt).getBytes());
		} catch (NoSuchAlgorithmException e) { // will never fail.
			e.printStackTrace();
			System.exit(1);
		}
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
		//String set = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		StringBuilder salt = new StringBuilder();

		for (int i=0; i<9; ++i)
			salt.append(SET.charAt(next(Integer.SIZE-1) % LEN));

		this.salt = salt.toString();
	}

	public static void main(String args[]) {

		Password p = new Password("hello");
		System.out.print  (p.verify("hello")?"y":"n");
		System.out.print  (p.verify("12345")?"y":"n");
		p.salt = "x";
		System.out.print  (p.verify("hello")?"y":"n");
		System.out.println(p.verify("12345")?"y":"n");
	}
}
