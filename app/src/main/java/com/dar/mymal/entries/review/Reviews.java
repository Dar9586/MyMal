package com.dar.mymal.entries.review;

import android.util.Log;

import com.dar.mymal.downloader.DownloadURL;
import com.dar.mymal.entries.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by stopp on 18/07/2017.
 */

public class Reviews {

    boolean anime;
    int id;
    String url,html;
    List<Review> rew=new ArrayList<>();
    void getHtml() {
        try {
            html= new DownloadURL().execute(url).get();
        } catch (InterruptedException|ExecutionException e) {
            Log.e("OnMALError","Error load Reviews for "+(anime?"anime":"manga")+" "+id+", "+e.getMessage());
        }
    }
    public Reviews(int id,boolean anime) {
        this.anime = anime;
        this.id = id;
        url=String.format("https://myanimelist.net/%s/%d/reviews/reviews",anime?"anime":"manga",id);
        getHtml();
        int last=0;
        //rew.add(new Review(html.substring(last,html.indexOf("button_form",last))));
        while(true){
            last=html.indexOf("borderDark",last+1);
            if(last==-1)break;
            rew.add(new Review(html.substring(last,html.indexOf("button_form",last))));
            System.out.println(rew.get(rew.size()-1).toString());
        }
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

    public List<Review> getReviews() {
        return rew;
    }

    @Override
    public String toString() {
        return "Reviews{" +
                "anime=" + anime +
                ", id=" + id +
                ", url='" + url + '\'' +
                ", html='" + html + '\'' +
                ", rew=" + rew +
                '}';
    }

}
