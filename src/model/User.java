package model;
import java.sql.*;

public class User {


	public int      id;
	public String   login;
	public String   name;
	public String   description;

	public String   certPath;
	public String   privKeyPath;

	public Password password;
	public TANList  tanList;

	public boolean  isAdmin;
	public int      totalAccesses;
	public Date     blockEnds;

	public User()
	{
	}

	public static User byLogin(Connection conn, String login)
		throws Exception
	{
		String query = "select * from users where login="+"'"+login+"';\n";
		Statement st = conn.createStatement();
		try (ResultSet rs = st.executeQuery(query)) {
			if (rs.next()) {
				User u = new User();
				u.id          = rs.getInt     ("id");
				u.login       = rs.getString  ("login");
				u.name        = rs.getString  ("name");
				u.description = rs.getString  ("description");

				u.certPath    = rs.getString  ("certPath");
				u.privKeyPath = rs.getString  ("privKeyPath");

				u.tanList  = new TANList (rs.getString ("tanList"));
				u.password = new Password(rs.getString ("password"));

				u.totalAccesses = rs.getInt     ("totalAccesses");
				u.isAdmin       = rs.getInt     ("isAdmin") != 0 ? true : false;

				// YYYY-MM-DDTHH:MM:SS.SSS 
				//u.blockedUntil  = null;
				return u;
			}
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

		u.certPath      = "asdf";
		u.privKeyPath   = "asdf";

		u.password      = Password.newPassword("BADACA");
		u.tanList       = new TANList();

		u.isAdmin       = false;
		u.totalAccesses = 0;
		u.blockEnds     = null;

		System.out.println("login="+u.login);
		System.out.println("name ="+u.name);
		System.out.println("desc ="+u.description);
		System.out.println(u.password.verify("BADACA")?"y":"n");

		System.out.println(u.tanList.marshal());
		u.tanList       = new TANList();
		u.updateTanList(db.conn());

		System.out.println(u.store(db.conn())?"insert":"ERROR: login already in use");

	}
}
