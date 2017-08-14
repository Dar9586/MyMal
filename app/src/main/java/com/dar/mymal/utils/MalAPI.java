package com.dar.mymal.utils;

import android.content.Context;
import android.util.Log;

import com.dar.mymal.entries.api.Anime;
import com.dar.mymal.entries.api.Entry;
import com.dar.mymal.entries.api.Manga;
import com.dar.mymal.entries.SearchEntry;
import com.dar.mymal.downloader.DownloadURL;
import com.dar.mymal.global.EntryList;
import com.dar.mymal.global.LoginData;
import com.dar.mymal.tuple.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by atopp on 04/07/2017.
 */

public class MalAPI {
    public static void add(final int id,final boolean anime){
        Log.i("OnMALInfo","Adding :"+id+","+anime);
        DownloadURL c=new DownloadURL(new DownloadURL.AsyncResponse() {
            @Override
            public void processFinish(String output) {

                }
        },LoginData.getEncoded());
        String kk="";
        try{
            if (anime)
                kk=c.execute("https://myanimelist.net/api/animelist/add/" + id + ".xml?data=%3C?xml%20version=%221.0%22%20encoding=%22UTF-8%22?%3E%3Centry%3E%3Cepisode%3E0%3C/episode%3E%3Cstatus%3E6%3C/status%3E%3Cscore%3E%3C/score%3E%3Cstorage_type%3E%3C/storage_type%3E%3Cstorage_value%3E%3C/storage_value%3E%3Ctimes_rewatched%3E%3C/times_rewatched%3E%3Crewatch_value%3E%3C/rewatch_value%3E%3Cdate_start%3E%3C/date_start%3E%3Cdate_finish%3E%3C/date_finish%3E%3Cpriority%3E%3C/priority%3E%3Cenable_discussion%3E%3C/enable_discussion%3E%3Cenable_rewatching%3E%3C/enable_rewatching%3E%3Ccomments%3E%3C/comments%3E%3Ctags%3E%3C/tags%3E%3C/entry%3E").get();
            else
                kk=c.execute("https://myanimelist.net/api/mangalist/add/" + id + ".xml?data=%3C?xml%20version=%221.0%22%20encoding=%22UTF-8%22?%3E%3Centry%3E%3Cchapter%3E0%3C/chapter%3E%3Cvolume%3E0%3C/volume%3E%3Cstatus%3E6%3C/status%3E%3Cscore%3E%3C/score%3E%3Ctimes_reread%3E%3C/times_reread%3E%3Creread_value%3E%3C/reread_value%3E%3Cdate_start%3E%3C/date_start%3E%3Cdate_finish%3E%3C/date_finish%3E%3Cpriority%3E%3C/priority%3E%3Cenable_discussion%3E%3C/enable_discussion%3E%3Cenable_rereading%3E%3C/enable_rereading%3E%3Ccomments%3E%3C/comments%3E%3Cscan_group%3E%3C/scan_group%3E%3Ctags%3E%3C/tags%3E%3Cretail_volumes%3E%3C/retail_volumes%3E%3C/entry%3E").get();
        }catch(InterruptedException|ExecutionException e){Log.e("OnMALError","Search error: "+e.getMessage());}
            Log.i("OnMALInfo","Response: "+kk);
        String h=MALUtils.getList(EntryList.getOwnUser(),anime);
        EntryList.getOwnlist()[anime?0:1][4].add(
                anime?new Anime(h.substring(h.indexOf("<series_animedb_id>"+id),h.indexOf("</anime>",h.indexOf("<series_animedb_id>"+id)))):
                        new Manga(h.substring(h.indexOf("<series_mangadb_id>"+id),h.indexOf("</manga>",h.indexOf("<series_mangadb_id>"+id))))
        );
    }
    public static void remove(final int id,final boolean anime){
        Log.i("OnMALInfo","Removing :"+id+","+anime);
        DownloadURL c=new DownloadURL(new DownloadURL.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.i("OnMALInfo","Response: "+output);
                Tuple2<Integer,Integer> where=MALUtils.getIdIndex(EntryList.getOwnlist()[anime?0:1],id);
                try {
                    EntryList.getOwnlist()[anime ? 0 : 1][where.getA()].remove(where.getB().intValue());
                }catch(NullPointerException e){Log.e("OnMALError","Anime don't found");}
            }
        },LoginData.getEncoded());
        c.execute("https://myanimelist.net/api/" + (anime ? "animelist" : "mangalist") + "/delete/" + id + ".xml");

    }

    public static void update(final Entry a){
        Log.i("OnMALInfo","Updating :"+a.getID()+","+a.isAnime());
        DownloadURL c=new DownloadURL(new DownloadURL.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.i("OnMALInfo","Response: "+output);
            }
        },LoginData.getEncoded());
            if(a.isAnime()){
                Anime b=(Anime)a;
                c.execute("https://myanimelist.net/api/animelist/update/"+b.getID()+".xml?data=%3C?xml%20version=%221.0%22%20encoding=%22UTF-8%22?%3E%3Centry%3E%3C" +
                        "episode%3E"+b.getMyEpisodes()+"%3C/episode%3E%3C" +
                        "status%3E"+b.getMyStatus(true)+"%3C/status%3E%3C" +
                        "score%3E"+b.getScore()+"%3C/score%3E%3C" +
                        "storage_type%3E%3C/storage_type%3E%3C" +
                        "storage_value%3E%3C/storage_value%3E%3C" +
                        "times_rewatched%3E%3C/times_rewatched%3E%3C" +
                        "rewatch_value%3E%3C/rewatch_value%3E%3C" +
                        "date_start%3E"+b.getMyStart("MMddyyyy")+"%3C/date_start%3E%3C" +
                        "date_finish%3E"+b.getMyFinish("MMddyyyy")+"%3C/date_finish%3E%3C" +
                        "priority%3E%3C/priority%3E%3C" +
                        "enable_discussion%3E%3C/enable_discussion%3E%3C" +
                        "enable_rewatching%3E"+(b.getRewatch()?"1":"0")+"%3C/enable_rewatching%3E%3C" +
                        "comments%3E%3C/comments%3E%3C" +
                        "tags%3E"+b.getTags()+"%3C/tags%3E%3C/entry%3E");
            }
            else{
                Manga b=(Manga)a;
                c.execute("https://myanimelist.net/api/mangalist/update/"+b.getID()+".xml?data=%3C?xml%20version=%221.0%22%20encoding=%22UTF-8%22?%3E"+
                        "%3Centry%3E"+
                        "%3Cchapter%3E"+b.getMyChapter()+"%3C/chapter%3E"+
                        "%3Cvolume%3E"+b.getMyVolumes()+"%3C/volume%3E"+
                        "%3Cstatus%3E"+b.getMyStatus(true)+"%3C/status%3E"+
                        "%3Cscore%3E"+b.getScore()+"%3C/score%3E"+
                        "%3Ctimes_reread%3E%3C/times_reread%3E"+
                        "%3Creread_value%3E%3C/reread_value%3E"+
                        "%3Cdate_start%3E"+b.getMyStart("MMddyyyy")+"%3C/date_start%3E"+
                        "%3Cdate_finish%3E"+b.getMyFinish("MMddyyyy")+"%3C/date_finish%3E"+
                        "%3Cpriority%3E%3C/priority%3E"+
                        "%3Cenable_discussion%3E%3C/enable_discussion%3E"+
                        "%3Cenable_rereading%3E"+(b.getRewatch()?"1":"0")+"%3C/enable_rereading%3E"+
                        "%3Ccomments%3E%3C/comments%3E"+
                        "%3Cscan_group%3E%3C/scan_group%3E"+
                        "%3Ctags%3E"+b.getTags()+"%3C/tags%3E"+
                        "%3Cretail_volumes%3E%3C/retail_volumes%3E"+
                        "%3C/entry%3E");
            }
    }
    public static List<SearchEntry> search(Context cont, String s, boolean anime){
        DownloadURL c=new DownloadURL(new DownloadURL.AsyncResponse() {
            @Override
            public void processFinish(String output) {

            }
        },cont, LoginData.getEncoded());
        String h="";
        try {
            h = c.execute("https://myanimelist.net/api/" + (anime ? "anime" : "manga") + "/search.xml?q=" + s.replace(' ', '+')).get();
        }catch(InterruptedException|ExecutionException e){Log.e("OnMALError","Search error: "+e.getMessage());}
        String[]h1=h.split("</entry>");
        List<SearchEntry>sl=new ArrayList<>();
        for(int a=0;a<h1.length-1;a++){
            sl.add(new SearchEntry(h1[a],anime));
        }
        return sl;
    }
}
