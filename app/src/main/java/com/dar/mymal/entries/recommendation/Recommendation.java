package com.dar.mymal.entries.recommendation;

import android.text.Html;
import android.util.Log;

import com.dar.mymal.entries.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stopp on 18/07/2017.
 */

public class Recommendation {
    String html,imageURL,title;
    int id,howMany;
    boolean isAnime=true;
    List<SingleRecommendation> rec=new ArrayList<>();

    public String getImageURL() {
        return imageURL;
    }

    public String getTitle() {
        return title;
    }

    public String getHtml() {
        return html;
    }

    public int getHowMany() {
        return howMany;
    }

    public int getId() {
        return id;
    }

    public List<SingleRecommendation> getRec() {
        return rec;
    }

    void setId(){
        String h=html.substring(html.indexOf("a href")+15);
        id=Integer.parseInt(h.substring(0,h.indexOf('/')));
        h=h.substring(h.indexOf("<strong>")+8);
        title= Html.fromHtml(h.substring(0,h.indexOf("</strong>"))).toString();
    }
    void setImageURL(){
        String h=html.substring(html.indexOf("data-src")+10);
        imageURL=h.substring(0,h.indexOf('"'));
    }
    void setHowMany(){howMany=rec.size();}

    void setRec() {
        int last=0;
        String user,text;
        while(true){
            last=html.indexOf("detail-user-recs-text",last);
            if(last==-1)break;
            last+=23;
            text=html.substring(last,html.indexOf("</div>",last))/*.replace("<br />","\n").replaceAll("<[^>]*>","")*/.replace("&nbsp;","").replace(">read more<","><");
            Log.d("OnMALDebug",text);
            last=html.indexOf("/profile/",last)+9;
            user=html.substring(last,html.indexOf('"',last));
            rec.add(new SingleRecommendation(Html.fromHtml(user).toString(),Html.fromHtml(text).toString()));
        }
    }

    public Recommendation(String html,boolean anime) {
        this.html = html;
        this.isAnime=anime;
        setId();
        setImageURL();
        setRec();
        setHowMany();
    }

    public boolean isAnime() {
        return isAnime;
    }

    @Override
    public String toString() {
        return "Recommendation{" +
                "id=" + id +
                ", howMany=" + howMany +
                ", rec=" + rec +
                ", imageURL=" + imageURL +
                '}';
    }

}
