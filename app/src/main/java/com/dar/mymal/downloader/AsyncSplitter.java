package com.dar.mymal.downloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dar.mymal.entries.api.Anime;
import com.dar.mymal.entries.api.Entry;
import com.dar.mymal.entries.api.Manga;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by atopp on 14/08/2017.
 */

public class AsyncSplitter extends AsyncTask<String, String, List<Entry>[]> {
    boolean anime;
    public interface AsyncResponse {
        void processFinish(List<Entry>[] output);
    }

    public AsyncSplitter.AsyncResponse delegate = null;

    @Override
    protected void onPreExecute() {

    }
    public AsyncSplitter(AsyncResponse delegate,boolean anime) {
        this.anime = anime;
        this.delegate = delegate;
    }

    @Override
    protected void onPostExecute(List<Entry>[] x){
        delegate.processFinish(x);
    }
    @Override
    protected List<Entry>[] doInBackground(String... output) {
        List<Entry>[] list=new List[]{new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()};
        List<Integer>nums=new ArrayList<>(Arrays.asList(1,2,3,4,6));
        String[]tempxmls=output[0].split(anime?"</anime>":"</manga>");
        for(int a=0;a<tempxmls.length-1;a++){
            list[nums.indexOf(Integer.parseInt(Entry.findTagValue("my_status",tempxmls[a])))].add(anime?new Anime(tempxmls[a]):new Manga(tempxmls[a]));
        }
        return list;
    }
}
