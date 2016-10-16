import model.*;
import fs.*;

import java.util.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

	Stage window;

	DB    db;
	User  u;

	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		db     = new DB();
		db.connect("main.db");

		//DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//Date date = new Date();
		//System.out.println(dateFormat.format(date));

		window = primaryStage;
		window.setTitle("INF1416");

		u = Login.getUser(db);
		Login.validatePassword(u);

	}

}
