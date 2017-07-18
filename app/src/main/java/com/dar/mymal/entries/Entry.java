package com.dar.mymal.entries;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by atopp on 28/06/2017.
 */
public abstract class Entry {
    protected String title,synonyms,imageURL,XML,tags;
    protected int id,type,status,score,mystatus;
    protected boolean rewatch,isanime;
    protected Date start,finish,mystart,myfinish;
    protected String findTagValue(String tag){
        if(XML.contains('<'+tag+"/>"))return "";
        return XML.substring(XML.indexOf('<'+tag+'>')+tag.length()+2,XML.indexOf("</"+tag+'>'));
    }
    public static String findTagValue(String tag,String XML){
        if(XML.contains('<'+tag+"/>"))return "";
        return XML.substring(XML.indexOf('<'+tag+'>')+tag.length()+2,XML.indexOf("</"+tag+'>'));
    }
    public Entry(String xml){
        XML=xml;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            start = format.parse(findTagValue("series_start"));
            finish = format.parse(findTagValue("series_end"));
            mystart = format.parse(findTagValue("my_start_date"));
            myfinish = format.parse(findTagValue("my_finish_date"));
        } catch (ParseException e) {
            Log.e("OnMALError","Error parsing entry: "+e.getMessage());
        }
        title=findTagValue("series_title");
        tags=findTagValue("my_tags");
        synonyms=findTagValue("series_synonyms");
        imageURL=findTagValue("series_image");
        type=Integer.parseInt(findTagValue("series_type"));
        status=Integer.parseInt(findTagValue("series_status"));
        score=Integer.parseInt(findTagValue("my_score"));
        mystatus=Integer.parseInt(findTagValue("my_status"));
    }
    static String[][] statusName={{"Unknown","Airing","Finished","Not yet aired"},{"Unknown","Publishing","Finished","Not yet published"}};
    static String[][] typeName={{"Unknown","TV","OVA","Movie","Special","ONA","Music"},{"Unknown","Manga","Novel","One-shot","Doujinshi","Manhwa","Manhua","OEL"}};
    static String[][] myStatusName={{"Watching","Completed","On-Hold","Dropped","Plan to watch"},{"Reading","Completed","On-Hold","Dropped","Plan to read"}};
    public int     getID          ()         {return id;}
    public String  getTitle       ()         {return title;}
    public String  getSynonyms    ()         {return synonyms;}
    public String  getTags        ()         {return tags;}
    public int     getType        ()         {return type;}
    public int     getStatus      ()         {return status;}
    public Date    getStart       ()         {return start;}
    public String  getStart       (String f) {return new SimpleDateFormat(f).format(start);}
    public Date    getFinish      ()         {return finish;}
    public String  getFinish      (String f) {return new SimpleDateFormat(f).format(finish);}
    public String  getImageURL    ()         {return imageURL;}
    public Date    getMyStart     ()         {return mystart;}
    public String  getMyStart     (String f) {return new SimpleDateFormat(f).format(mystart);}
    public Date    getMyFinish    ()         {return myfinish;}
    public String  getMyFinish    (String f) {return new SimpleDateFormat(f).format(myfinish);}
    public int     getScore       ()         {return score;}
    public int     getMyStatus    ()         {return mystatus;}
    public int     getMyStatus    (boolean s){return (s&&mystatus==5)?6:mystatus;}
    public boolean getRewatch     ()         {return rewatch;}
    public boolean isAnime        ()         {return isanime;}

    public String  getType        (boolean s){return typeName[s?0:1][type];}
    public String  getStatus      (boolean s){return statusName[s?0:1][status];}
    //public String  getMyStatus    (boolean s){return myStatusName[s?0:1][mystatus];}
    public static String[]getStatusList  (boolean s){return statusName[s?0:1];}
    public static String[]getTypeList    (boolean s){return typeName[s?0:1];}
    public static String[]getMyStatusList(boolean s){return myStatusName[s?0:1];}


    public void    setMyStart     (Date    x) {mystart=x;}
    public void    setMyFinish    (Date    x) {myfinish=x;}
    public void    setScore       (int     x) {score=x;}
    public void    setTags        (String  x) {tags=x;}
    public void    setMyStatus    (int     x) {mystatus=x==6?5:x;}
    public void    setRewatch     (boolean x) {rewatch=x;}

    public static int findById(Entry[] s, int id){
        for(int a=0;a<s.length;a++)
            if(s[a].getID()==id)return a;
        return -1;
    }
}
