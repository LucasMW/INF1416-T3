import fs.Session;

import java.io.File;
/*from  w  w  w  .  j  ava2s .com*/
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.layout.GridPane;
import javafx.stage.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class SessionForm {

	static String privateKeyFile;
	static Session session;

	public static Session open(String certificate)
	{

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("INF1416:Load Private Key");
		window.setMinWidth (250);

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);

		Label     fileLabel = new Label("Key File:");
		TextField fileInput = new TextField();
		GridPane.setConstraints(fileLabel, 0, 0);
		GridPane.setConstraints(fileInput, 1, 0);

		Label         errorLabel    = new Label("");
		Label         passwordLabel = new Label("Key passowrd:");
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Your password");
		GridPane.setConstraints(errorLabel,    2, 1);
		GridPane.setConstraints(passwordLabel, 0, 1);
		GridPane.setConstraints(passwordField, 1, 1);

		Button btnOk = new Button("ok");
		Button btnCancel = new Button("cancel");
		GridPane.setConstraints(btnOk,     0, 2);
		GridPane.setConstraints(btnCancel, 1, 2);

		btnOk.setOnAction(e -> {
			privateKeyFile = fileInput.getText();
			if (verifySession(privateKeyFile,
						passwordField.getText(),
						certificate)) {
				window.close();
			} else {
				errorLabel.setText("(key with certificate missmatch)");
			}
		});
		passwordField.setOnAction(e->btnOk.getOnAction());

		btnCancel.setOnAction(e -> {
			privateKeyFile = null;
			window.close();
		});

		Button btnLoad = new Button("+");
		GridPane.setConstraints(btnLoad, 2, 0);
		btnLoad.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter =
				new FileChooser.ExtensionFilter("Private Key (*.key)", "*.key");
			fileChooser.getExtensionFilters().add(extFilter);
			File file = fileChooser.showOpenDialog(window);
			if (file != null) {
				fileInput.setText(""+file);
			}
		});

		grid.getChildren().addAll(
				fileLabel, fileInput, btnLoad,
				passwordLabel, passwordField, errorLabel,
				btnOk, btnCancel);

		grid.setAlignment(Pos.CENTER);
		Scene scene = new Scene(grid);
		window.setScene(scene);
		window.showAndWait();

		return session;
	}

	static boolean verifySession(String keyFile, String password, String cert) {
		try {
			session = new Session(keyFile, password, cert, true);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
