package com.dar.mymal.utils;

import com.dar.mymal.entries.*;
import com.dar.mymal.utils.downloader.DownloadURL;
import com.dar.mymal.utils.downloader.DownloadURLAuth;

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
    public static List<List<Entry>>[] getEntries(String user){
        String[]tempxmls=getList(user,true).split("</anime>");
        List<List<Entry>>[] list= new List[]{new ArrayList<>(),new ArrayList<>()};
        list[0].add(new ArrayList<Entry>());list[0].add(new ArrayList<Entry>());list[0].add(new ArrayList<Entry>());list[0].add(new ArrayList<Entry>());list[0].add(new ArrayList<Entry>());
        list[1].add(new ArrayList<Entry>());list[1].add(new ArrayList<Entry>());list[1].add(new ArrayList<Entry>());list[1].add(new ArrayList<Entry>());list[1].add(new ArrayList<Entry>());
        List<Integer>nums=new ArrayList<>(Arrays.asList(1,2,3,4,6));
        for(int a=0;a<tempxmls.length-1;a++){
            list[0].get(nums.indexOf(Integer.parseInt(Entry.findTagValue("my_status",tempxmls[a])))).add(new Anime(tempxmls[a]));
        }
        tempxmls=getList(user,false).split("</manga>");
        for(int a=0;a<tempxmls.length-1;a++){
            list[1].get(nums.indexOf(Integer.parseInt(Entry.findTagValue("my_status",tempxmls[a])))).add(new Manga(tempxmls[a]));
        }
        return  list;
    }
    public static String getList(String user,boolean anime){
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
    public static void removeFromList(int id){
        new DownloadURLAuth(LoginData.enco).execute("https://myanimelist.net/api/animelist/delete/"+id+".xml");
    }
    public static void addToList(int id){
        new DownloadURLAuth(LoginData.enco).execute("https://myanimelist.net/api/mangalist/add/"+id+".xml?data=%3C?xml%20version=%221.0%22%20encoding=%22UTF-8%22?%3E%3Centry%3E%3Cepisode%3E0%3C/episode%3E%3Cstatus%3E6%3C/status%3E%3Cscore%3E%3C/score%3E%3Cstorage_type%3E%3C/storage_type%3E%3Cstorage_value%3E%3C/storage_value%3E%3Ctimes_rewatched%3E%3C/times_rewatched%3E%3Crewatch_value%3E%3C/rewatch_value%3E%3Cdate_start%3E%3C/date_start%3E%3Cdate_finish%3E%3C/date_finish%3E%3Cpriority%3E%3C/priority%3E%3Cenable_discussion%3E%3C/enable_discussion%3E%3Cenable_rewatching%3E%3C/enable_rewatching%3E%3Ccomments%3E%3C/comments%3E%3Ctags%3E%3C/tags%3E%3C/entry%3E");
    }

}

