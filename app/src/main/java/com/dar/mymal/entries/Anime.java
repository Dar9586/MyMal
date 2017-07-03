package com.dar.mymal.entries;

import java.text.SimpleDateFormat;

/**
 * Created by atopp on 28/06/2017.
 */
public class Anime extends Entry{
    int totEpisode,watched;
    public Anime(String xml){
        super(xml);
        id=Integer.parseInt(findTagValue("series_animedb_id"));
        rewatch=findTagValue("my_rewatching")!="0";
        totEpisode=Integer.parseInt(findTagValue("series_episodes"));
        watched=Integer.parseInt(findTagValue("my_watched_episodes"));
    }


    public int     getEpisodes   (){return totEpisode;}
    public int     getMyEpisodes (){return watched;}

    @Override
    public String toString() {
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        return String.format("{ID:%d, Title:%s, Synonyms:%s, Type:%d, Episodes:%d, Status:%d, Start:%s, Finish:%s, ImageURL:%s, MyEpisodes:%d, MyStart:%s, MyFinish:%s, Score:%d, Status:%d, Rewatch:%b}",id,title,synonyms,type,totEpisode,status,dtf.format(start),dtf.format(finish),imageURL,watched,dtf.format(mystart),dtf.format(myfinish),score,mystatus,rewatch);
    }
}
