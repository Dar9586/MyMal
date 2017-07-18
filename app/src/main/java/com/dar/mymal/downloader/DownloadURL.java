package com.dar.mymal.downloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by stopp on 30/06/2017.
 */

public class DownloadURL extends AsyncTask<String, String, String> {
    String enco="";
    ProgressDialog pgd;
    boolean conte=false;
    @Override
    protected void onPreExecute() {
        if(conte){
        pgd.setTitle("Loading ent");
        pgd.setMessage("Please wait...");
        pgd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pgd.show();}
    }
    public DownloadURL(){}
    public DownloadURL(Context cont){
        pgd=new ProgressDialog(cont);
        conte=true;
    }
    public DownloadURL(String enco){
        this.enco=enco;
    }
    public DownloadURL(Context cont, String enco){
        pgd=new ProgressDialog(cont);
        conte=true;
        this.enco=enco;
    }
    @Override
    protected void onPostExecute(String x)
    {
        if(conte) {
            pgd.dismiss();
        }
    }
    @Override
    protected String doInBackground(String... f_url) {
        try {
            Log.i("OnMALInfo","Downloading string from: "+f_url[0]);
            HttpURLConnection connection = (HttpURLConnection) new URL(f_url[0]).openConnection();
            if(enco!=""){
                Log.i("OnMALInfo","Applying encoding");
                connection.setRequestProperty  ("Authorization", "Basic " + enco);
            }
            connection.setRequestProperty( "User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64)");
            BufferedReader in = new BufferedReader (new InputStreamReader(connection.getInputStream()));
            StringBuilder fina=new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                //Log.w("LINEHTML",line);
                fina.append(line);
            }
            return fina.toString();
        } catch(IOException e) {Log.e("OnMALError","DownloadURL DownloadError: "+ e.getMessage());}
        return "";
    }

}
