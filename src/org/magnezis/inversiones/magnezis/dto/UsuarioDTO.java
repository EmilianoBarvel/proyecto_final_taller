/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.magnezis.inversiones.magnezis.dto;


public class UsuarioDTO {
    private int id;
    private String username;
    private String nombre;
    private String nombreRol;
    private int rolId;

    public UsuarioDTO() {}

    public UsuarioDTO(int id, String username, String nombre, String nombreRol, int rolId) {
        this.id = id;
        this.username = username;
        this.nombre = nombre;
        this.nombreRol = nombreRol;
        this.rolId = rolId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }

    public int getRolId() { return rolId; }
    public void setRolId(int rolId) { this.rolId = rolId; }
}
