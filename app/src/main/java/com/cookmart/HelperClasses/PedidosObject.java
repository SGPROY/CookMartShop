package com.cookmart.HelperClasses;

import java.io.Serializable;
import java.util.HashMap;

public class PedidosObject implements Serializable {
    private String idDireccion, tipoPago, estado, precioTotal, fecha, index, numeroTlf;
    private HashMap<String, HashMap<String, String>> productos;

    public String getNumeroTlf() {
        return numeroTlf;
    }

    public void setNumeroTlf(String numeroTlf) {
        this.numeroTlf = numeroTlf;
    }

    public String getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(String idDireccion) {
        this.idDireccion = idDireccion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(String precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public HashMap<String, HashMap<String, String>> getProductos() {
        return productos;
    }

    public void setProductos(HashMap<String, HashMap<String, String>> productos) {
        this.productos = productos;
    }
}
