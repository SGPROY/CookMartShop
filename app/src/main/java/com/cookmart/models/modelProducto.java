package com.cookmart.models;

public class modelProducto {
    String imagen;
    String titulo;
    String precio, unidadMedida, descripcion, imagenMayor;
    String idProducto;

    public modelProducto(String image, String title, String price, String measuringUnit, String description, String bigImage, String productid) {
        this.imagen = image;
        this.titulo = title;
        this.precio = price;
        this.unidadMedida = measuringUnit;
        this.descripcion = description;
        this.imagenMayor = bigImage;
        this.idProducto = productid;
    }

    public String getPrecio() {
        return precio;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getImagenMayor() {
        return imagenMayor;
    }

    public String getImagen() {
        return imagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getIdProducto() {
        return idProducto;
    }

}
