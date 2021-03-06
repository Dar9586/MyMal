package com.dar.mymal.entries.inspector;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.dar.mymal.ViewLoader;
import com.dar.mymal.tuple.Tuple2;
import com.dar.mymal.tuple.Tuple3;
import com.dar.mymal.downloader.DownloadURL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by atopp on 07/07/2017.
 */

public class IDInspector {
    private int id;
    private boolean anime;
    private List<Tuple2<String,List<Tuple3<Integer,Boolean,String>>>> rel;
    private Tuple2<String,List<Tuple2<String,String>>>[] info;
    private String[][]songs={{},{}};
    private String html,url,imageURL,description,title;
    private DownloadURL downloadURL;
    private View views[]=new View[8];
    private Context context;

    public IDInspector(int id, boolean anime,Context context,View views[]){
        this.context=context;
        this.views=views;
        this.id=id;
        this.anime=anime;
        url="https://myanimelist.net/"+(anime?"anime":"manga")+"/"+id;
        mainLoad();
    }
    public IDInspector(int id, boolean anime){
        this(id,anime,null,null);
    }
    public IDInspector(String url){
        this.url=url;
        Log.d("OnMALDebug",url.substring(24,29)+", "+url.substring(30,url.indexOf('/',31)));
        this.anime=url.substring(24,29).equals("anime");
        this.id=Integer.parseInt(url.substring(30,url.indexOf('/',31)));
        mainLoad();
    }
    private void mainLoad(){
        Log.i("OnMALInfo","Inspecting: "+url);
        downloadURL=new DownloadURL(new DownloadURL.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                html=output;
                loadTitle();
                loadImageURL();
                loadDescription();
                if(context!=null)ViewLoader.loadViewDescription(views[0],LayoutInflater.from(context),getDescription());
                loadInfo();
                if(context!=null)ViewLoader.loadViewInfos(views[1],LayoutInflater.from(context),getInfo());
                loadRelation();
                if(context!=null)ViewLoader.loadViewRelations(views[2],LayoutInflater.from(context),getRel());
                if(anime)loadSongs();
                if(context!=null&&anime)ViewLoader.loadViewSong(views[3],LayoutInflater.from(context),songs);

            }
        });
        downloadURL.execute(url);
    }

    private void loadTitle() {
        String h=html.substring(html.indexOf("itemprop=\"name\"")+16);
        title=h.substring(0,h.indexOf('<'));
    }


    public DownloadURL getDownloadURL() {
        return downloadURL;
    }

    public String getTitle() {
        return title;
    }


    private void loadSongs(){
        int pos=html.indexOf("theme-songs js-theme-songs opnening")+37;
        songs[0]=html.substring(pos,html.indexOf("</div></div>",pos)).replace("<br>","\n").replaceAll("<[^>]*>","").split("\n");
        pos=html.indexOf("theme-songs js-theme-songs ending")+35;
        songs[1]=html.substring(pos,html.indexOf("</div></div>",pos)).replace("<br>","\n").replaceAll("<[^>]*>","").split("\n");
    }

    void loadRelation(){
        List<Tuple2<String,List<Tuple3<Integer,Boolean,String>>>> fin=new ArrayList<>();
        String str=html;
        str=str.substring(str.indexOf("anime_detail_related_anime"),str.indexOf("detail-characters-list"));
        List<Tuple2<String,Integer>>si=new ArrayList<>();
        int lastI=0;
        while(lastI<str.indexOf("nowrap=\"\"",lastI)){
            lastI=str.indexOf("nowrap=\"\"",lastI)+6;
            si.add(new Tuple2<>(Html.fromHtml(str.substring(str.indexOf('>',lastI)+1,str.indexOf('<',lastI)-1)).toString(),lastI));}
        List<Tuple3<Integer,Boolean,String>>g;
        for(int a=0;a<si.size();a++){
            String sub=str.substring(si.get(a).getB(),(a+1)==si.size()?str.length():si.get(a+1).getB());
            lastI=0;
            g=new ArrayList<>();
            while(lastI<sub.indexOf("href",lastI)){
                lastI=sub.indexOf("href",lastI)+7;
                String ss=sub.substring(lastI,sub.indexOf('<',lastI));
                if(Integer.parseInt(ss.substring(6,ss.indexOf('/',6)))!=id)
                    g.add(new Tuple3<>(Integer.parseInt(ss.substring(6,ss.indexOf('/',6))),ss.charAt(0)=='a',Html.fromHtml(ss.substring(ss.indexOf('>')+1)).toString()));
            }
            fin.add(new Tuple2(si.get(a).getA(),g));
        }
        rel=fin;
    }

    void loadInfo() {
        String str="";
        try{
        str=html.substring(html.indexOf(anime?"icon-block mt8":"icon-facebook")+16,html.indexOf("clearfix mauto mt16")-12);}catch(RuntimeException e){}
        String titles[]={"Alternative Titles","Information","Statistics"};
        Tuple2<String,List<Tuple2<String,String>>>[]fin=new Tuple2[]{new Tuple2(titles[0],new ArrayList<>()),new Tuple2(titles[1],new ArrayList<>()),new Tuple2(titles[2],new ArrayList<>())};
        str=str.replaceAll("<[^>]*>","");
        for(int a=0;a<titles.length;a++)str=str.replace(titles[a],"");
        List<String> values=new ArrayList<>();
        values.addAll(Arrays.asList(new String[]{"English","Synonyms","Japanese","Type","Episodes","Volumes","Chapters","Status",
                "Serialization","Aired","Authors","Premiered","Published","Broadcast","Producers",
                "Licensors","Studios","Source","Genres","Duration","Rating","Score","Ranked","Popularity","Members","Favorites"}));
        List<Tuple2<Integer,String>>jj=new ArrayList<>();
        for(int a=0;a<values.size();a++)if(str.contains(values.get(a)))jj.add(new Tuple2(str.indexOf(values.get(a)),values.get(a)));
        Collections.sort(jj, new Comparator<Tuple2<Integer, String>>() {
            @Override
            public int compare(Tuple2<Integer, String> integerStringTuple2, Tuple2<Integer, String> t1) {
                return integerStringTuple2.getA()-t1.getA();
            }
        });
        int y=-1;
        for(int a=0;a<jj.size();a++)if(jj.get(a).getB()=="Type"){y=a;break;}
        for(int a=0;a<jj.size();a++){
            int where=a<y?0:a<jj.size()-5?1:2;
            int start=jj.get(a).getA()+jj.get(a).getB().length()+1;
            int end=(a+1)==jj.size()?str.length():jj.get(a+1).getA();
            String ooo=Html.fromHtml(str.substring(start,end).replace("  ","").trim()).toString();
            if(jj.get(a).getB().equals("Score"))ooo=ooo.substring(0,ooo.indexOf(')')+1);
            if(jj.get(a).getB().equals("Ranked"))ooo=ooo.substring(0,ooo.lastIndexOf('2'));
            Tuple2<String,String>op=new Tuple2(jj.get(a).getB(),ooo);
            fin[where].getB().add(op);
        }
        info=fin;
    }

    public void loadImageURL() {
        String str=html.substring(html.indexOf("og:image")+19);
        imageURL=str.substring(0,str.indexOf('"'));
    }
    public void loadDescription() {
        String str=html.substring(html.indexOf("og:description")+25);
        description= Html.fromHtml(str.substring(0,str.indexOf('"'))).toString();
    }

    public int getId() {return id;}

    public boolean isAnime() {return anime;}

    public List<Tuple2<String, List<Tuple3<Integer, Boolean, String>>>> getRel() {return rel;}

    public Tuple2<String, List<Tuple2<String, String>>>[] getInfo() {return info;}

    public String getUrl() {return url;}

    public String getImageURL() { return imageURL;}

    public String getDescription() {return description;}

    public String[][] getSongs() {
        return songs;
    }
/*
    * fin.get(i).getKey() title of the row
    * fin.get(i).getValue() get a list of tuple of boolean if is an anime,int for id and string for name
    * */
}
