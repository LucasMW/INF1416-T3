
import java.io.*;
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

import java.security.spec.InvalidKeySpecException;
import javax.security.cert.*; // using java.security.cert.* fails to compile

import fs.Session;
import model.*;

public class SessionForm {

	static String privateKeyFile;
	static Session session;

	public static Session open(String certificate,User u,DB db)
	{

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("INF1416:Load Private Key");
		db.register(7001,u); // load private key presented
		window.setMinWidth (250);
		window.setMinHeight(120);

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
			int r = verifySession(privateKeyFile,
						passwordField.getText(),
						certificate);
			System.out.println(r);
			switch (r) {
				case 0:
				case 3:
					window.close();
					break;
				case 1:
				case 5:
					db.register(7004,u); //missmatch certificate/key
					errorLabel.setText("(certificate/key missmatch)");
					break;
				case 2:
					db.register(7003,u);  //password invalid
					errorLabel.setText("(invalid password)");
					break;
				case 4:
					db.register(7002,u);  //invalid path
					errorLabel.setText("(no such file)");
					break;
				default:
					errorLabel.setText("(problems)");
					break;
			}
		});
		passwordField.setOnAction(e->btnOk.getOnAction());

		btnCancel.setOnAction(e -> {
			privateKeyFile = null;
			window.close();
			db.register(7006,u); //Voltar
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
		db.register(7005,u); //verify ok
		return session;

	}

	static int verifySession(String keyFile, String password, String cert) {
		try {
			session = new Session(keyFile, password, cert, true);
			return 0;
		} catch (javax.crypto.BadPaddingException e) {
			System.out.println("BadPaddingException");
			return 1;
		} catch (InvalidKeySpecException e) {
			System.out.println("InvalidKeySpecException");
			return 2;
		} catch (CertificateException e) {
			System.out.println("CertificateException");
			return 3;
		} catch (IOException e) {
			System.out.println("IOException");
			return 4;
		} catch (java.lang.SecurityException e) {
			System.out.println("SecurityException");
			return 5;
		}
	}
}
