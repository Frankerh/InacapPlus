package com.example.myapplication;

public class Publicacion {
    private String usuario;
    private String fecha;
    private String opinion;
    private int likes;

    public Publicacion(String usuario, String fecha, String opinion, int likes) {
        this.usuario = usuario;
        this.fecha = fecha;
        this.opinion = opinion;
        this.likes = likes;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
