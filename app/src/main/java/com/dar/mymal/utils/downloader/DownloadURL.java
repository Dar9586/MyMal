package com.dar.mymal.utils.downloader;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by stopp on 30/06/2017.
 */

public class DownloadURL extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... f_url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(f_url[0]).openConnection();
            BufferedReader in = new BufferedReader (new InputStreamReader(connection.getInputStream()));
            //String line;
            StringBuilder fina=new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                fina.append(line);
            }
            //System.out.println("Sub3:"+(System.nanoTime()-t));
            return fina.toString();
        } catch(Exception e) {}
        return "";
    }

}
