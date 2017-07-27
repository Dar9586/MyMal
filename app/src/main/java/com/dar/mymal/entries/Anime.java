package com.dar.mymal.entries;

import com.dar.mymal.utils.MalAPI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by atopp on 28/06/2017.
 */
public class Anime extends Entry{
    int totEpisode,watched;
    public Anime(String xml){
        super(xml);
        isanime=true;
        id=Integer.parseInt(findTagValue("series_animedb_id"));
        rewatch=findTagValue("my_rewatching").equals("1");
        totEpisode=Integer.parseInt(findTagValue("series_episodes"));
        watched=Integer.parseInt(findTagValue("my_watched_episodes"));
    }


    public int     getEpisodes   (){return totEpisode;}
    public int     getMyEpisodes (){return watched;}

    public void    setMyEpisodes (int x){watched=x;}
    @Override
    public String toString() {
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        return String.format("{ID:%d, Title:%s, Synonyms:%s, Type:%d, Episodes:%d, Status:%d, Start:%s, Finish:%s, ImageURL:%s, MyEpisodes:%d, MyStart:%s, MyFinish:%s, Score:%d, MyStatus:%d, Rewatch:%b}",id,title,synonyms,type,totEpisode,status,dtf.format(start),dtf.format(finish),imageURL,watched,dtf.format(mystart),dtf.format(myfinish),score,mystatus,rewatch);
    }
}
