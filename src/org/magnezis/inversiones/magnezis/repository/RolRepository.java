
package org.magnezis.inversiones.magnezis.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.magnezis.inversiones.magnezis.config.ConnectionDB;
import org.magnezis.inversiones.magnezis.model.Rol;

public class RolRepository {

    public List<Rol> listarTodos() throws SQLException {
        String sql = "SELECT id_rol, nombre_rol, descripcion FROM roles ORDER BY nombre_rol";
        List<Rol> roles = new ArrayList<>();
        Connection con = ConnectionDB.getConnectionDataBase();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                roles.add(new Rol(
                        rs.getInt("id_rol"),
                        rs.getString("nombre_rol"),
                        rs.getString("descripcion")));
            }
        }
        return roles;
    }
}