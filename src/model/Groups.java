package model;

import java.util.*;
import java.sql.*;
import java.time.*;

public class Groups {

	public Map<String, Group> groups;

	public static class Group {
		public int id;
		public String name;
		public String description;

		public Group(String name, String description)
		{
			this.id          = -1;
			this.name        = name;
			this.description = description;
		}

		public Group(ResultSet rs)
			throws Exception
		{
			id          = rs.getInt   ("id");
			name        = rs.getString("name");
			description = rs.getString("description");
		}
	}

	public Groups()
	{
		groups = new HashMap<String, Group>();
	}

	public void put(Group g)
	{
		groups.put(g.name, g);
	}

	public Group get(String name)
	{
		return groups.get(name);
	}

	public static Groups fromUserLogin(Connection conn, String login)
	{
		Groups gs = null;

		try {
			gs = new Groups();
			String query = String.format(
					"select * from groups\n"+
					"	join ingroup on groups.id = ingroup.group_id\n"+
					"	join users   on users.id  = ingroup.user_id\n"+
					"where users.id=ingroup.user_id and users.login='%s';",
					login);

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			while (rs.next())
				gs.put(new Group(rs));

			rs.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return gs;
	}

	public Set<String> list() {
		return groups.keySet();
	}

	public String toString() {
		String all = "[ ";
		for (String s: groups.keySet()) {
			all += s + " ";
		}
		all += "]";
		return all;
	}

	public static Groups getAll(Connection conn)
	{
		Groups gs = null;

		try {
			gs = new Groups();

			String query="select * from groups;";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			while (rs.next())
				gs.put(new Group(rs));

			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return gs;
	}

	public static void main(String args[]) {
		DB db = new DB();
		db.connect("main.db");

		Groups gs = Groups.fromUserLogin(db.conn(), "admin");

		for (Map.Entry<String, Group> e: gs.groups.entrySet()) {
			String key   = e.getKey();
			Group  group = e.getValue();

			System.out.println(key);
		}
	}
}
