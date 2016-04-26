package com.paxdron.stuappconductor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yayandroid.locationmanager.LocationBaseActivity;
import com.yayandroid.locationmanager.LocationConfiguration;
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.constants.FailType;
import com.yayandroid.locationmanager.constants.LogType;
import com.yayandroid.locationmanager.constants.ProviderType;

public class MainActivity extends LocationBaseActivity {
    public Spinner spinnerRuta,spinnerBus;
    private Button btnIniFinRec;
    Handler handler = new Handler();
    private boolean active;
    AlertDialog alert = null;
    Ubicacion current;
    ServerRequest serverRequest;
    String nombreUser;
    int idConductor;
    private ProgressDialog progressDialog;

    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            current.latitud=location_.getLatitude();
            current.longitud=location_.getLongitude();
            ActualizarRecorrido(current);
            System.out.println(current.latitud + " " + current.longitud);
            // Repeat this the same runnable code block again another 2 seconds
            handler.postDelayed(runnableCode, 5000);
            getLocation();
        }
    };
    private TextView locationText;
    private Location location_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nombreUser=getIntent().getExtras().getString(Login.USER);
        idConductor=Integer.parseInt(getIntent().getExtras().getString(Login.ID_USER));
        ((TextView)findViewById(R.id.tvWelcome)).setText("Bienvenido "+nombreUser);
        spinnerRuta = (Spinner) findViewById(R.id.spSelectRuta);
        spinnerBus = (Spinner) findViewById(R.id.spSelectUnidad);
        btnIniFinRec=(Button)findViewById(R.id.btnIniciar);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rutas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.buses, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRuta.setAdapter(adapter);
        spinnerBus.setAdapter(adapter2);
        active=false;
        current= new Ubicacion(idConductor);
        locationText = (TextView) findViewById(R.id.locationText);

        LocationManager.setLogType(LogType.GENERAL);
        getLocation();
        serverRequest = new ServerRequest(this);
        /*
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new  Localizacion();
        Local.setMainActivity(this);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                (LocationListener) Local);*/
    }

    public void iniciarRecorrido(View v){
        int idRuta=spinnerRuta.getSelectedItemPosition();
        double lat=location_.getLatitude();
        double lon=location_.getLongitude();
        int idBus =spinnerBus.getSelectedItemPosition()+1;
        String locStr=Double.toString(lat)+" "+Double.toString(lon);
        showToast(locStr, this);

        if(active) {
            handler.removeCallbacks(runnableCode);
            finalizarRecorrido(idConductor);
            btnIniFinRec.setText(getString(R.string.btnIniciar));
        }
        else {
            handler.post(runnableCode);
            current=new Ubicacion(idConductor,idRuta,lat,lon,idBus);
            iniciarRecorrido(current);
            btnIniFinRec.setText(getString(R.string.btnFinalizar));
        }
        active=!active;

    }

    public void iniciarRecorrido(Ubicacion location){
        serverRequest.registerDoInBackground(location, new GetUbicacionCallback() {
            @Override
            public void done(Ubicacion returnedUbicacion) {
                showToast("Iniciado", getApplicationContext());
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

    @Override
    protected void onDestroy() {
        if(active) {
            handler.removeCallbacks(runnableCode);
            finalizarRecorrido(idConductor);
        }
        super.onDestroy();
    }

    @Override
    public LocationConfiguration getLocationConfiguration() {
        return new LocationConfiguration()
                .keepTracking(true)
                .askForGooglePlayServices(true)
                .setWaitPeriod(ProviderType.GOOGLE_PLAY_SERVICES, 5 * 100)
                .setWaitPeriod(ProviderType.GPS, 5 * 100)
                .setWaitPeriod(ProviderType.NETWORK, 5 * 100)
                .setTimeInterval(5 * 1000);
    }

    @Override
    public void onLocationChanged(Location location) {
        setText(location);
    }

    @Override
    public void onLocationFailed(int failType) {
        switch (failType) {
            case FailType.PERMISSION_DENIED: {
                locationText.setText("Couldn't get location, because user didn't give permission!");
                break;
            }
            case FailType.GP_SERVICES_NOT_AVAILABLE:
            case FailType.GP_SERVICES_CONNECTION_FAIL: {
                locationText.setText("Couldn't get location, because Google Play Services not available!");
                break;
            }
            case FailType.NETWORK_NOT_AVAILABLE: {
                locationText.setText("Couldn't get location, because network is not accessible!");
                break;
            }
            case FailType.TIMEOUT: {
                locationText.setText("Couldn't get location, and timeout!");
                break;
            }
        }
    }


    private void setText(Location location) {
        location_=location;
        String appendValue = location.getLatitude() + ", " + location.getLongitude() + "\n";

        locationText.setText(appendValue);
    }


}
