package com.dar.mymal.utils;

import java.util.concurrent.ExecutionException;

import android.util.Base64;

import com.dar.mymal.downloader.DownloadURL;

/**
 * Created by stopp on 28/06/2017.
 */
public class LoginData {
    static String user,enco;
    static boolean log=false;
    public LoginData(String user,String enco,boolean ok){
        this.user = user;
        this.enco = enco;
        log=true;
    }
    public LoginData(String user, String pass){
        DownloadURL c=new DownloadURL(Base64.encodeToString((user+":"+pass).getBytes(),Base64.NO_WRAP));
        boolean ok=false;
        try {
            ok = c.execute("https://myanimelist.net/api/account/verify_credentials.xml").get()!="";
        }catch(InterruptedException|ExecutionException e){}
        if(ok) {
            this.user = user;
            this.enco = Base64.encodeToString((user+":"+pass).getBytes(),Base64.NO_WRAP);
            log = true;
        }
    }
    public static String  getUsername(){return user;}
    public static String  getEncoded (){return enco;}
    public static boolean isLogged   (){return  log;}
}
