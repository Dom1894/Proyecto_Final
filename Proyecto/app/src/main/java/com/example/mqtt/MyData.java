package com.example.mqtt;

public class MyData {
    private String ID;
    private String TITULO;
    private String MENSAJE;

    public MyData() {
    }

    public MyData(String ID, String TITULO, String MENSAJE) {
        this.ID = ID;
        this.TITULO = TITULO;
        this.MENSAJE = MENSAJE;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTITULO() {
        return TITULO;
    }

    public void setTITULO(String TITULO) {
        this.TITULO = TITULO;
    }

    public String getMENSAJE() {
        return MENSAJE;
    }

    public void setMENSAJE(String MENSAJE) {
        this.MENSAJE = MENSAJE;
    }
}
