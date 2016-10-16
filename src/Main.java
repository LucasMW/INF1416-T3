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

		u = User.byLogin(db.conn(), "admin");
		//do {
		//	do {
		//		u = Login.getUser(db);
		//	} while (!Login.validatePassword(db, u));
		//} while (!Login.validateTANEntry(db, u));

		// main menu

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("INF1416:Menu");
		window.setMinWidth(250);

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);

		Label nameLabel = new Label("Name: ");
		GridPane.setConstraints(nameLabel, 0, 0);

		Label nameLabel_= new Label(u.name);
		GridPane.setConstraints(nameLabel_, 1, 0);

		Label loginLabel = new Label("Login: ");
		GridPane.setConstraints(loginLabel, 0, 1);

		Label loginLabel_= new Label(u.login);
		GridPane.setConstraints(loginLabel_, 1, 1);

		Label groupsLabel = new Label("Groups: ");
		GridPane.setConstraints(groupsLabel, 0, 2);

		Label groupsLabel_ = new Label(u.groupsToString());
		GridPane.setConstraints(groupsLabel_, 1, 2);

		Button newUserBtn = new Button("Cadastrar usuario");
		GridPane.setConstraints(newUserBtn, 0, 3);
		newUserBtn.setOnAction(e -> {
			User newUser = UserForm.display();
			System.out.println(newUser);
		});

		Button loadKeyBtn = new Button("Carregar chave privada");
		GridPane.setConstraints(loadKeyBtn, 0, 4);
		GridPane.setFillWidth  (loadKeyBtn, true);
		GridPane.setFillHeight (loadKeyBtn, true);


		Button fsBtn = new Button("Consultar arquivos");
		GridPane.setConstraints(fsBtn, 0, 5);
		GridPane.setFillWidth  (fsBtn, true);
		GridPane.setFillHeight (fsBtn, true);

		Button exitBtn = new Button("Sair");
		GridPane.setConstraints(exitBtn, 0, 6);
		GridPane.setFillWidth  (exitBtn, true);
		GridPane.setFillHeight (exitBtn, true);
		exitBtn.setOnAction(e -> {
			window.close();
		});

		grid.getChildren().addAll(
				nameLabel, nameLabel_,
				loginLabel, loginLabel_,
				groupsLabel, groupsLabel_,
				newUserBtn, loadKeyBtn, fsBtn, exitBtn
				);

		Scene scene = new Scene(grid, 800, 600);
		window.setScene(scene);
		window.showAndWait();

	}
}
