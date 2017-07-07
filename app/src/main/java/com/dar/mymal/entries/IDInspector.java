package com.dar.mymal.entries;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.dar.mymal.tuple.Tuple2;
import com.dar.mymal.tuple.Tuple3;
import com.dar.mymal.utils.downloader.DownloadURL;

import java.util.ArrayList;
import java.util.Arrays;
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
    String html,url,imageURL,description;
    public IDInspector(int id,boolean anime){
        this.id=id;
        this.anime=anime;
        url="https://myanimelist.net/"+(anime?"anime":"manga")+"/"+id;
        Log.e("ENTRYURL",url);
        try {
            html = new DownloadURL().execute(url).get();
        }catch (InterruptedException|ExecutionException e){
            Log.e("EntryCrash",e.getMessage());}
        loadImageURL();
        loadDescription();
        loadRelation();
        loadInfo();
    }


    void loadRelation(){
        List<Tuple2<String,List<Tuple3<Integer,Boolean,String>>>> fin=new ArrayList<>();
        String str=html;
        str=str.substring(str.indexOf("anime_detail_related_anime"),str.indexOf("detail-characters-list"));
        System.out.println(str);
        List<Tuple2<String,Integer>>si=new ArrayList<>();
        int lastI=0;
        while(lastI<str.indexOf("nowrap=\"\"",lastI)){
            lastI=str.indexOf("nowrap=\"\"",lastI)+6;
            si.add(new Tuple2<>(str.substring(str.indexOf('>',lastI)+1,str.indexOf('<',lastI)-1),lastI));}
        List<Tuple3<Integer,Boolean,String>>g;
        for(int a=0;a<si.size();a++){
            String sub=str.substring(si.get(a).getB(),(a+1)==si.size()?str.length():si.get(a+1).getB());
            lastI=0;
            g=new ArrayList<>();
            while(lastI<sub.indexOf("href",lastI)){
                lastI=sub.indexOf("href",lastI)+7;
                String ss=sub.substring(lastI,sub.indexOf('<',lastI));
                if(Integer.parseInt(ss.substring(6,ss.indexOf('/',6)))!=id)
                    g.add(new Tuple3<>(Integer.parseInt(ss.substring(6,ss.indexOf('/',6))),ss.charAt(0)=='a',ss.substring(ss.indexOf('>')+1)));
            }
            fin.add(new Tuple2(si.get(a).getA(),g));
        }
        System.out.println(fin.toString());
        rel=fin;
    }

    void loadInfo() {
        String str="";
        try{
Log.w("SSDASSA",Integer.toString(html.indexOf("icon-block mt8")));
        str=html.substring(html.indexOf("icon-block mt8")+16,html.indexOf("clearfix mauto mt16")-12).replace("1indicates a weighted score. Please note that 'Not yet aired' titles are excluded.","")
                .replace("2based on the top anime page. Please note that 'Not yet aired' and 'R18+' titles are excluded.","");}catch(RuntimeException e){}
        String titles[]={"Alternative Titles","Information","Statistics"};
        Tuple2<String,List<Tuple2<String,String>>>[]fin=new Tuple2[]{new Tuple2(titles[0],new ArrayList<>()),new Tuple2(titles[1],new ArrayList<>()),new Tuple2(titles[2],new ArrayList<>())};
        str=str.replaceAll("<[^>]*>","");
        Log.e("CIAONE",str);
        for(int a=0;a<titles.length;a++)str=str.replace(titles[a],"");
        List<String> values=new ArrayList<>();
        values.addAll(Arrays.asList(new String[]{"English","Synonyms","Japanese","Type","Episodes","Volumes","Chapters","Status",
                "Serialization","Aired","Authors","Premiered","Published","Broadcast","Producers",
                "Licensors","Studios","Source","Genres","Duration","Rating","Score","Ranked","Popularity","Members","Favorites"}));
        for(int a=0;a<values.size();a++)if(!str.contains(values.get(a)))values.remove(a--);
        int y=values.indexOf("Type");
        for(int a=0;a<values.size();a++){
            fin[a<y?0:a<values.size()-5?1:2].getB().add(new Tuple2(values.get(a),str.substring(str.indexOf(values.get(a))
                    +values.get(a).length()+1,(a+1)==values.size()?str.length():
                    str.indexOf(values.get(a+1))).replace("  ","").trim()));
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

    /*
    * fin.get(i).getKey() title of the row
    * fin.get(i).getValue() get a list of tuple of boolean if is an anime,int for id and string for name
    * */
}
