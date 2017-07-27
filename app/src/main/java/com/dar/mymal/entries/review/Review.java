package com.dar.mymal.entries.review;

import android.text.Html;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by stopp on 18/07/2017.
 */

public class Review {
    String html,user,review,episodeSeen,imageURL;
    int helpful;
    Date date;
    List<Integer> scores=new ArrayList<>();

    void setUser() {
        user=Html.fromHtml(html.substring(html.indexOf("/profile/")+9,html.indexOf('"',html.indexOf("/profile/")+9))).toString();
    }

    void setReview() {
        review=html.substring(html.lastIndexOf("</table>"),html.lastIndexOf("<div id")).replace("\n","")/*.replace("<br />","\n").replaceAll("<[^>]*>","")*/.trim();
        review= Html.fromHtml(review).toString();
    }

    void setEpisodeSeen() {
        String h=html.substring(html.indexOf("lightLink"),html.indexOf("</div>",html.indexOf("lightLink")));
        h=h.substring(h.indexOf('>')+1).trim();
        episodeSeen=h.substring(0,h.length()-14);
    }

    void setImageURL() {
        String h=html.substring(html.indexOf("img src=\"")+9);
        imageURL=h.substring(0,h.indexOf('"'));

    }

    void setHelpful() {
        String h=html.substring(html.indexOf("<span"),html.indexOf("</span>",html.indexOf("<span")));
        helpful=Integer.parseInt(h.substring(h.indexOf('>')+1));
    }

    void setDate() {
        String h=html.substring(html.indexOf("div title"),html.indexOf("</div>",html.indexOf("div title")));
        DateFormat format = new SimpleDateFormat("'div title=\"'hh:mm aa'\">'MMM d, yyyy", Locale.US);
        try {
            date=format.parse(h);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setScores() {
        String h=html.substring(html.lastIndexOf("<table"),html.lastIndexOf("</table>")).replaceAll("<[^>]*>","");
        String k[]=h.split("          ");
        for(int a=0;a<k.length;a++){
            if(k[a].trim().length()>3)scores.add(Integer.parseInt(k[++a].trim()));
        }
    }

    public Review(String html) {
        this.html = html;
        //System.out.println(html);
        setDate();
        setEpisodeSeen();
        setHelpful();
        setImageURL();
        setReview();
        setScores();
        setUser();
    }


    public String getHtml() {
        return html;
    }

    public String getUser() {
        return user;
    }

    public String getReview() {
        return review;
    }

    public String getEpisodeSeen() {
        return episodeSeen;
    }

    public String getImageURL() {
        return imageURL;
    }

    public int getHelpful() {
        return helpful;
    }

    public Date getDate() {
        return date;
    }

    public List<Integer> getScores() {
        return scores;
    }

    @Override
    public String toString() {
        DateFormat hh=new SimpleDateFormat("dd/MM/yyyy-kk:mm");
        return "Review{" +
                "user='" + user + '\'' +
                ", review='" + review + '\'' +
                ", episodeSeen='" + episodeSeen + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", helpful=" + helpful +
                ", date=" + hh.format(date) +
                ", scores=" + scores +
                '}';
    }

}
