package com.dar.mymal.utils.downloader;

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
    Boolean ok=false;
    String user,pass,enco;
    /**
     * Before starting background thread
     * */
    @Override
    protected void onPreExecute() {

    }
    public DownloadURLAuth(String enco){
        this.enco=enco;
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
