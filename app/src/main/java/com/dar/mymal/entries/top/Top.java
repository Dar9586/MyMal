package com.dar.mymal.entries.top;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.dar.mymal.ViewLoader;
import com.dar.mymal.adapters.TopAdapter;
import com.dar.mymal.downloader.DownloadURL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by atopp on 14/08/2017.
 */


public class Top {
    public enum TopType{ALL,POPULAR,AIRING,UPCOMING,FAVORITE}
    private int page;
    private boolean anime;
    private String html;
    private List<TopEntry> tops=new ArrayList<>();
    private DownloadURL async;
    private View view;
    private Context context;
    private String[]types={"all","bypopularity","airing","upcoming","favorite"};
    public Top(boolean anime,int page,int type,View view,Context context) {
        this.view=view;
        this.context=context;
        this.page = page;
        this.anime = anime;
        async=new DownloadURL(new DownloadURL.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                html=output;
                html=html.substring(html.indexOf("<table"),html.lastIndexOf("</table>")+8);
                separateEntries();
            }
        });
        async.execute(String.format("https://myanimelist.net/top%s.php?type=%s&limit=%d",anime?"anime":"manga",types[type],page*50));
    }

    private void separateEntries() {
        int pos=0,count=1;
        while((pos=html.indexOf("<tr class=\"ranking-list\"",pos))!=-1){
            tops.add(new TopEntry(anime,page*50+count++,html.substring(pos,(pos=html.indexOf("</tr",pos)))));
        }
        if(context!=null) ViewLoader.loadAdapterView(view,new TopAdapter(context,tops),new LinearLayoutManager(context));
    }


    public class TopEntry{
        boolean anime;
        int id,pos;
        float score;
        String desc[],code,title,image;
        public TopEntry(boolean anime, int pos, String code) {
            this.anime = anime;
            this.pos = pos;
            this.code=code;
            findImage();
            findTitle();
            findId();
            findDesc();
            findScore();
            System.out.println(toString());
        }

        @Override
        public String toString() {
            return "TopEntry{" +
                    "anime=" + anime +
                    ", id=" + id +
                    ", pos=" + pos +
                    ", score=" + score +
                    ", title='" + title + '\'' +
                    ", image='" + image + '\'' +
                    ", desc='" + desc + '\'' +
                    '}';
        }

        private void findScore(){
            String h=code.substring(code.indexOf('>',code.indexOf("\"text "))+1);
            if(h.startsWith("N/A"))score=-1;
            else score=Float.parseFloat(h.substring(0,h.indexOf('<')));
        }
        private void findDesc(){
            String h=code.substring(code.indexOf('>',code.indexOf("class=\"information"))+2);
            h=h.substring(0,h.indexOf("</div>"));
            desc=h.trim().split("<br>");
        }
        private void findId(){
            String h=code.substring(code.indexOf("#area")+5);
            id=Integer.parseInt(h.substring(0,h.indexOf('"')));
        }
        private void findTitle() {
            String h=code.substring(code.indexOf('>',code.lastIndexOf("hoverinfo_trigger"))+1);
            title=h.substring(0,h.indexOf('<'));
        }

        private void findImage() {
            String h=code.substring(code.indexOf("data-src")+10);
            image=h.substring(0,h.indexOf('"'));
        }

        public String getTitle() {
            return title;
        }

        public boolean isAnime() {
            return anime;
        }

        public int getId() {
            return id;
        }

        public int getPos() {
            return pos;
        }

        public String[] getDesc() {
            return desc;
        }

        public float getScore() {
            return score;
        }

        public String getImage() {
            return image;
        }
    }
}
