import model.*;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class UserForm {

	//Create variable
	static User newUser;

	public static User newUser() {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("INF1416:New user");
		window.setMinWidth(250);
		window.setMinHeight(250);

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);


		Label     nameLabel = new Label("Nome:");
		TextField nameInput = new TextField();
		GridPane.setConstraints(nameLabel, 0, 0);
		GridPane.setConstraints(nameInput, 1, 0);

		Label    loginLabel  = new Label("Login:");
		TextField loginInput = new TextField();
		GridPane.setConstraints(loginLabel,0, 1);
		GridPane.setConstraints(loginInput, 1, 1);

		Label     passLabel = new Label("Senha:");
		TextField passInput = new TextField();
		GridPane.setConstraints(passLabel, 0, 2);
		GridPane.setConstraints(passInput, 1, 2);

		Label passConfLabel     = new Label("Confirma:");
		TextField passConfInput = new TextField();
		GridPane.setConstraints(passConfLabel, 0, 3);
		GridPane.setConstraints(passConfInput, 1, 3);

		Button createButton = new Button("Criar");
		GridPane.setConstraints(createButton, 0, 4);
		createButton.setOnAction(e -> {

			newUser          = new User();
			newUser.name     = nameInput.getText();
			newUser.login    = loginInput.getText();
			newUser.password = Password.newPassword(passInput.getText());
			newUser.tanList  = new TANList();
		
			window.close();
		});

		Button cancelButton = new Button("Cancelar");
		GridPane.setConstraints(cancelButton, 1, 4);
		cancelButton.setOnAction(e -> { window.close(); });

		grid.getChildren().addAll(
				nameLabel, nameInput,
				loginLabel, loginInput,
				passLabel, passInput,
				passConfLabel, passConfInput,
				createButton, cancelButton);

		grid.setAlignment(Pos.CENTER);
		Scene scene = new Scene(grid);
		window.setScene(scene);
		window.showAndWait();

		// TODO: verify confirmation

		return newUser;
	}
}
