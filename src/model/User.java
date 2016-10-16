package model;
import java.util.*;
import java.sql.*;
import java.time.*;

public class User {


	public int      id;
	public String   login;
	public String   name;
	public String   description;

	public String   cert;
	public String   privKeyPath;

	public Password password;
	public TANList  tanList;

	public boolean  isAdmin;
	public int      totalAccesses;
	public LocalDateTime
	                blockedUntil;

	public User()
	{
	}

	public static User byLogin(Connection conn, String login)
	{
		try {
			String query = "select * from users where login="+"'"+login+"';\n";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			if (rs.next()) {
				User u = new User();
				u.id          = rs.getInt     ("id");
				u.login       = rs.getString  ("login");
				u.name        = rs.getString  ("name");
				u.description = rs.getString  ("description");

				u.cert        = rs.getString  ("cert");
				//u.privKeyPath = rs.getString  ("privKeyPath");

				u.tanList  = new TANList (rs.getString ("tanList"));
				u.password = new Password(rs.getString ("password"));

				u.totalAccesses = rs.getInt   ("totalAccesses");
				u.isAdmin       = rs.getInt   ("isAdmin") != 0 ? true : false;

				String ts       = rs.getString("blockedUntil");
				if (ts != null) u.blockedUntil = LocalDateTime.parse(ts);
				else            u.blockedUntil = LocalDateTime.now(Clock.systemUTC());

				rs.close();
				return u;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean store(Connection conn)
		throws Exception
	{
		if (User.byLogin(conn, login) != null)
			return false;

		String query =
			"insert into users "+
			"(login, name, description, password, tanList, isAdmin) "+
			"values "+
			String.format(
					"('%s', '%s', '%s', '%s', '%s', %d);",
					login,
					name,
					description,
					password.marshal(),
					tanList.marshal(),
					isAdmin?1:0);

		try {
		  Statement stmt = conn.createStatement();
		  stmt.executeUpdate(query);
		  return true;
		} catch (Exception e) {
			System.out.println("ERROR: sql insert");
			return false;
		}
	}

	public void updateTanList(Connection conn)
	{
		String query =
			"update users " +
			String.format(
					"set tanList = '%s' where id=%d",
					tanList.marshal(),
					id);
	}

	public void updateCert(Connection conn)
	{
		String query =
			"update users " +
			String.format(
					"set cert = '%s' where id=%d",
					cert,
					id);
	}

	public void block(Connection conn)
	{
		String query =
			"update users " +
			String.format(
					"set blockedUntil = %s where id=%d;",
					"strftime('%Y-%m-%dT%H:%M:%S', 'now', '+120 seconds')",
					id);

		try {
		  Statement stmt = conn.createStatement();
		  stmt.executeUpdate(query);
		} catch (Exception e) {
			System.out.println("ERROR: sql update blocked");
			e.printStackTrace();
		}
	}

	public boolean isBlocked()
	{
		return blockedUntil.isAfter(LocalDateTime.now(Clock.systemUTC()));
	}

	public static void main(String args[])
		throws Exception
	{
		if (args.length < 1) {
			System.out.printf("ERROR: requires 1 parameter:\n"+
					"\t- login\n");
			System.exit(1);
		}
		String login = args[0];

		Runtime.getRuntime().exec("sqlite3 main.db -init setup.sql");

		DB db = new DB();
		db.connect("main.db");
		User admin = User.byLogin(db.conn(), login);
		if (admin == null) {
			System.out.println("No user with login '" + login + "'");
			System.exit(1);
		}

		System.out.println("login="+admin.login);
		System.out.println("name ="+admin.name);
		System.out.println("desc ="+admin.description);
		System.out.println(admin.password.verify("BACADA")?"y":"n");

		User u = new User();
		//u.id;
		u.login         = "asdf";
		u.name          = "asdf";
		u.description   = "asdf";

		u.cert          = null;

		u.password      = Password.newPassword("BADACA");
		u.tanList       = new TANList();

		u.isAdmin       = false;
		u.totalAccesses = 0;
		//u.blockedUntil  = null;

		System.out.println("login="+u.login);
		System.out.println("name ="+u.name);
		System.out.println("desc ="+u.description);
		System.out.println(u.password.verify("BADACA")?"y":"n");
		System.out.println(u.store(db.conn())?
				"insert ok":
				"ERROR: insert failed, login already in use");

		System.out.println("new tanList:"+u.tanList.marshal());
		u.tanList       = new TANList();
		u.updateTanList(db.conn());
	}
}
