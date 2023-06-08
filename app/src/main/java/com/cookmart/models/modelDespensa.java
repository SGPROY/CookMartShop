package com.cookmart.models;

public class modelDespensa {
    private final double precioCantidad;
    private final String titulo;
    private final String tasaPrecio;
    private final String imagen;
    private final String unidadMedida;
    private final String idProducto;
    private String precio;
    private String cantidad;

    public modelDespensa(double quantityPrice, String productId, String measuringUnit, String title, String pricerate, String image, String price, String items) {
        this.titulo = title;
        this.unidadMedida = measuringUnit;
        this.tasaPrecio = pricerate;
        this.imagen = image;
        this.precio = price;
        this.precioCantidad = quantityPrice;
        this.cantidad = items;
        this.idProducto = productId;
    }

    public double getPrecioCantidad() {
        return precioCantidad;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getTasaPrecio() {
        return tasaPrecio;
    }

    public String getImagen() {
        return imagen;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }
}
