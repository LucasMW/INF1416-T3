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
import fs.*;

public class Main extends Application {

	Stage window;

	DB     db;
	User    u;
	Session fsSession;

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

		//do {
		//	do {
		//		u = Login.getUser(db);
		//	} while (!Login.validatePassword(db, u));
		//} while (!Login.validateTANEntry(db, u));
		//u = User.byLogin(db.conn(), "asdf");
		u = User.byLogin(db.conn(), "admin");
		u.totalAccesses++;

		// main menu

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("INF1416:Menu");
		window.setMinWidth(250);

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);

		// TODO: consume and make closeWindow do the window.close()
		window.setOnCloseRequest(e -> {
			//e.consume();
			closeWindow();
		});

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

		Label acessesLabel = new Label("acessos: ");
		GridPane.setConstraints(acessesLabel, 0, 3);

		Label acessesLabel_ = new Label(""+u.totalAccesses);
		GridPane.setConstraints(acessesLabel_, 1, 3);

		Button newUserBtn = null;
		if (u.isAdmin) {
			newUserBtn = new Button("Cadastrar usuario");
			GridPane.setConstraints(newUserBtn, 0, 5);
			newUserBtn.setOnAction(e -> {
				User newUser = UserForm.newUser();
				if (newUser != null) {
					newUser.tanList.saveToFile("./"+newUser.login+"-tan.txt");
					newUser.store(db.conn());
				}
			});
			grid.getChildren().addAll(newUserBtn);
		}

		Label  sessionLabel= new Label("");
		GridPane.setConstraints(sessionLabel, 1, 6);
		Button sessionBtn = new Button("Carregar chave privada");
		GridPane.setConstraints(sessionBtn, 0, 6);
		GridPane.setFillWidth  (sessionBtn, true);
		GridPane.setFillHeight (sessionBtn, true);
		sessionBtn.setOnAction(e -> {
			fsSession = SessionForm.open(u.cert);
			if (fsSession != null) {
				sessionLabel.setText("loaded");
			}
		});


		Button fsBtn = new Button("Consultar arquivos");
		GridPane.setConstraints(fsBtn, 0, 7);
		GridPane.setFillWidth  (fsBtn, true);
		GridPane.setFillHeight (fsBtn, true);

		Button exitBtn = new Button("Sair");
		GridPane.setConstraints(exitBtn, 0, 8);
		GridPane.setFillWidth  (exitBtn, true);
		GridPane.setFillHeight (exitBtn, true);
		exitBtn.setOnAction(e -> {
			closeWindow();
			window.close();
		});

		grid.getChildren().addAll(
				nameLabel, nameLabel_,
				loginLabel, loginLabel_,
				groupsLabel, groupsLabel_,
				acessesLabel, acessesLabel_,
				sessionBtn, sessionLabel,
				fsBtn,
				exitBtn
				);

		Scene scene = new Scene(grid, 800, 600);
		window.setScene(scene);
		window.show();
	}

	private void closeWindow() {
		u.updateTotalAccesses(db.conn());
		System.out.println("done");
		//window.close();
	}
}
