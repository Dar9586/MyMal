package com.dar.mymal.global;

import java.util.concurrent.ExecutionException;

import android.util.Base64;
import android.util.Log;

import com.dar.mymal.downloader.DownloadURL;


public class LoginData {
    static String user,enco;
    static boolean log=false;
    public LoginData(String user,String enco,boolean ok){
        LoginData.user = user;
        LoginData.enco = enco;
        log=true;
    }
    public LoginData(final String usery,final String pass){
        DownloadURL c=new DownloadURL(new DownloadURL.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                boolean ok=!output.equals("");
                if(ok){
                    user = usery;
                    enco = Base64.encodeToString((user+":"+pass).getBytes(),Base64.NO_WRAP);
                    log = true;
                }
            }
        },Base64.encodeToString((user + ":" + pass).getBytes(), Base64.NO_WRAP));
    }
    public static String  getUsername(){return user;}
    public static String  getEncoded (){return enco;}
    public static boolean isLogged   (){return  log;}
}
