package com.paxdron.stuappconductor;

/**
 * Created by TOSHIBA PC on 03/03/2016.
 */
public class Ubicacion {
    public int idConductor;
    public int idRuta;
    public double latitud;
    public double longitud;

    public Ubicacion(int idConductor, int idRuta, double latitud,double longitud) {
        this.idConductor=idConductor;
        this.longitud = longitud;
        this.idRuta = idRuta;
        this.latitud = latitud;
    }
}
