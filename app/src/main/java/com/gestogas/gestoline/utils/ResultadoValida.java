package com.gestogas.gestoline.utils;

public class ResultadoValida {
    private int iddetalle;
    private String resultado;

    public ResultadoValida(int iddetalle, String resultado) {
        this.iddetalle = iddetalle;
        this.resultado = resultado;
    }

    public int getIddetalle() {
        return iddetalle;
    }

    public String getResultado() {
        return resultado;
    }
}
