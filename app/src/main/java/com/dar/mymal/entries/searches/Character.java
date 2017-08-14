package com.dar.mymal.entries.searches;


import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.dar.mymal.ViewLoader;
import com.dar.mymal.adapters.seachEntry.CharacterAdapter;
import com.dar.mymal.downloader.DownloadURL;
import com.dar.mymal.tuple.Tuple2;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by atopp on 09/08/2017.
 */
public class Character {
    String html,url,query;
    int page;
    List<CharacterSearchEntry>characters=new ArrayList<>();
    private DownloadURL async;
    View view;
    Context context;

    public String getUrl() {
        return url;
    }

    public String getQuery() {
        return query;
    }

    public int getPage() {
        return page;
    }

    public List<CharacterSearchEntry> getCharacters() {
        return characters;
    }

    public DownloadURL getAsync() {
        return async;
    }
    public Character(String query, int page,View view,Context context) {
        this.view=view;
        this.context=context;
        this.query = query;
        this.page = page;
        this.url=String.format("https://myanimelist.net/character.php?q=%s&show=%d",query.replace(' ','+'),page);
        downloadURL();
    }
    public Character(String query, int page) {
        this(query,page,null,null);
    }
    void splitHTML(){
        if(html.contains("<td class=\"borderClass\">No results found</td>"))return;
        String[] ent=html.split("<tr>");
        for(int a=0;a<ent.length;a++)
            characters.add(new CharacterSearchEntry(ent[a]));
        if(view!=null) ViewLoader.loadAdapterView(view,new CharacterAdapter(context,characters),new LinearLayoutManager(context));
    }
    void downloadURL() {
        async=new DownloadURL(new DownloadURL.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                html=output;
                try{
                html=html.substring(html.indexOf("class=\"normal_header\""));
                html=html.substring(0,html.indexOf("</table"));
                html=html.substring(html.indexOf("<tr>")+4);
                splitHTML();
                }catch(StringIndexOutOfBoundsException e){
                    Log.e("OnMALError","Unable to find character with name: "+query);}
            }
        });
        async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);

    }
    public class CharacterSearchEntry{
        int id;
        String imageURL,name,desc,code;
        List<Tuple2<Integer,String>>animes=new ArrayList<>();
        List<Tuple2<Integer,String>>mangas=new ArrayList<>();

        public CharacterSearchEntry(String code) {
            this.code = code;
            //System.out.println(code);
            loadID();
            loadImageURL();
            loadDesc();
            loadName();
            loadEntries();
            System.out.println(toString());
        }

        @Override
        public String toString() {
            return "CharacterSearchEntry{" +
                    "id=" + id +
                    ", imageURL='" + imageURL + '\'' +
                    ", name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    ", animes=" + animes +
                    ", mangas=" + mangas +
                    '}';
        }

        void loadID(){
            String h=code.substring(code.indexOf("<a href")+20);
            id=Integer.parseInt(h.substring(0,h.indexOf('/')));
        }
        void loadImageURL(){
            String h=code.substring(code.indexOf("<img")+10);
            imageURL=h.substring(0,h.indexOf('"'));
        }
        void loadEntries(){
            String h=code.substring(code.lastIndexOf("<small>")+8);
            h=h.substring(0,h.lastIndexOf("</small>")+8);
            boolean hasAnime=h.startsWith("Anime:");
            boolean hasManga=h.contains("<div>Manga:");
            if(hasAnime){
                String hh=h.substring(7,h.indexOf(hasManga?"<div>Manga:":"</small>"));
                String sp[]=hh.split("</a>");
                for(int a=0;a<sp.length;a++){
                    if(sp[a].length()>20)
                        animes.add(getEntry(sp[a]));
                }
            }
            if(hasManga){
                String hh=h.substring(h.indexOf("<div>Manga:")+12);
                String sp[]=hh.split("</a>");
                for(int a=0;a<sp.length;a++){
                    if(!sp[a].contains("/manga//")&&sp[a].length()>20)
                        mangas.add(getEntry(sp[a]));
                }
            }

        }
        Tuple2<Integer,String> getEntry(String text){
            String h = text.substring(text.indexOf("<a href") + 16);
            int id = Integer.parseInt(h.substring(0, h.indexOf('/')));
            h = h.substring(h.indexOf('>') + 1);
            return new Tuple2<>(id, h);
        }
        void loadDesc(){
            if(code.contains("<br />")) {
                String h = code.substring(code.indexOf("<small>") + 7);
                desc = h.substring(0, h.indexOf('<'));
            }
        }
        void loadName(){
            String h=code.substring(code.lastIndexOf(Integer.toString(id)));
            h=h.substring(h.indexOf('>')+1);
            name=h.substring(0,h.indexOf('<'));
        }
        public int getId() {
            return id;
        }

        public String getImageURL() {
            return imageURL;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }

        public List<Tuple2<Integer, String>> getAnimes() {
            return animes;
        }

        public List<Tuple2<Integer, String>> getMangas() {
            return mangas;
        }
    }
}
