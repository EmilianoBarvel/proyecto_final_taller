package org.magnezis.inversiones.magnezis.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.magnezis.inversiones.magnezis.model.Rol;
import org.magnezis.inversiones.magnezis.model.Usuario;
import org.magnezis.inversiones.magnezis.service.UsuarioService;

public class DashboardController {

    @FXML private Label lblSesion;
    @FXML private TextField txtNombre;
    @FXML private TextField txtUsername;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<Rol> cmbRoles;
    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colRol;

    private final UsuarioService usuarioService = new UsuarioService();
    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdUsuario()).asObject());
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCompleto()));
        colUsername.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreUsuario()));
        colCorreo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCorreo()));
        colRol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRol().getNombreRol()));

        // Al seleccionar una fila, sus datos pasan al formulario
        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, antes, u) -> {
            if (u != null) {
                txtNombre.setText(u.getNombreCompleto());
                txtUsername.setText(u.getNombreUsuario());
                txtCorreo.setText(u.getCorreo());
                txtPassword.clear();
                for (Rol rol : cmbRoles.getItems()) {
                    if (rol.getIdRol() == u.getRol().getIdRol()) {
                        cmbRoles.setValue(rol);
                    }
                }
            }
        });

        try {
            cmbRoles.setItems(FXCollections.observableArrayList(usuarioService.listarRoles()));
            cargarUsuarios();
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        lblSesion.setText("Sesión: " + usuario.getNombreCompleto() + "  |  Rol: " + usuario.getRol().getNombreRol());
    }

    private void cargarUsuarios() throws Exception {
        tblUsuarios.setItems(FXCollections.observableArrayList(usuarioService.listarUsuarios()));
    }

    @FXML
    private void handleGuardar() {
        try {
            Usuario u = new Usuario();
            u.setNombreCompleto(txtNombre.getText());
            u.setNombreUsuario(txtUsername.getText());
            u.setCorreo(txtCorreo.getText());
            u.setPassword(txtPassword.getText());
            u.setRol(cmbRoles.getValue());

            Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                usuarioService.registrar(u);
            } else {
                u.setIdUsuario(seleccionado.getIdUsuario());
                usuarioService.actualizar(u);
            }
            cargarUsuarios();
            handleLimpiar();
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    @FXML
    private void handleLimpiar() {
        txtNombre.clear();
        txtUsername.clear();
        txtCorreo.clear();
        txtPassword.clear();
        cmbRoles.setValue(null);
        tblUsuarios.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleEliminar() {
        Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null || seleccionado.getIdUsuario() == usuarioActual.getIdUsuario()) {
            return;
        }
        try {
            usuarioService.eliminar(seleccionado.getIdUsuario());
            cargarUsuarios();
            handleLimpiar();
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    @FXML
    private void handleCerrarSesion(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/resources/view/login-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Inicio de sesión");
    }

    private void error(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
