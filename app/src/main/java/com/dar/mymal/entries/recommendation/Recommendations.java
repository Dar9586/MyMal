package com.dar.mymal.entries.recommendation;

import android.util.Log;

import com.dar.mymal.downloader.DownloadURL;
import com.dar.mymal.entries.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by stopp on 18/07/2017.
 */

public class Recommendations {

    boolean anime;
    int id;
    String url,html;
    List<Recommendation> rec=new ArrayList<>();
    void getHtml() {
        try {
            html= new DownloadURL().execute(url).get();
        } catch (InterruptedException|ExecutionException e) {
            Log.e("OnMALError","Error load RecommendationsActivity for "+(anime?"anime":"manga")+" "+id+", "+e.getMessage());
        }
        html=html.substring(html.indexOf("Make a recommendation"),html.lastIndexOf("</table>"));
    }

    public boolean isAnime() {
        return anime;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public List<Recommendation> getRec() {
        return rec;
    }

    public Recommendations(int id, boolean anime) {
        this.anime = anime;
        this.id = id;
        url=String.format("https://myanimelist.net/%s/%d/userrecs/userrecs",anime?"anime":"manga",id);
        getHtml();


        int last=0;
        while(true){
            last=html.indexOf("<table",last+1);
            if(last<0)break;
            rec.add(new Recommendation(html.substring(html.indexOf("<table",last),html.indexOf("</table",last))));
        }
    }

    @Override
    public String toString() {
        return "Reviews{" +
                "anime=" + anime +
                ", id=" + id +
                ", url='" + url + '\'' +
                ", html='" + html + '\'' +
                ", rew=" + rec +
                '}';
    }

}
