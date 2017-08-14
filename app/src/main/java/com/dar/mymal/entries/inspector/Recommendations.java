package com.dar.mymal.entries.inspector;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.View;

import com.dar.mymal.ViewLoader;
import com.dar.mymal.adapters.dataEntry.RecommendationAdapter;
import com.dar.mymal.downloader.DownloadURL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stopp on 18/07/2017.
 */

public class Recommendations {

    boolean anime;
    int id;
    String url,html;
    List<Recommendation> rec=new ArrayList<>();
    DownloadURL async;
    View view;
    Context context;
    void getHtml() {
            async=new DownloadURL(new DownloadURL.AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    html=output.substring(output.indexOf("Make a recommendation"),output.lastIndexOf("</table>"));
                    int last=0;
                    while(true){
                        last=html.indexOf("<table",last+1);
                        if(last<0)break;
                        rec.add(new Recommendation(html.substring(html.indexOf("<table",last),html.indexOf("</table",last)),anime));
                    }
                    if(context!=null) ViewLoader.loadAdapterView(view,new RecommendationAdapter(context,Recommendations.this),new LinearLayoutManager(context));
                }
            });
        async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);
    }

    public DownloadURL getAsync() {
        return async;
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

    public Recommendations(int id, boolean anime, Context context, View view) {
        this.anime = anime;
        this.id = id;
        this.view = view;
        this.context = context;
        url=String.format("https://myanimelist.net/%s/%d/x/userrecs",anime?"anime":"manga",id);
        getHtml();
    }

    public Recommendations(int id, boolean anime) {
        this(id,anime,null,null);
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
    public class Recommendation {
        String html,imageURL,title;
        int id,howMany;
        boolean isAnime=true;
        List<SingleRecommendation> rec=new ArrayList<>();

        public  String getImageURL() {
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
        public class SingleRecommendation {
            String user,text;

            public SingleRecommendation(String user, String text) {
                this.user = user;
                this.text = text;
            }

            public String getUser() {
                return user;
            }

            public String getText() {
                return text;
            }

            @Override
            public String toString() {
                return "SingleRecommendation{" +
                        "user='" + user + '\'' +
                        ", text='" + text + '\'' +
                        '}';
            }

        }
    }
}
