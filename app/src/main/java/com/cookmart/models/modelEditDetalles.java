package com.cookmart.models;

public class modelEditDetalles {
    private String imagen, titulo, unidadMedida, cantidad, precio;

    public modelEditDetalles(String image, String title, String measuringUnit, String quantity, String price) {
        this.imagen = image;
        this.titulo = title;
        this.unidadMedida = measuringUnit;
        this.cantidad = quantity;
        this.precio = price;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }
}
