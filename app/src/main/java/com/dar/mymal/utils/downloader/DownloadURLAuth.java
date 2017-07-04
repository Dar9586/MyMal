package com.dar.mymal.utils.downloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by stopp on 30/06/2017.
 */

public class DownloadURLAuth extends AsyncTask<String, String, Boolean> {
    String enco;
    ProgressDialog pgd;
    /**
     * Before starting background thread
     * */
    @Override
    protected void onPreExecute() {
        pgd.setTitle("Loading ent");
        pgd.setMessage("Please wait...");
        pgd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pgd.show();
    }
    public DownloadURLAuth(Context cont, String enco){
        pgd=new ProgressDialog(cont);
        this.enco=enco;
    }
    @Override
    protected void onPostExecute(Boolean image)
    {
        pgd.dismiss();
    }
    @Override
    protected Boolean doInBackground(String... f_url) {
        try {
            Log.e("LOGDATA","ENCODEDASYNC: "+enco);
            HttpURLConnection connection = (HttpURLConnection) new URL(f_url[0]).openConnection();
            connection.setRequestProperty  ("Authorization", "Basic " + enco);
            connection.getInputStream();
            return true;
        } catch(IOException e) {Log.e("LOGDATA","ERROR: "+e.getMessage());}
        return false;
    }

}
