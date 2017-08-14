package com.dar.mymal.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.dar.mymal.ViewLoader;
import com.dar.mymal.adapters.listEntries.AnimeAdapter;
import com.dar.mymal.adapters.listEntries.MangaAdapter;
import com.dar.mymal.downloader.AsyncSplitter;
import com.dar.mymal.entries.api.Anime;
import com.dar.mymal.entries.api.Entry;
import com.dar.mymal.entries.api.Manga;
import com.dar.mymal.global.EntryList;
import com.dar.mymal.global.Settings;
import com.dar.mymal.tuple.Tuple2;
import com.dar.mymal.downloader.DownloadURL;

import java.io.BufferedReader;
import java.io.File;
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
    final static List<Integer>nums=new ArrayList<>(Arrays.asList(1,2,3,4,6));
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
        } catch(Exception e) {}
        return "";
    }
    public static List<Entry>[] getEntriesAnime(String user){
        Log.i("OnMALInfo","Refreshing list anime");
        List<Entry>[] list=new List[]{new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()};
        String[]tempxmls=getList(user,true).split("</anime>");
        for(int a=0;a<tempxmls.length-1;a++){
            list[nums.indexOf(Integer.parseInt(Entry.findTagValue("my_status",tempxmls[a])))].add(new Anime(tempxmls[a]));
        }
        return list;
    }
    public static List<Entry>[] getEntriesManga(String user){
        Log.i("OnMALInfo","Refreshing list manga");
        List<Entry>[] list=new List[]{new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()};
        String[]tempxmls=getList(user,false).split("</manga>");
        for(int a=0;a<tempxmls.length-1;a++){
            list[nums.indexOf(Integer.parseInt(Entry.findTagValue("my_status",tempxmls[a])))].add(new Manga(tempxmls[a]));
        }
        return list;
    }

    public static List<Entry>[][] getEntries(String user){
        return new List[][]{getEntriesAnime(user),getEntriesManga(user)};
    }

    private static void getListAsync(final String user, final boolean anime, final boolean isownuser,final View view, final Context context,final int actual,final SwipeRefreshLayout refresh){
        new DownloadURL(new DownloadURL.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                new AsyncSplitter(new AsyncSplitter.AsyncResponse() {
                    @Override
                    public void processFinish(List<Entry>[] output) {
                        if(isownuser)EntryList.getOwnlist()[anime?0:1]=output;
                        else EntryList.getActualList()[anime?0:1]=output;
                        if(context!=null) {
                            ViewLoader.loadAdapterView(view, anime ? new AnimeAdapter(context, actual) : new MangaAdapter(context, actual), new LinearLayoutManager(context));
                            refresh.setRefreshing(false);
                        }
                    }
                },anime).execute(output);
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,("https://myanimelist.net/malappinfo.php?u="+user+"&status=all&type="+(anime?"anime":"manga")));
    }
    private static void getListAsync(final String user,final boolean anime,final boolean isownuser){
        getListAsync(user,anime,isownuser,null,null,-1,null);
     }
    public static void getListAsync(String user, boolean isownuser, View view, Context context, int actual, SwipeRefreshLayout refresh){
        refresh.setRefreshing(true);
        boolean oks[]={Settings.isAnime(),!Settings.isAnime()};
        getListAsync(user,oks[0],isownuser,view,context,actual,refresh);
        getListAsync(user,oks[1],isownuser);
    }
    public static String getList(String user, boolean anime){
        try {
            return new DownloadURL(new DownloadURL.AsyncResponse() {
                @Override
                public void processFinish(String output) {

                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,("https://myanimelist.net/malappinfo.php?u="+user+"&status=all&type="+(anime?"anime":"manga"))).get();
        } catch (InterruptedException|ExecutionException e) {
            Log.e("OnMALError","Error getting list: "+e.getMessage());
        }
        return "";
    }
    public static Tuple2<Integer,Integer> getIdIndex(List<Entry>en[], int id){
        for(int a=0;a<5;a++)for(int b=0;b<en[a].size();b++)if(en[a].get(b).getID()==id)return new Tuple2<>(a,b);
        return null;
    }
    public static List<String> getCacheFile(){
        String pathImg= Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache";
        File cacheFolder=new File(pathImg);
        cacheFolder.mkdir();
        File[] temp=cacheFolder.listFiles();
        List<String> files= new ArrayList<>();
        if(temp!=null)for(int a=0;a<temp.length;a++)files.add(temp[a].getName());
        return files;
    }
}
