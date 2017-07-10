package com.dar.mymal.entries;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by atopp on 07/07/2017.
 */

public class SearchEntry {
    protected String title,english,synonyms,synopsis,image,type,status,XML;
    protected int id,episodes;
    protected  boolean isanime;
    public String getTitle() {return title;}

    public String getSynonyms() {return synonyms;}

    public String getSynopsis() {return synopsis;}

    public String getImage() {return image;}

    public String getType() {return type;}

    public String getStatus() {return status;}

    public int getId() {return id;}

    public int getEpisodes() {return episodes;}

    public float getScore() {return score;}

    public Date getStart() {return start;}

    public Date getFinish() {return finish;}
    public boolean isAnime(){return isanime;}

    protected float score;
    protected Date start,finish;

    public String getEnglish() {
        return english;
    }

    protected String findTagValue(String tag){
        if(XML.contains('<'+tag+"/>"))return "";
        return XML.substring(XML.indexOf('<'+tag+'>')+tag.length()+2,XML.indexOf("</"+tag+'>'));
    }

    public SearchEntry(String xml,boolean anime){
        XML=xml;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            start = format.parse(findTagValue("start_date"));
            finish = format.parse(findTagValue("end_date"));
        } catch (ParseException e) {
            e.getMessage();
        }
        isanime=anime;
        title=findTagValue("title");
        type=findTagValue("type");
        english=findTagValue("english");
        synonyms=findTagValue("synonyms");
        synopsis=findTagValue("synopsis");
        image=findTagValue("image");
        status=findTagValue("status");
        score=Float.parseFloat(findTagValue("score"));
        id = Integer.parseInt(findTagValue("id"));
        episodes=Integer.parseInt(findTagValue(isanime?"episodes":"chapters"));
    }

    @Override
    public String toString() {
        return "SearchEntry{" +
                "title='" + title + '\'' +
                ", english='" + english + '\'' +
                ", synonyms='" + synonyms + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", image='" + image + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", XML='" + XML + '\'' +
                ", id=" + id +
                ", episodes=" + episodes +
                ", isanime=" + isanime +
                ", score=" + score +
                ", start=" + start +
                ", finish=" + finish +
                '}';
    }
}
