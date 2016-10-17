

import model.DB;

public class LogView
{
    static DB db;

	public static void main(String[] args)
	{
    db     = new DB();
    db.connect("main.db");
    db.viewRegistry();
	}

}
