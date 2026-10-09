package org.magnezis.inversiones.magnezis.service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.magnezis.inversiones.magnezis.model.Rol;
import org.magnezis.inversiones.magnezis.model.Usuario;
import org.magnezis.inversiones.magnezis.repository.RolRepository;
import org.magnezis.inversiones.magnezis.repository.UsuarioRepository;

public class UsuarioService {

    private static final int ITERACIONES = 65536;
    private static final int LONGITUD_BITS = 256;
    private static final int LONGITUD_SALT = 16;

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final RolRepository rolRepository = new RolRepository();

    // Devuelve el usuario si las credenciales son correctas; si no, null
    public Usuario autenticar(String correo, String password) throws SQLException {
        if (correo == null || correo.trim().isEmpty()
                || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Ingresa tu correo y tu contraseña.");
        }

        Usuario usuario = usuarioRepository.buscarPorCorreo(correo.trim());
        if (usuario == null || !verificarPassword(password, usuario.getPassword())) {
            return null;
        }
        return usuario;
    }

    public List<Usuario> listarUsuarios() throws SQLException {
        return usuarioRepository.listarTodos();
    }

    public List<Rol> listarRoles() throws SQLException {
        return rolRepository.listarTodos();
    }

    public void registrar(Usuario usuario) throws SQLException {
        validar(usuario, true);
        usuario.setPassword(hashearPassword(usuario.getPassword()));
        usuarioRepository.insertar(usuario);
    }

    // Si la contraseña viene vacía, se conserva la actual
    public void actualizar(Usuario usuario) throws SQLException {
        validar(usuario, false);
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuario.setPassword(hashearPassword(usuario.getPassword()));
        } else {
            usuario.setPassword(null);
        }
        usuarioRepository.actualizar(usuario);
    }

    public void eliminar(int idUsuario) throws SQLException {
        usuarioRepository.eliminar(idUsuario);
    }

    private void validar(Usuario u, boolean esNuevo) throws SQLException {
        if (vacio(u.getNombreCompleto()) || vacio(u.getNombreUsuario()) || vacio(u.getCorreo())) {
            throw new IllegalArgumentException("Nombre, usuario y correo son obligatorios.");
        }
        if (!u.getCorreo().contains("@")) {
            throw new IllegalArgumentException("El correo no es válido.");
        }
        if (esNuevo && vacio(u.getPassword())) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        if (u.getRol() == null) {
            throw new IllegalArgumentException("Selecciona un rol.");
        }

        u.setNombreCompleto(u.getNombreCompleto().trim());
        u.setNombreUsuario(u.getNombreUsuario().trim());
        u.setCorreo(u.getCorreo().trim());

        int idExcluir = esNuevo ? 0 : u.getIdUsuario();
        if (usuarioRepository.existeNombreUsuario(u.getNombreUsuario(), idExcluir)) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre de usuario.");
        }
    }

    private boolean vacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    // ---------- Hash de contraseñas (PBKDF2, incluido en el JDK) ----------
    // Se guarda como "salt:hash" en Base64

    private String hashearPassword(String passwordPlano) {
        byte[] salt = new byte[LONGITUD_SALT];
        new SecureRandom().nextBytes(salt);
        byte[] hash = calcularHash(passwordPlano, salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    private boolean verificarPassword(String passwordPlano, String almacenado) {
        if (almacenado == null || !almacenado.contains(":")) {
            return false;
        }
        String[] partes = almacenado.split(":");
        byte[] salt = Base64.getDecoder().decode(partes[0]);
        byte[] hashEsperado = Base64.getDecoder().decode(partes[1]);
        byte[] hashCalculado = calcularHash(passwordPlano, salt);
        return MessageDigest.isEqual(hashEsperado, hashCalculado);
    }

    private byte[] calcularHash(String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERACIONES, LONGITUD_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña", e);
        }
    }
}