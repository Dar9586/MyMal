package com.dar.mymal.adapters;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dar.mymal.R;
import com.dar.mymal.entries.Anime;
import com.dar.mymal.utils.downloader.DownloadImage;

import java.util.List;

/**
 * Created by stopp on 03/07/2017.
 */

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.ViewHolder> {
    private Anime[] mDataset;
    private boolean useLessData;
    List<String>files;
    public static class ViewHolder extends RecyclerView.ViewHolder {
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

    public AnimeAdapter(Anime[] myDataset, boolean useLessData, List<String> files) {
        this.mDataset = myDataset;
        this.useLessData=useLessData;
        this.files=files;
    }

    @Override
    public AnimeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        //View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_layout_anime, parent, false);
        //ViewHolder vh = new ViewHolder(myView);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_layout_anime, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Anime ent=mDataset[position];
        if(files.contains(ent.getID()+".jpg")){
            holder.imgView.setBackground(new BitmapDrawable(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache/"+ent.getID()+".jpg")));
        }else if(!useLessData){
            new DownloadImage(holder.imgView,Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache",Integer.toString(ent.getID())).execute(ent.getImageURL());
        }
        holder.title.setText(Html.fromHtml(ent.getTitle()).toString());
        holder.id.setText(Integer.toString(ent.getID()));
        holder.status.setText(ent.getType(true)+" - "+ent.getStatus(true));
        holder.progress.setText(ent.getMyEpisodes()+"/"+ent.getEpisodes());
    }
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}
