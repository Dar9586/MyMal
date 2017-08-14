package com.dar.mymal.entries.searches;

import android.content.Context;
import android.view.View;

import com.dar.mymal.downloader.DownloadURL;

/**
 * Created by atopp on 12/08/2017.
 */

public class MasterSearch {
    AnimeManga anime,manga;
    People people;
    User user;
    Character character;
    String query;
    DownloadURL async;
    private View views[]=new View[5];
    Context context;
    public MasterSearch(final String query,final View views[],Context context) {
        this.context=context;
        this.views=views;
        this.query = query;
        anime=new AnimeManga(true,query,views[0],context);
        manga=new AnimeManga(false,query,views[1],context);
        character=new Character(query,0,views[2],context);
        people=new People(query,0,views[3],context);
        user=new User(query,0,views[4],context);
    }
}
