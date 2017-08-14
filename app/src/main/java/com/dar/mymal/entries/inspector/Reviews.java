package com.dar.mymal.entries.inspector;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.View;

import com.dar.mymal.ViewLoader;
import com.dar.mymal.adapters.dataEntry.ReviewAdapter;
import com.dar.mymal.downloader.DownloadURL;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

/**
 * Created by stopp on 18/07/2017.
 */

public class Reviews {

    boolean anime;
    int id;
    String url,html;
    List<Review> rew=new ArrayList<>();
    DownloadURL async;
    View view;
    Context context;
    public List<Review> getRew() {
        return rew;
    }

    public DownloadURL getAsync() {
        return async;
    }

    void getHtml() {
            async=new DownloadURL(new DownloadURL.AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    html=output;
                    int last=0;
                    //rew.add(new Review(html.substring(last,html.indexOf("button_form",last))));
                    while(true){
                        last=html.indexOf("borderDark",last+1);
                        if(last==-1)break;
                        rew.add(new Review(html.substring(last,html.indexOf("button_form",last))));
                    }
                    if(context!=null) ViewLoader.loadAdapterView(view,new ReviewAdapter(context,Reviews.this),new LinearLayoutManager(context));
                }
            });
        async.executeOnExecutor(THREAD_POOL_EXECUTOR  ,url);
    }

    public Reviews(int id, boolean anime, Context context, View view) {
        this.anime = anime;
        this.id = id;
        this.view = view;
        this.context = context;
        url=String.format("https://myanimelist.net/%s/%d/x/reviews",anime?"anime":"manga",id);
        getHtml();
    }

    public Reviews(int id, boolean anime) {
        this(id,anime,null,null);
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
                ", rew=" + rew +
                '}';
    }

    public class Review {
        String html,user,review,episodeSeen,imageURL;
        int helpful;
        Date date;
        List<Integer> scores=new ArrayList<>();

        void setUser() {
            user= Html.fromHtml(html.substring(html.indexOf("/profile/")+9,html.indexOf('"',html.indexOf("/profile/")+9))).toString();
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

}
