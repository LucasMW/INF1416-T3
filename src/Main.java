import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Main extends Application {

	Stage window;

	DB     db;
	User    u;
	Session fsSession;
	Dir     root;

	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		db     = new DB();
		db.connect("main.db");

		//db.viewRegistry(); use this to viewRegistry
		db.register(1001); //system started
		window = primaryStage;
		window.setTitle("INF1416");

		do {
			do {
				u = Login.getUser(db);
			} while (!Login.validatePassword(db, u));
		} while (!Login.validateTANEntry(db, u));
		//u = User.byLogin(db.conn(), "asdf");
		//u = User.byLogin(db.conn(), "admin");
		//u = User.byLogin(db.conn(), "user03");
		u.totalAccesses++;

		// main menu

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("INF1416:Menu");
		window.setMinWidth(400);
		window.setMinHeight(300);

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);

		// TODO: consume and make closeWindow do the window.close()
		window.setOnCloseRequest(e -> {
			e.consume();
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

		Label groupsLabel_ = new Label(u.groups.toString());
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
				db.register(5002,u); //cadastrar usuario
				User newUser = UserForm.createUser(db,u);
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
			db.register(5003,u); //load private key
			fsSession = SessionForm.open(u.cert,u,db);
			if (fsSession != null) {
				sessionLabel.setText("loaded");
			}
		});


		ListView<Dir.Entry> filesList = new ListView<Dir.Entry>();
		GridPane.setConstraints(filesList, 1, 9);
		filesList.setOnMouseClicked(e -> {

			Dir.Entry fileEntry = filesList.getSelectionModel().getSelectedItem();
			String fileName     = fileEntry.realName;
			System.out.println(fileName);
			String newFileName= root.path + fileName;
			Dir.Entry entry = root.list().get(fileName);
			db.register(8008,u,fileName); //fileName selected
			try {
				(new File (fsSession, root.path + entry.cryptedName))
					.save(newFileName);
				System.out.println("created on " + newFileName);
				db.register(8009,u,fileName); //decripted
			} catch (Exception se) {
				db.register(8012,u,fileName);  //verify false
				System.out.println("corrupted file");
				db.register(8011,u,fileName); //not decripted
			}
		});

		Button fsBtn = new Button("Consultar arquivos");
		GridPane.setConstraints(fsBtn, 0, 7);

		TextField fsDirInput = new TextField();
		GridPane.setConstraints(fsDirInput, 1, 7);
		fsDirInput.setText("./data/Files/");

		fsBtn.setOnAction(e -> {
			db.register(5004,u); //browse files
			db.register(8001,u); //browsing files screen presented
			try {
				root = new Dir(fsSession, fsDirInput.getText());
				db.register(8003,u); //filesList pressed
				filesList.setItems(
						FXCollections.observableArrayList(root.asList()));
				db.register(8007,u); //filesList presented

			} catch (Exception ex) {
				System.out.println("no such directory");
				db.register(8006,u); //invalid path
				filesList.setItems(null);
			}
			db.register(8002,u); //returning to main menu
		});

		Button exitBtn = new Button("Sair");
		GridPane.setConstraints(exitBtn, 0, 8);
		GridPane.setFillWidth  (exitBtn, true);
		GridPane.setFillHeight (exitBtn, true);
		exitBtn.setOnAction(e -> {
			db.register(5005,u); //exit
			db.register(9001,u); //exit screen pressented

			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Tela de saída");
			alert.setHeaderText("Deseja mesmo sair?");
			alert.setContentText("Sair para sair, voltar para voltar");

			ButtonType buttonTypeConfirm = new ButtonType("Sair");
			ButtonType buttonTypeCancel = new ButtonType("Voltar");

			alert.getButtonTypes().setAll(buttonTypeConfirm, buttonTypeCancel);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeConfirm){
				closeWindow();
				db.register(9002,u); //exit pressed
				window.close();
			} else if (result.get() == buttonTypeCancel) {
			    db.register(9003,u); //exit pressed
			}

		});
		//não há onde colocar o db.register(9003) para nossa implementação

		grid.getChildren().addAll(
				nameLabel, nameLabel_,
				loginLabel, loginLabel_,
				groupsLabel, groupsLabel_,
				acessesLabel, acessesLabel_,
				sessionBtn, sessionLabel,
				fsBtn, fsDirInput,
				exitBtn,
				filesList
				);

		Scene scene = new Scene(grid, 800, 600);
		window.setScene(scene);
		window.show();
	}

	private void closeWindow() {
		if(u!=null) {
			u.updateTotalAccesses(db.conn());
		}
		System.out.println("done");
		db.register(1002); //system closed
		//window.close();
	}
}
