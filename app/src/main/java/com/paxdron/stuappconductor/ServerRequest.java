package com.paxdron.stuappconductor;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by TOSHIBA PC on 03/03/2016.
 */
public class ServerRequest {
    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT=1000*15;
    public static final String SERVER_ADDRESS = "http://10.0.2.2/login/";
    private Context context;
    public ServerRequest(Context context){
        this.context =context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Procesando");
        progressDialog.setMessage("Por favor, espere");

    }

    public  void registerDoInBackground(Ubicacion location, GetUbicacionCallback callback){
        progressDialog.show();
        new AsyncTaskRegistro(location,callback).execute();
    }

    public  void deleteDoInBackground(int idConductor, GetUbicacionCallback callback){
        progressDialog.show();
        new AsyncTaskDelete(idConductor,callback).execute();
    }

    public  void updateDoInBackground(Ubicacion location, GetUbicacionCallback callback){
        new AsyncTaskUpdate(location,callback).execute();
    }

    public class AsyncTaskRegistro extends AsyncTask<Void, Void, Void>{
        Ubicacion location;
        GetUbicacionCallback locationCallback;

        public AsyncTaskRegistro(Ubicacion location, GetUbicacionCallback locationCallback) {
            this.location = location;
            this.locationCallback = locationCallback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            /*dataToSend.put(context.getString(R.string.name),user.nombre);
            dataToSend.put(context.getString(R.string.password),user.contra);
            dataToSend.put(context.getString(R.string.username),user.usuario);
            dataToSend.put(context.getString(R.string.mail),user.correo);*/
            int idConductor=location.idConductor;
            int idRuta = location.idRuta;
            double latitud = location.latitud;
            double longitud = location.longitud;
            int idBus = location.idBus;

            try {
                URL url = new URL(context.getString(R.string.URLRegister));
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                //httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode(context.getString(R.string.idRuta), "UTF-8") + "=" + URLEncoder.encode(Integer.toString(idRuta), "UTF-8") + "&" +
                        URLEncoder.encode(context.getString(R.string.idConductor), "UTF-8") + "=" + URLEncoder.encode(Integer.toString(idConductor), "UTF-8") + "&" +
                        URLEncoder.encode(context.getString(R.string.latitud), "UTF-8") + "=" + URLEncoder.encode(Double.toString(latitud), "UTF-8") + "&" +
                        URLEncoder.encode(context.getString(R.string.longitud), "UTF-8") + "=" + URLEncoder.encode(Double.toString(longitud), "UTF-8") + "&" +
                        URLEncoder.encode(context.getString(R.string.idBus), "UTF-8") + "=" + URLEncoder.encode(Double.toString(idBus), "UTF-8");
                System.out.println("info enviada "+data);
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                IS.close();
                //httpURLConnection.connect();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            locationCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }

    public class AsyncTaskDelete extends AsyncTask<Void, Void, Void>{
        int idConductor;
        GetUbicacionCallback locationCallback;

        public AsyncTaskDelete(int idConductor, GetUbicacionCallback locationCallback) {
            this.idConductor = idConductor;
            this.locationCallback = locationCallback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            /*dataToSend.put(context.getString(R.string.name),user.nombre);
            dataToSend.put(context.getString(R.string.password),user.contra);
            dataToSend.put(context.getString(R.string.username),user.usuario);
            dataToSend.put(context.getString(R.string.mail),user.correo);*/
            try {
                URL url = new URL(context.getString(R.string.URLDelete));
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                //httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode(context.getString(R.string.idConductor), "UTF-8") + "=" + URLEncoder.encode(Integer.toString(idConductor), "UTF-8");
                System.out.println("info enviada "+data);
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                IS.close();
                //httpURLConnection.connect();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            locationCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }


    public class AsyncTaskUpdate extends AsyncTask<Void, Void, Void>{
        Ubicacion location;
        GetUbicacionCallback locationCallback;

        public AsyncTaskUpdate(Ubicacion location, GetUbicacionCallback locationCallback) {
            this.location = location;
            this.locationCallback = locationCallback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            /*dataToSend.put(context.getString(R.string.name),user.nombre);
            dataToSend.put(context.getString(R.string.password),user.contra);
            dataToSend.put(context.getString(R.string.username),user.usuario);
            dataToSend.put(context.getString(R.string.mail),user.correo);*/
            int idConductor=location.idConductor;
            double latitud = location.latitud;
            double longitud = location.longitud;

            try {
                URL url = new URL(context.getString(R.string.URLUpdate));
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                //httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode(context.getString(R.string.idConductor), "UTF-8") + "=" + URLEncoder.encode(Integer.toString(idConductor), "UTF-8") + "&" +
                        URLEncoder.encode(context.getString(R.string.latitud), "UTF-8") + "=" + URLEncoder.encode(Double.toString(latitud), "UTF-8") + "&" +
                        URLEncoder.encode(context.getString(R.string.longitud), "UTF-8") + "=" + URLEncoder.encode(Double.toString(longitud), "UTF-8");
                System.out.println("info enviada "+data);
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                IS.close();
                //httpURLConnection.connect();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            locationCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }

}

