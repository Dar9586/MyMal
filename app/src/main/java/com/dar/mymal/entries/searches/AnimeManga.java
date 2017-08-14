package com.dar.mymal.entries.searches;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.dar.mymal.ViewLoader;
import com.dar.mymal.adapters.seachEntry.AnimeMangaAdapter;
import com.dar.mymal.downloader.DownloadURL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnimeManga {
    private List<SearchEntry>entries=new ArrayList<>();
    private int type,score,status,magazineID,startDay,startMonth,startYear,endDay,endMonth,endYear,orderType,rated,page,publisher;
    //exclude:true=Exclude genres selected/false=Include genres selected[1:0]
    //ascend:true=Ascend/false=Descend[2:1]
    private boolean exclude,ascend,anime;
    private char columns[]={'a','b','c','d','e','f','g'};
    private int genre[];
    private String query,url,html;
    private DownloadURL async;
    private View view;
    private Context context;

    public String getQuery() {
        return query;
    }

    public DownloadURL getAsync() {
        return async;
    }

    public List<SearchEntry> getEntries() {
        return entries;
    }

    public int getType() {
        return type;
    }

    public int getScore() {
        return score;
    }

    public int getStatus() {
        return status;
    }

    public int getMagazineID() {
        return magazineID;
    }

    public int getStartDay() {
        return startDay;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public int getStartYear() {
        return startYear;
    }

    public int getEndDay() {
        return endDay;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public int getEndYear() {
        return endYear;
    }

    public int getOrderType() {
        return orderType;
    }

    public int getRated() {
        return rated;
    }

    public int getPage() {
        return page;
    }

    public int getPublisher() {
        return publisher;
    }

    public boolean isExclude() {
        return exclude;
    }

    public boolean isAscend() {
        return ascend;
    }

    public boolean isAnime() {
        return anime;
    }

    public char[] getColumns() {
        return columns;
    }

    public int[] getGenre() {
        return genre;
    }

    public String getUrl() {
        return url;
    }

    public AnimeManga(String query, int type, int score, int status, int magazineID, int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear, int orderType, int page, boolean exclude, boolean ascend, int[] genre) {
        loaderManga(query, type, score, status, magazineID, startDay, startMonth, startYear, endDay, endMonth, endYear, orderType, page, exclude, ascend, genre);
    }
    public AnimeManga(String query, int type, int score, int status, int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear, int orderType, int rated, int page, int publisher, boolean exclude, boolean ascend, int[] genre) {
        loaderAnime(query,type,score,status,startDay,startMonth,startYear,endDay,endMonth,endYear,orderType,rated,page,publisher,exclude,ascend,genre);
    }
    void loaderManga(String query, int type, int score, int status, int magazineID, int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear, int orderType, int page, boolean exclude, boolean ascend, int[] genre) {
        this.query=query;
        this.type = type;
        this.score = score;
        this.status = status;
        this.magazineID = magazineID;
        this.startDay = startDay;
        this.startMonth = startMonth;
        this.startYear = startYear;
        this.endDay = endDay;
        this.endMonth = endMonth;
        this.endYear = endYear;
        this.orderType = orderType;
        this.page = page;
        this.exclude = exclude;
        this.ascend = ascend;
        this.genre = genre;
        this.anime=false;
        mainLoad();
    }

    public AnimeManga(boolean anime, String query) {
        if(anime)
            loaderAnime(query,0,0,0,0,0,0,0,0,0,0,0,0,0,false,false,new int[]{});
        else
            loaderManga(query,0,0,0,0,0,0,0,0,0,0,0,0,false,false,new int[]{});
    }
    public AnimeManga(boolean anime, String query, View view, Context cont) {
        this.view=view;
        this.context=cont;
        if(anime)
            loaderAnime(query,0,0,0,0,0,0,0,0,0,0,0,0,0,false,false,new int[]{});
        else
            loaderManga(query,0,0,0,0,0,0,0,0,0,0,0,0,false,false,new int[]{});
    }
    void loaderAnime(String query, int type, int score, int status, int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear, int orderType, int rated, int page, int publisher, boolean exclude, boolean ascend, int[] genre) {
        this.query=query;
        this.type = type;
        this.score = score;
        this.status = status;
        this.startDay = startDay;
        this.startMonth = startMonth;
        this.startYear = startYear;
        this.endDay = endDay;
        this.endMonth = endMonth;
        this.endYear = endYear;
        this.orderType = orderType;
        this.rated = rated;
        this.page = page;
        this.publisher = publisher;
        this.exclude = exclude;
        this.ascend = ascend;
        this.genre = genre;
        this.anime=true;
        mainLoad();
    }
    public AnimeManga(String url) {
        this.url = url;
        this.anime=url.contains("anime.php");
        getHTML();
    }

    void mainLoad(){
        generateURL();
        getHTML();
    }

    private void splitEntries() {
        String[]entries=html.split("<tr>");
        for(int a=0;a<entries.length;a++)
            this.entries.add(new SearchEntry(entries[a],anime));
        if(view!=null) ViewLoader.loadAdapterView(view,new AnimeMangaAdapter(context,this.entries),new LinearLayoutManager(context));
    }

    void getHTML(){
        async=new DownloadURL(new DownloadURL.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                html=output;
                try {
                    html = html.substring(html.indexOf("js-categories-seasonal"));
                    html = html.substring(0, html.indexOf("</table>"));
                    html = html.substring(html.indexOf("<tr>") + 4);
                    html = html.substring(html.indexOf("<tr>") + 4);
                    splitEntries();
                }catch(StringIndexOutOfBoundsException e){Log.e("OnmALError","Unable to find "+(anime?"anime":"manga")+": "+query);}
            }
        });
        async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);

    }
    void generateURL(){
        StringBuilder url=new StringBuilder();
        url.append(String.format("https://myanimelist.net/%s.php?q=%s&type=%d&score=%d&status=%d",anime?"anime":"manga",query,type,score,status));
        if(anime){
            url.append(String.format("&p=%d&r=%d",publisher,rated));
        }else{
            url.append(String.format("&mid=%d",magazineID));
        }
        url.append(String.format("&sm=%d&sd=%d&sy=%d&em=%d&ed=%d&ey=%d",startMonth,startDay,startYear,endMonth,endDay,endYear));
        for (int a=0;a<columns.length;a++){url.append(String.format("&c[%d]=%c",a,columns[a]));}
        url.append(String.format("&gx=%d",exclude?1:0));
        for (int a=0;a<genre.length;a++){url.append(String.format("&genre[%d]=%d",a,genre[a]));}
        url.append(String.format("&o=%d&w=%d&show=%d",orderType,ascend?2:1,page*50));
        this.url=url.toString();
        System.out.println(this.url);
    }



    public class SearchEntry{
        String html,title,description,type,episodes,score,imageURL,members,rated,volumes,chapters,start,end;
        boolean anime;
        int id;

        public SearchEntry(String html, boolean anime) {
            this.html = html;
            this.anime = anime;
            loadID();
            loadTitle();
            loadDescription();
            loadImage();
            loadInfo();

        }
        void loadID(){
            String h=html.substring(html.indexOf("myanimelist.net")+22);
            id=Integer.parseInt(h.substring(0,h.indexOf('/')));
        }
        void loadTitle(){
            String h=html.substring(html.indexOf("<strong>")+8);
            title=h.substring(0,h.indexOf('<'));
        }
        void loadDescription(){
            String h=html.substring(html.indexOf("<div class=\"pt4\">")+17);
            description=h.substring(0,h.indexOf('<'));
        }
        void loadImage(){
            String h=html.substring(html.indexOf("data-src")+10);
            imageURL=h.substring(0,h.indexOf('"'));
        }

        public String getTitle() {
            return title;
        }

        public int getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public String getType() {
            return type;
        }

        public String getEpisodes() {
            return episodes;
        }

        public String getScore() {
            return score;
        }

        public String getImageURL() {
            return imageURL;
        }

        public String getMembers() {
            return members;
        }

        public String getRated() {
            return rated;
        }

        public String getVolumes() {
            return volumes;
        }

        public String getChapters() {
            return chapters;
        }

        public String getStart() {
            return start;
        }

        public String getEnd() {
            return end;
        }

        public boolean isAnime() {
            return anime;
        }

        void loadInfo(){
            String h=html.substring(html.lastIndexOf("</div>")+10);
            String k[]=h.split("\n");
            type=k[1].trim();
            if(anime){
                episodes=k[3].trim();
                score=k[5].trim();
                start=k[7].trim();
                end=k[9].trim();
                members=k[11].trim();
                rated=k[13].trim();
            }
            else{
                volumes=k[3].trim();
                chapters=k[5].trim();
                score=k[7].trim();
                start=k[9].trim();
                end=k[11].trim();
                members=k[13].trim();
            }

        }
        @Override
        public String toString() {
            return "SearchEntry{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", type='" + type + '\'' +
                    ", episodes='" + episodes + '\'' +
                    ", score='" + score + '\'' +
                    ", imageURL='" + imageURL + '\'' +
                    ", members='" + members + '\'' +
                    ", rated='" + rated + '\'' +
                    ", volumes='" + volumes + '\'' +
                    ", chapters='" + chapters + '\'' +
                    ", start='" + start + '\'' +
                    ", end='" + end + '\'' +
                    ", anime=" + anime +
                    '}';
        }
    }

}
