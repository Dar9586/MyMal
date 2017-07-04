package com.dar.mymal.utils;

import android.content.Context;
import android.util.Log;

import com.dar.mymal.entries.Anime;
import com.dar.mymal.entries.Entry;
import com.dar.mymal.entries.Manga;
import com.dar.mymal.utils.downloader.DownloadURLAuth;

import java.util.concurrent.ExecutionException;

import static com.dar.mymal.R.id.status;

/**
 * Created by atopp on 04/07/2017.
 */

public class MalAPI {
    public static void add(Context cont,Entry a){
        DownloadURLAuth c=new DownloadURLAuth(cont,LoginData.enco);
            if(a.isAnime())c.execute("https://myanimelist.net/api/animelist/add/"+a.getID()+".xml?data=%3C?xml%20version=%221.0%22%20encoding=%22UTF-8%22?%3E%3Centry%3E%3Cepisode%3E0%3C/episode%3E%3Cstatus%3E6%3C/status%3E%3Cscore%3E%3C/score%3E%3Cstorage_type%3E%3C/storage_type%3E%3Cstorage_value%3E%3C/storage_value%3E%3Ctimes_rewatched%3E%3C/times_rewatched%3E%3Crewatch_value%3E%3C/rewatch_value%3E%3Cdate_start%3E%3C/date_start%3E%3Cdate_finish%3E%3C/date_finish%3E%3Cpriority%3E%3C/priority%3E%3Cenable_discussion%3E%3C/enable_discussion%3E%3Cenable_rewatching%3E%3C/enable_rewatching%3E%3Ccomments%3E%3C/comments%3E%3Ctags%3E%3C/tags%3E%3C/entry%3E");
            else           c.execute("https://myanimelist.net/api/mangalist/add/"+a.getID()+".xml?data=%3C?xml%20version=%221.0%22%20encoding=%22UTF-8%22?%3E%3Centry%3E%3Cchapter%3E0%3C/chapter%3E%3Cvolume%3E0%3C/volume%3E%3Cstatus%3E6%3C/status%3E%3Cscore%3E%3C/score%3E%3Ctimes_reread%3E%3C/times_reread%3E%3Creread_value%3E%3C/reread_value%3E%3Cdate_start%3E%3C/date_start%3E%3Cdate_finish%3E%3C/date_finish%3E%3Cpriority%3E%3C/priority%3E%3Cenable_discussion%3E%3C/enable_discussion%3E%3Cenable_rereading%3E%3C/enable_rereading%3E%3Ccomments%3E%3C/comments%3E%3Cscan_group%3E%3C/scan_group%3E%3Ctags%3E%3C/tags%3E%3Cretail_volumes%3E%3C/retail_volumes%3E%3C/entry%3E");

         }
    public static void remove(Context cont,Entry a){
        DownloadURLAuth c=new DownloadURLAuth(cont,LoginData.enco);
            c.execute("https://myanimelist.net/api/"+(a.isAnime()?"animelist":"mangalist")+"/delete/"+a.getID()+".xml");

    }
    public static void update(Context cont, Entry a){
        DownloadURLAuth c=new DownloadURLAuth(cont,LoginData.enco);

            if(a.isAnime()){
                Anime b=(Anime)a;
                c.execute("https://myanimelist.net/api/animelist/update/"+b.getID()+".xml?data=%3C?xml%20version=%221.0%22%20encoding=%22UTF-8%22?%3E%3Centry%3E%3C" +
                        "episode%3E"+b.getMyEpisodes()+"%3C/episode%3E%3C" +
                        "status%3E"+b.getMyStatus()+"%3C/status%3E%3C" +
                        "score%3E"+b.getScore()+"%3C/score%3E%3C" +
                        "storage_type%3E%3C/storage_type%3E%3C" +
                        "storage_value%3E%3C/storage_value%3E%3C" +
                        "times_rewatched%3E%3C/times_rewatched%3E%3C" +
                        "rewatch_value%3E"+(b.getRewatch()?"1":"0")+"%3C/rewatch_value%3E%3C" +
                        "date_start%3E"+b.getMyStart("MMddyyyy")+"%3C/date_start%3E%3C" +
                        "date_finish%3E"+b.getMyFinish("MMddyyyy")+"%3C/date_finish%3E%3C" +
                        "priority%3E%3C/priority%3E%3C" +
                        "enable_discussion%3E%3C/enable_discussion%3E%3C" +
                        "enable_rewatching%3E%3C/enable_rewatching%3E%3C" +
                        "comments%3E%3C/comments%3E%3C" +
                        "tags%3E%3C/tags%3E%3C/entry%3E");
            }
            else{
                Manga b=(Manga)a;
                c.execute("https://myanimelist.net/api/mangalist/update/"+b.getID()+".xml?data=%3C?xml%20version=%221.0%22%20encoding=%22UTF-8%22?%3E"+
                        "%3Centry%3E"+
                        "%3Cchapter%3E"+b.getMyChapter()+"%3C/chapter%3E"+
                        "%3Cvolume%3E"+b.getMyVolumes()+"%3C/volume%3E"+
                        "%3Cstatus%3E"+b.getMyStatus()+"%3C/status%3E"+
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
                        "%3Ctags%3E%3C/tags%3E"+
                        "%3Cretail_volumes%3E%3C/retail_volumes%3E"+
                        "%3C/entry%3E");


        }
    }
}
