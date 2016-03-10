package com.paxdron.stuappconductor;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int ID_CONDUCTOR=0;
    public Spinner spinner;
    private Button btnIniFinRec;
    Handler handler = new Handler();
    private boolean active;
    LocationManager locationManager;
    Location location_;
    AlertDialog alert = null;
    LocationListener locationListener;
    Ubicacion current;
    ServerRequest serverRequest;


    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            getCurrentLocation();

            ActualizarRecorrido(current);
            System.out.println(current.latitud + " " + current.longitud);
            // Repeat this the same runnable code block again another 2 seconds
            handler.postDelayed(runnableCode, 2000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = (Spinner) findViewById(R.id.spSelectRuta);
        btnIniFinRec=(Button)findViewById(R.id.btnIniciar);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rutas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        active=false;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        current= new Ubicacion(ID_CONDUCTOR);

        getCurrentLocation();

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                current.latitud=location.getLatitude();
                current.longitud=location.getLatitude();
                System.out.println("Cambio");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
        serverRequest = new ServerRequest(this);
        /*
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new  Localizacion();
        Local.setMainActivity(this);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                (LocationListener) Local);*/
    }

    public void iniciarRecorrido(View v){
        int idRuta=spinner.getSelectedItemPosition();
        getCurrentLocation();
        double lat=location_.getLatitude();
        double lon=location_.getLongitude();
        String locStr=Double.toString(lat)+" "+Double.toString(lon);
        showToast(locStr, this);

        if(active) {
            handler.removeCallbacks(runnableCode);
            finalizarRecorrido(ID_CONDUCTOR);
            btnIniFinRec.setText(getString(R.string.btnIniciar));
        }
        else {
            handler.post(runnableCode);
            current=new Ubicacion(ID_CONDUCTOR,idRuta,lat,lon);
            iniciarRecorrido(current);
            btnIniFinRec.setText(getString(R.string.btnFinalizar));
        }
        active=!active;

    }

    public void iniciarRecorrido(Ubicacion location){
        serverRequest.registerDoInBackground(location, new GetUbicacionCallback() {
            @Override
            public void done(Ubicacion returnedUbicacion) {
                showToast("Iniciado",getApplicationContext());
            }
        });
    }

    public void ActualizarRecorrido(Ubicacion location){
        serverRequest.updateDoInBackground(location, new GetUbicacionCallback() {
            @Override
            public void done(Ubicacion returnedUbicacion) {
            }
        });
    }

    public void finalizarRecorrido(int idConductor){
        serverRequest.deleteDoInBackground(idConductor, new GetUbicacionCallback() {
            @Override
            public void done(Ubicacion returnedUbicacion) {
                showToast("Finalizado", getApplicationContext());
            }
        });
    }

    private  void showToast(String message, Context context) {  //Funcion para mostrar mensajes en pantalla
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    public void getCurrentLocation(){
        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            AlertNoGps();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            } else {
                location_ = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        } else {
            location_ = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }


    @Override
    protected void onDestroy() {
        if(active) {
            handler.removeCallbacks(runnableCode);
            finalizarRecorrido(ID_CONDUCTOR);
        }
        super.onDestroy();
    }
}
