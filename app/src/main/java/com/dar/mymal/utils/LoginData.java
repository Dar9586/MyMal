package com.dar.mymal.utils;

import java.io.BufferedReader;
import java.io.*;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.dar.mymal.utils.downloader.DownloadURLAuth;

/**
 * Created by stopp on 28/06/2017.
 */
public class LoginData {
    static String user,pass,enco;
    static boolean log=false;
    public LoginData(String user,String enco,boolean ok){
        this.user = user;
        this.enco = enco;
        log=true;
    }
    public LoginData(String user,String pass){
        DownloadURLAuth c=new DownloadURLAuth(new String(Base64.encode((user+":"+pass).getBytes(),1)));
        boolean ok=false;
        try {
            ok = c.execute("https://myanimelist.net/api/account/verify_credentials.xml").get();
        }catch(InterruptedException|ExecutionException e){}
        if(ok) {
            this.user = user;
            this.pass = pass;
            this.enco = new String(Base64.encode((user+":"+pass).getBytes(),1));
            log = true;
        }
    }
    public static String  getUsername(){return user;}
    public static String  getEncoded (){return enco;}
    public static boolean isLogged   (){return  log;}
}
