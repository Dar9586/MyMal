package com.dar.mymal.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.dar.mymal.entries.*;
import com.dar.mymal.tuple.Tuple2;
import com.dar.mymal.utils.downloader.DownloadURL;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by stopp on 28/06/2017.
 */
public  class MALUtils {
    public static String getDescription(int id,boolean anime){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://myanimelist.net/"+(anime?"anime":"manga")+"/"+id).openConnection();
            BufferedReader in = new BufferedReader (new InputStreamReader(connection.getInputStream()));
            StringBuilder fina=new StringBuilder();
            String line;
            while (!(line = in.readLine()).contains("/manifest.json")) {
                fina.append(line);
            }
            int j=fina.indexOf("og:description")+25;
            return fina.substring(j,fina.indexOf("\"",j));
            //System.out.println("Sub3:"+(System.nanoTime()-t));
        } catch(Exception e) {}
        return "";
    }
    public static List<Entry>[][] getEntries(String user){
        List<Entry>[][] list=new List[][]{{new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()},{new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()}};
        List<Integer>nums=new ArrayList<>(Arrays.asList(1,2,3,4,6));
        String[]tempxmls=getList(user,true).split("</anime>");
        for(int a=0;a<tempxmls.length-1;a++){
            list[0][nums.indexOf(Integer.parseInt(Entry.findTagValue("my_status",tempxmls[a])))].add(new Anime(tempxmls[a]));
        }
        tempxmls=getList(user,false).split("</manga>");
        for(int a=0;a<tempxmls.length-1;a++){
            list[1][nums.indexOf(Integer.parseInt(Entry.findTagValue("my_status",tempxmls[a])))].add(new Manga(tempxmls[a]));
        }
        return  list;
    }
    public static String getList(String user, boolean anime){
        try {
            return new DownloadURL().execute("https://myanimelist.net/malappinfo.php?u="+user+"&status=all&type="+(anime?"anime":"manga")).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String IndentXml(String x){
        return IndentXml(new StringBuilder(x));
    }
    public static String IndentXml(StringBuilder fina){
        int ol=0,l;
        Boolean closed=false;
        while((l=fina.indexOf("><",ol))>ol){
            if(fina.charAt(l+2)!='/') {
                closed=false;
                fina.insert(l + 1, '\n');
            }else{
                if(closed){
                    fina.insert(l + 1, '\n');
                }
                closed=true;
            }

            ol=l+2;}
        return fina.toString();
    }
    public static Tuple2<Integer,Integer> getIdIndex(List<Entry>en[], int id){
        for(int a=0;a<5;a++)for(int b=0;b<en[a].size();b++)if(en[a].get(b).getID()==id)return new Tuple2<>(a,b);
        return null;
    }
}
