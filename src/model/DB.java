package model;
import java.sql.*;

public class DB {

	Connection conn=null;

	public void connect(String file)
	{
		try { // this is a fatal error!
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:"+file);
		} catch (Exception e) {
			System.out.println("fatal error: db failure.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void install()
		throws Exception
	{
		System.out.println("Installing");
		Runtime.getRuntime().exec("sqlite3 main.db -init setup.sql");
	}

	public Connection conn()
	{
		return this.conn;
	}

	public int count(String table)
	{
		try {
			String query = String.format("select count(*) as total from %s;", table);

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			int total = rs.getInt("total");

			rs.close();
			return total;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	public static void main(String args[]) {
		DB db = new DB();
		db.connect("main.db");
		System.out.println(db.count("users"));
	}
}
