package com.gestogas.gestoline.data;

public class dataDetectoresHumo {

    String id;
    String no_detector;
    String ubicacion;
    String revision1;
    String resultado1;
    String revision2;
    String resultado2;
    String revision3;
    String resultado3;
    String revision4;
    String resultado4;

    public dataDetectoresHumo(String id, String no_detector, String ubicacion, String revision1, String resultado1, String revision2, String resultado2, String revision3, String resultado3, String revision4, String resultado4){
        this.id = id;
        this.no_detector = no_detector;
        this.ubicacion = ubicacion;
        this.revision1 = revision1;
        this.resultado1 = resultado1;
        this.revision2 = revision2;
        this.resultado2 = resultado2;
        this.revision3 = revision3;
        this.resultado3 = resultado3;
        this.revision4 = revision4;
        this.resultado4 = resultado4;
    }

    public String getId() {
        return id;
    }

    public String getNo_detector() {
        return no_detector;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getRevision1() {
        return revision1;
    }

    public String getResultado1() {
        return resultado1;
    }
    public String getRevision2() {
        return revision2;
    }
    public String getResultado2() {
        return resultado2;
    }
    public String getRevision3() {
        return revision3;
    }
    public String getResultado3() {
        return resultado3;
    }
    public String getRevision4() {
        return revision4;
    }
    public String getResultado4() {
        return resultado4;
    }

}
