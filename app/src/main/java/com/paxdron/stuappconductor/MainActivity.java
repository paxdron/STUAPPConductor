package com.paxdron.stuappconductor;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
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
    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            Log.d("Handlers", "Called on main thread");
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
        /*
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new  Localizacion();
        Local.setMainActivity(this);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                (LocationListener) Local);*/
    }

    public void iniciarRecorrido(View v){
        int idRuta=spinner.getSelectedItemPosition();
        double lat=89.14;
        double lon=18.98;
        if(active) {
            //handler.removeCallbacks(runnableCode);
            finalizarRecorrido(ID_CONDUCTOR);
            btnIniFinRec.setText(getString(R.string.btnIniciar));
        }
        else {
            //handler.post(runnableCode);
            Ubicacion current=new Ubicacion(ID_CONDUCTOR,idRuta,lat,lon);
            iniciarRecorrido(current);
            btnIniFinRec.setText(getString(R.string.btnFinalizar));
        }
        active=!active;

    }

    public void iniciarRecorrido(Ubicacion location){
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.registerDoInBackground(location, new GetUbicacionCallback() {
            @Override
            public void done(Ubicacion returnedUbicacion) {
                showToast("Iniciado",getApplicationContext());
            }
        });
    }

    public void finalizarRecorrido(int idConductor){
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.deleteDoInBackground(idConductor, new GetUbicacionCallback() {
            @Override
            public void done(Ubicacion returnedUbicacion) {
                showToast("Finalizado",getApplicationContext());
            }
        });
    }

    private  void showToast(String message, Context context) {  //Funcion para mostrar mensajes en pantalla
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }




}
