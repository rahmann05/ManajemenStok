package com.jejekatering.jstok.model;

public class Pengguna {
    private int idPengguna;
    private String username;
    private String password;
    private String role;

    // Constructor
    public Pengguna() {}

    public Pengguna(int idPengguna, String username, String password, String role) {
        this.idPengguna = idPengguna;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    public int getIdPengguna() { return idPengguna; }
    public void setIdPengguna(int idPengguna) { this.idPengguna = idPengguna; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}