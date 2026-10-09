package org.magnezis.inversiones.magnezis.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.magnezis.inversiones.magnezis.config.ConnectionDB;
import org.magnezis.inversiones.magnezis.model.Rol;
import org.magnezis.inversiones.magnezis.model.Usuario;

public class UsuarioRepository {

    private static final String SELECT_BASE
            = "SELECT u.id_usuario, u.nombre_completo, u.nombre_usuario, u.correo, u.password, "
            + "r.id_rol, r.nombre_rol, r.descripcion "
            + "FROM usuarios u INNER JOIN roles r ON u.id_rol = r.id_rol ";

    public List<Usuario> listarTodos() throws SQLException {
        String sql = SELECT_BASE + "ORDER BY u.id_usuario";
        List<Usuario> usuarios = new ArrayList<>();
        Connection con = ConnectionDB.getConnectionDataBase();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapear(rs));
            }
        }
        return usuarios;
    }

    public Usuario buscarPorCorreo(String correo) throws SQLException {
        String sql = SELECT_BASE + "WHERE u.correo = ?";
        Connection con = ConnectionDB.getConnectionDataBase();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    // idExcluir: id del usuario que se está editando (0 si es uno nuevo)
    public boolean existeNombreUsuario(String nombreUsuario, int idExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE nombre_usuario = ? AND id_usuario <> ?";
        Connection con = ConnectionDB.getConnectionDataBase();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setInt(2, idExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public void insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre_completo, nombre_usuario, correo, password, id_rol) "
                + "VALUES (?, ?, ?, ?, ?)";
        Connection con = ConnectionDB.getConnectionDataBase();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombreCompleto());
            ps.setString(2, u.getNombreUsuario());
            ps.setString(3, u.getCorreo());
            ps.setString(4, u.getPassword());
            ps.setInt(5, u.getRol().getIdRol());
            ps.executeUpdate();
        }
    }

    // Si u.getPassword() es null o vacío, la contraseña actual no se modifica
    public void actualizar(Usuario u) throws SQLException {
        boolean cambiarPassword = u.getPassword() != null && !u.getPassword().isEmpty();
        String sql = cambiarPassword
                ? "UPDATE usuarios SET nombre_completo = ?, nombre_usuario = ?, correo = ?, id_rol = ?, password = ? WHERE id_usuario = ?"
                : "UPDATE usuarios SET nombre_completo = ?, nombre_usuario = ?, correo = ?, id_rol = ? WHERE id_usuario = ?";
        Connection con = ConnectionDB.getConnectionDataBase();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombreCompleto());
            ps.setString(2, u.getNombreUsuario());
            ps.setString(3, u.getCorreo());
            ps.setInt(4, u.getRol().getIdRol());
            if (cambiarPassword) {
                ps.setString(5, u.getPassword());
                ps.setInt(6, u.getIdUsuario());
            } else {
                ps.setInt(5, u.getIdUsuario());
            }
            ps.executeUpdate();
        }
    }

    public void eliminar(int idUsuario) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        Connection con = ConnectionDB.getConnectionDataBase();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Rol rol = new Rol(
                rs.getInt("id_rol"),
                rs.getString("nombre_rol"),
                rs.getString("descripcion"));

        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombreCompleto(rs.getString("nombre_completo"));
        u.setNombreUsuario(rs.getString("nombre_usuario"));
        u.setCorreo(rs.getString("correo"));
        u.setPassword(rs.getString("password"));
        u.setRol(rol);
        return u;
    }
}