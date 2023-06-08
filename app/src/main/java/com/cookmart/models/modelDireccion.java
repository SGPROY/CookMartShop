package com.cookmart.models;

public class modelDireccion {
    private String nombre, direccion, codPostal, referencia, numero, idDireccion, ciudad;

    public modelDireccion(String city, String name, String addressId, String pincode, String addressLine1, String landmark, String number) {
        this.nombre = name;
        this.direccion = addressLine1;
        this.referencia = landmark;
        this.codPostal = pincode;
        this.ciudad = city;
        this.numero = number;
        this.idDireccion = addressId;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getIdDireccion() {
        return idDireccion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodPostal() {
        return codPostal;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getReferencia() {
        return referencia;
    }

    public String getNumero() {
        return numero;
    }
}

