package com.dar.mymal.entries.searches;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.dar.mymal.ViewLoader;
import com.dar.mymal.adapters.seachEntry.PeopleAdapter;
import com.dar.mymal.downloader.DownloadURL;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by atopp on 09/08/2017.
 */
public class People {
    String html,query,url;
    int page;
    List<PeopleSearchEntry>peoples=new ArrayList<>();
    DownloadURL async;
    View view;
    Context context;

    public DownloadURL getAsync() {
        return async;
    }

    public String getQuery() {
        return query;
    }

    public String getUrl() {
        return url;
    }

    public int getPage() {
        return page;
    }

    public List<PeopleSearchEntry> getPeoples() {
        return peoples;
    }
    public People(String query, int page, View view,Context context) {
        this.view=view;
        this.context=context;
        this.query = query;
        this.page=page;
        this.url=String.format("https://myanimelist.net/people.php?q=%s&show=%d",query.replace(' ','+'),page*50);
        downloadURL();
    }
    public People(String query, int page) {
        this(query,page,null,null);
    }
    void splitHTML(){
        if(html.contains("<td class=\"borderClass\">No results returned</td>"))return;
        String[] ent=html.split("<tr>");
        for(int a=0;a<ent.length;a++)
            peoples.add(new PeopleSearchEntry(ent[a]));
        if(view!=null) ViewLoader.loadAdapterView(view,new PeopleAdapter(context,peoples),new LinearLayoutManager(context));
    }
    void downloadURL(){
        //System.out.println(url);
        async=new DownloadURL(new DownloadURL.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                html=output;
                    try {
                        if(html.substring(0,500).contains("<title>Search People - MyAnimeList.net")) {
                        html = html.substring(html.indexOf("class=\"normal_header\""));
                        html = html.substring(0, html.indexOf("</table"));
                        html = html.substring(html.indexOf("<tr>") + 4);
                        splitHTML();
                        }else if(context!=null){ViewLoader.loadAdapterView(view,null,new LinearLayoutManager(context));}
                    } catch (StringIndexOutOfBoundsException e) {
                        Log.e("OnMALError", "Unable to find people with name: " + query);
                    }

            }
        });
        async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);

    }

    public class PeopleSearchEntry{
        String imageURL,name,code;
        int id;

        public PeopleSearchEntry(String code) {
            this.code = code;
            loadName();
            loadImageURL();
            loadID();
        }
        void loadImageURL(){
            String h=code.substring(code.indexOf("<img")+10);
            imageURL=h.substring(0,h.indexOf('"'));
        }
        void loadName(){
            String h=code.substring(code.lastIndexOf("<a href"));
            h=h.substring(h.indexOf('>')+1);
            name=h.substring(0,h.indexOf('<'));
        }
        void loadID(){
            String h=code.substring(code.indexOf("a href")+16);
            id=Integer.parseInt(h.substring(0,h.indexOf('/')));
        }

        public String getImageURL() {
            return imageURL;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "PeopleSearchEntry{" +
                    "imageURL='" + imageURL + '\'' +
                    ", name='" + name + '\'' +
                    ", id=" + id +
                    '}';
        }
    }
}
