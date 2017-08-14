package com.dar.mymal.entries.searches;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.dar.mymal.ViewLoader;
import com.dar.mymal.adapters.seachEntry.UserAdapter;
import com.dar.mymal.downloader.DownloadURL;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by atopp on 09/08/2017.
 */
public class User {
    String html,query,url,location;
    int page,maxAge,minAge,genderType;
    DownloadURL async;
    List<UserSearchEntry>users=new ArrayList<>();
    View view;
    Context context;

    public String getQuery() {
        return query;
    }

    public String getUrl() {
        return url;
    }

    public String getLocation() {
        return location;
    }

    public int getPage() {
        return page;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public int getMinAge() {
        return minAge;
    }

    public int getGenderType() {
        return genderType;
    }

    public DownloadURL getAsync() {
        return async;
    }

    public List<UserSearchEntry> getUsers() {
        return users;
    }

    public User(String query, String location, int page, int maxAge, int minAge, int genderType) {
        this.query = query;
        this.location = location;
        this.page = page;
        this.maxAge = maxAge;
        this.minAge = minAge;
        this.genderType = genderType;
        this.url=String.format("https://myanimelist.net/users.php?q=%s&loc=%s&agelow=%d&agehigh=%d&g=%d&show=%d",query.replace(' ','+'),location,minAge,maxAge,genderType,page*24);
        downloadURL();
    }
    public User(String query, int page,View view,Context context) {
        this(query,"",page,0,0,0);
        this.view=view;
        this.context=context;
    }
    public User(String query, int page) {
        this(query,"",page,0,0,0);
    }
    void splitHTML(){
        if(html.contains("</div>No users found<br>"))return;
        String[] ent=html.split("<td ");
        for(int a=0;a<ent.length;a++)
            users.add(new UserSearchEntry(ent[a]));
        if(view!=null) ViewLoader.loadAdapterView(view,new UserAdapter(context,users),new LinearLayoutManager(context));
    }
    void downloadURL(){
        async=new DownloadURL(new DownloadURL.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                html=output;

                    try {
                        if(html.substring(0,500).contains("<title>Users - MyAnimeList.net")) {
                        html = html.substring(html.lastIndexOf("<table"));
                        html = html.substring(0, html.lastIndexOf("</table"));
                        html = html.substring(html.indexOf("<td ") + 4);

                        splitHTML();
                        }else if(context!=null){ViewLoader.loadAdapterView(view,null,new LinearLayoutManager(context));}
                    } catch (StringIndexOutOfBoundsException e) {
                        Log.e("OnMALError", "Unable to find user: " + query);
                    }

            }
        });
        async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);
    }
    public class UserSearchEntry{
        String imageURL,name,desc,code;

        public UserSearchEntry(String code) {
            this.code = code;
            loadImageURL();
            loadName();
            loadDesc();
        }

        @Override
        public String toString() {
            return "UserSearchEntry{" +
                    "imageURL='" + imageURL + '\'' +
                    ", name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    '}';
        }

        private void loadName() {
            String h=code.substring(code.indexOf("<a href"));
            h=h.substring(h.indexOf('>')+1);
            name=h.substring(0,h.indexOf('<'));
        }
        private void loadImageURL() {
            String h=code.substring(code.indexOf("<img")+10);
            imageURL=h.substring(0,h.indexOf('"'));
        }
        private void loadDesc() {
            String h=code.substring(code.indexOf("<small>")+7);
            desc=h.substring(0,h.indexOf('<'));
        }

        public String getImageURL() {
            return imageURL;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }
    }
}
