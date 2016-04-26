package com.paxdron.stuappconductor;

/**
 * Created by TOSHIBA PC on 03/03/2016.
 */
public class Ubicacion {
    public int idConductor;
    public int idRuta;
    public double latitud;
    public double longitud;
    public int idBus;
    public Ubicacion(int idConductor, int idRuta, double latitud,double longitud, int idBus) {
        this.idConductor=idConductor;
        this.longitud = longitud;
        this.idRuta = idRuta;
        this.latitud = latitud;
        this.idBus = idBus;
    }

    public Ubicacion(int idCond) {
        idConductor=idCond;
    }
}
