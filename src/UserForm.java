import java.io.*;
import javafx.beans.value.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

import model.*;

public class UserForm {

	//Create variable
	static User newUser;
	static boolean taken;
	static boolean confirm;

	public static User createUser(DB db) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("INF1416:New user");
		window.setMinWidth(320);
		window.setMinHeight(250);

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);


		Label     nameLabel = new Label("Nome:");
		TextField nameInput = new TextField();
		GridPane.setConstraints(nameLabel, 0, 0);
		GridPane.setConstraints(nameInput, 1, 0);

		Label    loginLabel_ = new Label("");
		GridPane.setConstraints(loginLabel_, 2, 1);

		Label    loginLabel  = new Label("Login:");
		TextField loginInput = new TextField();
		GridPane.setConstraints(loginLabel,0, 1);
		GridPane.setConstraints(loginInput, 1, 1);

		Label     descLabel = new Label("Descrição:");
		TextField descInput = new TextField();
		GridPane.setConstraints(descLabel,0, 2);
		GridPane.setConstraints(descInput, 1, 2);

		loginInput.focusedProperty().addListener(
				(ObservableValue<? extends Boolean> observable,
				 Boolean oldValue,
				 Boolean newValue) -> {
					taken = User.byLoginExists(
							db.conn(),
							loginInput.getText());

					if (!newValue) {
						loginLabel_.setText(taken?"(taken)": "");
					}
				});

		Label     passLabel = new Label("Senha:");
		TextField passInput = new TextField();
		GridPane.setConstraints(passLabel, 0, 3);
		GridPane.setConstraints(passInput, 1, 3);

		Label passConfLabel     = new Label("Confirma:");
		Label passConfLabel_    = new Label("");
		TextField passConfInput = new TextField();
		GridPane.setConstraints(passConfLabel,  0, 4);
		GridPane.setConstraints(passConfInput,  1, 4);
		GridPane.setConstraints(passConfLabel_, 2, 4);

		Label certLabel     = new Label("Certificado:");
		TextField certInput = new TextField();
		GridPane.setConstraints(certLabel, 0, 5);
		GridPane.setConstraints(certInput, 1, 5);

		Button certBtn = new Button("+");
		GridPane.setConstraints(certBtn, 2, 5);
		certBtn.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter =
				new FileChooser.ExtensionFilter("Certificate (*.crt)", "*.crt");
			fileChooser.getExtensionFilters().add(extFilter);
			File file = fileChooser.showOpenDialog(window);
			if (file != null) {
				certInput.setText(""+file);
			}
		});

		Label createLabel_  = new Label("");
		GridPane.setConstraints(createLabel_, 2, 5);

		Button createButton = new Button("Cadastrar");
		Button cancelButton = new Button("Voltar");
		GridPane.setConstraints(createButton, 0, 6);
		GridPane.setConstraints(cancelButton, 1, 6);

		createButton.setOnAction(e -> {
			if (	nameInput    .getText().trim().equals("") ||
					descLabel    .getText().trim().equals("") ||
					passInput    .getText().trim().equals("") ||
					passConfInput.getText().trim().equals("") ||
					loginInput   .getText().trim().equals("")) { // invalid
				createLabel_.setText("(x)");
			} else {

				String pass      = passInput.getText();
				String passConf  = passConfInput.getText();

				if (pass.equals(passConf)) {
					newUser             = new User();
					newUser.name        = nameInput.getText();
					newUser.login       = loginInput.getText();
					newUser.description = descInput.getText();
					newUser.password    = Password.newPassword(pass);
					newUser.tanList     = new TANList();

					if (confirmForm(newUser)) {
						window.close();

					}
				} else {
					passConfLabel_.setText("(missmatch)");
				}
			}
		});

		cancelButton.setOnAction(e -> {
			newUser = null;
			window.close();
		});

		grid.getChildren().addAll(
				nameLabel, nameInput,
				loginLabel, loginInput, loginLabel_,
				descLabel, descInput,
				passLabel, passInput,
				passConfLabel, passConfInput, passConfLabel_,
				certLabel, certInput, certBtn,
				createButton, cancelButton, createLabel_
				);

		grid.setAlignment(Pos.CENTER);
		Scene scene = new Scene(grid);
		window.setScene(scene);
		window.showAndWait();

		// TODO: verify confirmation

		return newUser;
	}

	public static boolean confirmForm(User u) {
		confirm = false;

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("INF1416:New user");
		window.setMinWidth(300);
		window.setMinHeight(250);

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);

		Label     nameLabel = new Label("Nome:");
		GridPane.setConstraints(nameLabel, 0, 0);

		Label     nameLabel_= new Label(u.name);
		GridPane.setConstraints(nameLabel_, 1, 0);

		Label     loginLabel = new Label("Login:");
		GridPane.setConstraints(loginLabel, 0, 1);

		Label     loginLabel_= new Label(u.login);
		GridPane.setConstraints(loginLabel_, 1, 1);

		Label     descLabel = new Label("Descrição:");
		GridPane.setConstraints(descLabel, 0, 2);

		Label     descLabel_= new Label(u.description);
		GridPane.setConstraints(descLabel_, 1, 2);

		Button confirmButton = new Button("Confirmar");
		Button cancelButton  = new Button("Cancelar");
		GridPane.setConstraints(confirmButton, 0, 5);
		GridPane.setConstraints(cancelButton,  1, 5);

		cancelButton.setOnAction(e -> { window.close(); });

		grid.getChildren().addAll(
				nameLabel, nameLabel_,
				loginLabel, loginLabel_,
				descLabel, descLabel_,
				confirmButton, cancelButton
				);

		grid.setAlignment(Pos.CENTER);
		Scene scene = new Scene(grid);
		window.setScene(scene);
		window.showAndWait();

		return confirm;
	}

}
