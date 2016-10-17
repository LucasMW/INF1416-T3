package model;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
	public void register(int msg_id,User u,String filename)
	{
		try {
			String query = String.format("insert into registers (user_id,msg_id,time,file_name) values (%d,%d,datetime('now'),%s);",
			u.id,msg_id,filename);

			Statement st = conn.createStatement();
			st.executeUpdate(query);
		} catch (Exception e) {
			System.out.println("could not make registry!");
			//e.printStackTrace();
		}
	}
	public void register(int msg_id,User u)
	{
		try {
			String query = String.format("insert into registers (user_id,msg_id,time) values (%d,%d,datetime('now'));",
			u.id,msg_id);

			Statement st = conn.createStatement();
			st.executeUpdate(query);
		} catch (Exception e) {
			System.out.println("could not make registry!");
			//e.printStackTrace();
		}
	}
	//simple messages
	public void register(int msg_id)
	{
		try {
			String query = String.format("insert into registers (msg_id,time) values (%d,datetime('now'));",msg_id);
			Statement st = conn.createStatement();
			st.executeUpdate(query);
		} catch (Exception e) {
			System.out.println("could not make registry");
		}
	}

	public String[] UserLoginNames() {
		String query = String.format("select id,login from USERS");
		String[] array = new String[count("USERS")];
		try {

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			int usrId =  rs.getInt("id");
			array[usrId] = rs.getString("login");
			return array;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	public void viewRegistry()
	{
		String[] logins = UserLoginNames();
		try {
			String query = String.format("select * from messages join registers on messages.id = registers.msg_id ORDER BY time;");

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while(rs.next())
			{
				String message = rs.getString(2);
				int msg_type = rs.getInt(1);
				int regid = rs.getInt(3);
				int userId = rs.getInt(4);
				String date = rs.getString("time");
				Pattern pattern = Pattern.compile("([%][s])"); //case insensitive, use [g] for only lower
				Matcher matcher = pattern.matcher(message);
				int count = 0;
				while (matcher.find()) {
					count++;
				}
				System.out.println(count);
				if(count == 1)
				{
					message = String.format(message,logins[userId]);
				}
				else if(count == 2)
				{
					String filename = rs.getString("file_name");
					message = String.format(message,logins[userId]);
				}
				System.out.println(String.format("%d: %s",regid,message));
		 	}
			rs.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String args[]) {
		DB db = new DB();
		db.connect("main.db");
		System.out.println(db.count("users"));
	}
}
