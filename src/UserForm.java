import java.io.*;
import java.util.*;

import java.security.*;
import java.util.*;
import javafx.beans.value.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javax.crypto.*;
import javax.security.cert.*; // using java.security.cert.* fails to compile

import model.*;
import fs.*;

public class UserForm {

	//Create variable
	static User newUser;
	static boolean taken;
	static boolean confirm;
	static boolean certIsValid;
	static private X509Certificate cert;

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
			java.io.File file = fileChooser.showOpenDialog(window);
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
				if(!passwordIsAcceptable(pass))
				{
					 System.out.println("PASSWORD NOT ACCEPTABLE");
					 passConfLabel_.setText("Senha mal formada");
				}
				else if (pass.equals(passConf)) {
					newUser             = new User();
					newUser.name        = nameInput.getText();
					newUser.login       = loginInput.getText();
					newUser.description = descInput.getText();
					newUser.password    = Password.newPassword(pass);
					newUser.tanList     = new TANList();

					try {
						String certFile = certInput.getText();
						cert = loadCertificate(certFile);
						//System.out.println(cert);

						if (confirmForm(newUser, cert)) {
							newUser.cert = new String(
									FileHelper.readAllBytes(certFile), "UTF-8");
							window.close();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
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

	public static boolean confirmForm(User u, X509Certificate cert) {
		confirm = false;

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("INF1416:Novo Usuario");
		window.setMinWidth(800);
		window.setMinHeight(300);

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

		// certificate info:
		// Versão, Série, Validade,
		// Tipo de Assinatura,
		// Emissor e Sujeito (Friendly Name)

		Label certVersionLabel = new Label("Versão:");
		Label certVersionLabel_= new Label(""+cert.getVersion());
		GridPane.setConstraints(certVersionLabel, 0, 3);
		GridPane.setConstraints(certVersionLabel_, 1, 3);

		Label certSerialLabel = new Label("Série:");
		Label certSerialLabel_= new Label(""+cert.getVersion());
		GridPane.setConstraints(certSerialLabel,  0, 4);
		GridPane.setConstraints(certSerialLabel_, 1, 4);

		Label certValidityLabel = new Label("Validade:");
		Label certValidityLabel_= new Label(certIsValid? "válido" : "inválido");
		GridPane.setConstraints(certValidityLabel,  0, 5);
		GridPane.setConstraints(certValidityLabel_, 1, 5);

		Label certSignatureTypeLabel = new Label("Tipo de Assinatura:");
		Label certSignatureTypeLabel_= new Label(cert.getSigAlgName());
		GridPane.setConstraints(certSignatureTypeLabel,  0, 6);
		GridPane.setConstraints(certSignatureTypeLabel_, 1, 6);

		Label certIssuerLabel = new Label("Emissor:");
		Label certIssuerLabel_= new Label(cert.getIssuerDN().getName());
		GridPane.setConstraints(certIssuerLabel,  0, 7);
		GridPane.setConstraints(certIssuerLabel_, 1, 7);

		Label certSubjectLabel = new Label("Nome:");
		Label certSubjectLabel_= new Label(cert.getSubjectDN().getName());
		GridPane.setConstraints(certSubjectLabel,  0, 8);
		GridPane.setConstraints(certSubjectLabel_, 1, 8);


		Button confirmButton = new Button("Confirmar");
		Button cancelButton  = new Button("Cancelar");
		GridPane.setConstraints(confirmButton, 0, 9);
		GridPane.setConstraints(cancelButton,  1, 9);

		confirmButton.setOnAction(e -> {
			confirm = true;
			window.close();
		});

		cancelButton.setOnAction( e -> { window.close();
			confirm = false;
			window.close();
		});

		grid.getChildren().addAll(
				nameLabel, nameLabel_,
				loginLabel, loginLabel_,
				descLabel, descLabel_,
				certVersionLabel, certVersionLabel_,
				certSerialLabel, certSerialLabel_,
				certValidityLabel, certValidityLabel_,
				certSignatureTypeLabel, certSignatureTypeLabel_,
				certIssuerLabel, certIssuerLabel_,
				certSubjectLabel, certSubjectLabel_,
				confirmButton, cancelButton
				);

		grid.setAlignment(Pos.CENTER);
		Scene scene = new Scene(grid);
		window.setScene(scene);
		window.showAndWait();

		return confirm;
	}
	private static boolean passwordIsAcceptable(String password)
	{
		if(password.length() != 6)
			return false;
		String[] sylabs = new String[3];
		sylabs[0] = password.substring(0, 2);
		sylabs[1] = password.substring(2, 4);
		sylabs[2] = password.substring(4, 6);
		List<String> phonList = new ArrayList<String>(Arrays.asList(Login.phonems));
		for(String s: sylabs)
			if(!phonList.contains(s))
				return false;
		if(sylabs.length != 3)
		{
			System.out.println("SERIOUS ERROR");
			return false;
		}
		for(int i=0;i<sylabs.length;i++)
		{
			if(sylabs[i].equals(sylabs[(i+1)%3]) || sylabs[i].equals(sylabs[(i+2)%3]))
					return false;
		}
		return true;
	}

	private static X509Certificate loadCertificate(String certFile)
	{
		X509Certificate cert_ = null;

		try {
			InputStream in = new FileInputStream(certFile);
			cert_ = X509Certificate.getInstance(in);
			in.close();

			cert_.checkValidity(); // <- expirado
			certIsValid = true;
		} catch (CertificateExpiredException e) {
			certIsValid = false;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cert_;
	}
}
