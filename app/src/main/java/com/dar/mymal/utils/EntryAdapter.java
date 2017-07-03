package com.dar.mymal.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dar.mymal.R;
import com.dar.mymal.entries.Anime;
import com.dar.mymal.entries.Entry;
import com.dar.mymal.entries.Manga;
import com.dar.mymal.utils.downloader.DownloadImage;

import java.io.File;
import java.util.List;

/**
 * Created by stopp on 03/07/2017.
 */

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ViewHolder> {
    private Entry[] mDataset;
    private boolean useLessData,anime=true;
    List<String>files;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imgView;
        public TextView title,id,status,progress;

        public ViewHolder(View v) {
            super(v);
            imgView=(ImageView)v.findViewById(R.id.image);
            title=(TextView)v.findViewById(R.id.title);
            id=(TextView)v.findViewById(R.id.entry_id);
            status=(TextView)v.findViewById(R.id.status);
            progress=(TextView)v.findViewById(R.id.progress);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public EntryAdapter(Entry[] myDataset, boolean useLessData, List<String> files) {
        this.mDataset = myDataset;
        this.useLessData=useLessData;
        this.files=files;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EntryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_layout_anime, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(myView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Entry ent=mDataset[position];
        if(!useLessData){
            if(files.contains(ent.getID()+".jpg")){
                holder.imgView.setBackground(new BitmapDrawable(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache/"+ent.getID()+".jpg")));
            }
            else if(!useLessData){
                new DownloadImage(holder.imgView,Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache",Integer.toString(ent.getID())).execute(ent.getImageURL());
            }
        }
        holder.title.setText(Html.fromHtml(ent.getTitle()).toString());
        try{holder.id.setText(ent.getID());}catch(Exception e){
            Log.e("IDLOADERERROR",e.getMessage());}
        holder.status.setText(ent.getType(true)+" - "+ent.getStatus(anime));
        if(anime){
            holder.progress.setText(((Anime)ent).getMyEpisodes()+"/"+((Anime)ent).getEpisodes());}
        else{
            /*try{((TextView)myView.findViewById(R.id.progress1)).setText(((Manga)ent).getMyChapter()+"/"+((Manga)ent).getChapter());
                ((TextView)myView.findViewById(R.id.progress2)).setText(((Manga)ent).getMyVolumes()+"/"+((Manga)ent).getVolumes());}catch(Exception e){Log.e("IDLOADERERROR",e.getMessage());}*/
        }

    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }




}
