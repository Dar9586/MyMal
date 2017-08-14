package com.dar.mymal.entries.inspector;

import android.content.Context;
import android.view.View;

/**
 * Created by atopp on 13/08/2017.
 */

public class MasterInspector {
    IDInspector idInspector;
    Recommendations recommendations;
    Reviews reviews;
    Characters characters;
    int id;
    boolean anime;
    private View views[];
    Context context;

    public MasterInspector(int id, boolean anime, View[] views, Context context) {
        this.id = id;
        this.anime = anime;
        this.views = views;
        this.context = context;
        idInspector=new IDInspector(id,anime,context,new View[]{views[0],views[1],views[2],views[7]});
        recommendations=new Recommendations(id,anime,context,views[3]);
        reviews=new Reviews(id,anime,context,views[4]);
        characters=new Characters(id,anime,context,new View[]{views[5],views[6]});
    }
}
