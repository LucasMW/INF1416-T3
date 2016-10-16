import java.util.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

import model.*;

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

		window = primaryStage;
		window.setTitle("INF1416");

		do {
			do {
				u = Login.getUser(db);
			} while (!Login.validatePassword(db, u));
		} while (!Login.validateTANEntry(db, u));

		// main menu

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("INF1416:Login (Etapa 1)");
		window.setMinWidth(250);

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);

		Label nameLabel = new Label("Username: " + u.name);
		GridPane.setConstraints(nameLabel, 0, 0);

		Label loginLabel = new Label("Login: " + u.login);
		GridPane.setConstraints(loginLabel, 0, 1);

		Label groupsLabel = new Label("Groups: " + u.groups);
		GridPane.setConstraints(groupsLabel, 0, 2);

		grid.getChildren().addAll(
				nameLabel,
				loginLabel,
				groupsLabel);

		Scene scene = new Scene(grid, 800, 600);
		window.setScene(scene);
		window.showAndWait();

	}

}
