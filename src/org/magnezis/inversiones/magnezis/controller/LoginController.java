package org.magnezis.inversiones.magnezis.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.magnezis.inversiones.magnezis.model.Usuario;
import org.magnezis.inversiones.magnezis.service.UsuarioService;

public class LoginController {

    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtPassword;

    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    private void handleLogin() {
        try {
            Usuario usuario = usuarioService.autenticar(txtCorreo.getText(), txtPassword.getText());
            if (usuario == null) {
                error("Correo o contraseña incorrectos.");
                txtPassword.clear();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/view/dashboard-view.fxml"));
            Parent root = loader.load();
            ((DashboardController) loader.getController()).setUsuario(usuario);

            Stage stage = (Stage) txtCorreo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard principal");
            stage.centerOnScreen();
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    private void error(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
