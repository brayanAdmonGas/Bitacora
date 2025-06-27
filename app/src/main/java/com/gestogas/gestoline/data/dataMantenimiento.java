package com.gestogas.gestoline.data;

public class dataMantenimiento {

    public int id;
    public String folio;
    public String idequipo;
    public String descripcion;
    public String fecha;
    public String hora;
    public String estado;
    public String numverificacion;

    public dataMantenimiento(int id, String folio, String idequipo, String descripcion, String fecha, String hora, String estado, String numverificacion) {
        this.id = id;
        this.folio = folio;
        this.idequipo = idequipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.numverificacion = numverificacion;
    }

    public int getId() {
        return id;
    }

    public String getFolio() {
        return folio;
    }

    public String getIdequipo() {
        return idequipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getEstado() {
        return estado;
    }

    public String getNumverificacion() {
        return numverificacion;
    }
}

