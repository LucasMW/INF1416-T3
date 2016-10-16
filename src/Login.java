import java.util.*;
import model.*;

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

public class Login {

	static User u;
	static List<String[]> clicks;

	public static User getUser(DB db) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("INF1416:Login (Etapa 1)");
		window.setMinWidth(250);

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);

		Label nameLabel = new Label("Username:");
		GridPane.setConstraints(nameLabel, 0, 0);

		Label errorLabel = new Label("");
		GridPane.setConstraints(errorLabel, 2, 0);

		TextField loginInput = new TextField();
		GridPane.setConstraints(loginInput, 1, 0);
		loginInput.setPromptText("username");
		loginInput.setOnAction(e -> {
			if ((u = User.byLogin(db.conn(), loginInput.getText())) != null &&
					!u.isBlocked()) {
				window.close();
			} else {
				errorLabel.setText("(no such user)");
			}
		});

		Button loginButton = new Button("Log in");
		GridPane.setConstraints(loginButton, 1, 1);
		loginButton.setOnAction(loginInput.getOnAction());

		grid.getChildren().addAll(
				nameLabel,
				loginInput, loginButton, errorLabel);
		Scene scene = new Scene(grid, 800, 600);
		window.setScene(scene);
		window.showAndWait();

		return u;
	}

	static boolean validPassword = false;

	public static boolean validatePassword(User u) {
		clicks = new ArrayList<String[]>();

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("INF1416:Password (Etapa 2)");
		window.setMinWidth(250);

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);

		Label nLabel = new Label("");
		GridPane.setConstraints(nLabel, 0, 3);
		grid.getChildren().addAll(nLabel);

		List<String[]> btnLabels = buildLabels(grid);

		for (int i=0; i<btnLabels.size(); ++i) {
			String[] ss = btnLabels.get(i);
			Button btn = new Button(ss[0]+" "+ss[1]+" "+ss[2]);
			GridPane.setConstraints(btn, i/2, i%2);
			btn.setOnAction(e -> {
				if (clicks.size() == 0) {
					nLabel.setText("");
				}
				if (clicks.size() < 3) {
					clicks.add(btn.getText().split(" "));
					nLabel.setText(nLabel.getText()+"*");
				}

				if (clicks.size() > 2) {
					if (verifyCombinations(clicks, "", 0)) {
						validPassword = true;
						window.close();
					} else {
						nLabel.setText("(incorrect)");
						clicks.clear();
					}
				}
			});
			grid.getChildren().addAll(btn);
		}

		Scene scene = new Scene(grid, 800, 600);
		window.setScene(scene);
		window.showAndWait();

		return validPassword;
	}

	static final String[] phonems = {
		"BA", "BE", "BO",
		"CA", "CE", "CO",
		"DA", "DE", "DO",
		"FA", "FE", "FO",
		"GA", "GE", "GO"
	};

	private static List<String[]> buildLabels(GridPane grid) {
		List<String[]> btns      = new ArrayList<String[]>();
		List< String > phonList = new ArrayList<String>(Arrays.asList(phonems));
		Collections.shuffle(phonList);

		while (!phonList.isEmpty()) {
			String[] label = new String[3];
			for (int i=0; i<3; ++i) {
				label[i] = phonList.remove(0);
			}
			btns.add(label);
		}

		//for (int i=0; i<btns.size(); ++i) {
		//	String[] ss = btns.get(i);

		//	System.out.println(ss[0]+" "+ss[1]+" "+ss[2]);

		//	Button btn = new Button(ss[0]+" "+ss[1]+" "+ss[2]);
		//	GridPane.setConstraints(btn, i/2, i%2);
		//	btn.setOnAction(e -> {
		//		if (clicks.size() < 2) {
		//			System.out.println(clicks.size());
		//			clicks.add(btn.getText().split(" "));
		//			return;
		//		}
		//		if (clicks.size() < 3) {
		//			System.out.println(clicks.size());
		//			clicks.add(btn.getText().split(" "));
		//		}

		//		if (verifyCombinations(clicks, "", 0)) {
		//			// correct
		//			System.out.println("correct");
		//		} else {
		//			// incorrect
		//			System.out.println("incorrect");
		//		}
		//	});

		//	grid.getChildren().addAll(btn);
		//}

		return btns;
	}

	private static boolean verifyCombinations(List<String[]> btns, String s, int depth)
	{
		if (btns.size() == depth)
			return u.password.verify(s);

		for (int i=0; i<btns.get(depth).length; ++i) {
			boolean res = verifyCombinations(btns,
					s + btns.get(depth)[i], depth+1);
			if (res) return true;
		}
		return false;
	}
}
