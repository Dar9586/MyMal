package com.dar.mymal.entries;

import com.dar.mymal.utils.MalAPI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by atopp on 28/06/2017.
 */
public class Manga extends Entry {

    int totChapter,totVolume,myChapter,myVolume;
    public Manga(String xml){
        super(xml);
        isanime=false;
        id=Integer.parseInt(findTagValue("series_mangadb_id"));
        rewatch=findTagValue("my_rereadingg").equals("1");
        totChapter=Integer.parseInt(findTagValue("series_chapters"));
        totVolume=Integer.parseInt(findTagValue("series_volumes"));
        myChapter=Integer.parseInt(findTagValue("my_read_chapters"));
        myVolume=Integer.parseInt(findTagValue("my_read_volumes"));
    }
    public int     getChapter   (){return totChapter;}
    public int     getVolumes   (){return totVolume;}
    public int     getMyChapter (){return myChapter;}
    public int     getMyVolumes (){return myVolume;}

    public void    setMyChapter (int x){myChapter=x;}
    public void    setMyVolumes (int x){myVolume=x;}

    @Override
    public String toString() {
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        return String.format("{ID:%d, Title:%s, Synonyms:%s, Type:%d, Chapters:%d, Volumes:%d, Status:%d, Start:%s, Finish:%s, ImageURL:%s, MyChapter:%d, MyVolume:%d, MyStart:%s, MyFinish:%s, Score:%d, MyStatus:%d, Rewatch:%b}",id,title,synonyms,type,totChapter,totVolume,status,start==null?"null":dtf.format(start),finish==null?"null":dtf.format(finish),imageURL,myChapter,myVolume,mystart==null?"null":dtf.format(mystart),myfinish==null?"null":dtf.format(myfinish),score,mystatus,rewatch);
    }
}
