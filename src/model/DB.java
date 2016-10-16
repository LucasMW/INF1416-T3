package model;
import java.sql.*;

public class DB {

	private static DB db;
	Connection connection=null;

	public void connect(String file)
	{
		try { // this is a fatal error!
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:"+file);
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
		return this.connection;
	}

	//public void time(int seconds)
	//{
	//	String Query = "select strftime('%Y-%M-%d %H:%M:%S', 'now', '+"
	//		+seconds+" second');";
	//}

	public static void main(String args[]) {
		DB db = new DB();
		db.connect("main.db");
	}
}
