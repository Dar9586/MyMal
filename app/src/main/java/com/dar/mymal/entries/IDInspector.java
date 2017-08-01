package com.dar.mymal.entries;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.dar.mymal.entries.recommendation.Recommendations;
import com.dar.mymal.entries.review.Reviews;
import com.dar.mymal.tuple.Tuple2;
import com.dar.mymal.tuple.Tuple3;
import com.dar.mymal.downloader.DownloadURL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by atopp on 07/07/2017.
 */

public class IDInspector {
    int id;
    boolean anime;
    List<Tuple2<String,List<Tuple3<Integer,Boolean,String>>>> rel;
    Tuple2<String,List<Tuple2<String,String>>>[] info;
    String[][]songs={{},{}};
    String html,url,imageURL,description;
    Reviews rew;
    Recommendations rec;
    LoadReviews async;
    Characters characters;
    public IDInspector(int id,boolean anime){
        this.id=id;
        this.anime=anime;
        url="https://myanimelist.net/"+(anime?"anime":"manga")+"/"+id;
        mainLoad();
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
        try {
            html = new DownloadURL().execute(url).get();
        }catch (InterruptedException|ExecutionException e){
            Log.e("OnMALError",e.getMessage());}
        loadImageURL();
        loadDescription();
        loadRelation();
        loadInfo();
        loadSongs();
        async=new LoadReviews(this);
        async.execute();
    }

    public Characters getCharacters() {
        return characters;
    }

    public LoadReviews getAsync() {
        return async;
    }

    public Reviews getRew() {
        return rew;
    }

    public Recommendations getRec() {
        return rec;
    }

    public class LoadReviews extends AsyncTask<Void, String, Void> {
        IDInspector l;
        int status;
        @Override
        protected void onPreExecute() {

        }

        public LoadReviews(IDInspector l) {
            this.l=l;
        }
        @Override
        protected void onPostExecute(Void x)
        {
        }

        public int getStat() {
            return status;
        }

        @Override
        protected Void doInBackground(Void... f_url) {
            Log.d("OnMALDebug","Starting load reviews");status=0;
            l.rew= new Reviews(id,anime);
            Log.d("OnMALDebug","Finish load reviews");status=1;
            Log.d("OnMALDebug","Starting load reccomendations");status=2;
            l.rec= new Recommendations(id,anime);
            Log.d("OnMALDebug","Finish load reccomendations");status=3;
            Log.d("OnMALDebug","Starting load characters");status=4;
            l.characters=new Characters(id,anime);
            Log.d("OnMALDebug","Finish load characters");status=5;
            return null;
        }

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
        //str=str.substring(str.indexOf("https"));
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
