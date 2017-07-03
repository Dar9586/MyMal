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
import com.dar.mymal.entries.Manga;
import com.dar.mymal.utils.downloader.DownloadImage;

import java.util.List;

/**
 * Created by stopp on 03/07/2017.
 */

public class MangaAdapter extends RecyclerView.Adapter<MangaAdapter.ViewHolder> {
    private Manga[] mDataset;
    private boolean useLessData;
    List<String>files;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgView;
        public TextView title,id,status,progress1,progress2;

        public ViewHolder(View v) {
            super(v);
            imgView=(ImageView)v.findViewById(R.id.image);
            title=(TextView)v.findViewById(R.id.title);
            id=(TextView)v.findViewById(R.id.entry_id);
            status=(TextView)v.findViewById(R.id.status);
            progress1=(TextView)v.findViewById(R.id.progress1);
            progress2=(TextView)v.findViewById(R.id.progress2);
        }
    }

    public MangaAdapter(Manga[] myDataset, boolean useLessData, List<String> files) {
        this.mDataset = myDataset;
        this.useLessData=useLessData;
        this.files=files;
    }

    @Override
    public MangaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_layout_manga, parent, false);
        ViewHolder vh = new ViewHolder(myView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Manga ent=mDataset[position];
        if(files.contains(ent.getID()+".jpg")){
            holder.imgView.setBackground(new BitmapDrawable(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache/"+ent.getID()+".jpg")));
        }
        else if(!useLessData){
            new DownloadImage(holder.imgView,Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache",Integer.toString(ent.getID())).execute(ent.getImageURL());
        }
        holder.title.setText(Html.fromHtml(ent.getTitle()).toString());
        holder.id.setText(Integer.toString(ent.getID()));
        holder.status.setText(ent.getType(false)+" - "+ent.getStatus(false));
        holder.progress1.setText(ent.getMyChapter()+"/"+ent.getChapter());
        holder.progress2.setText(ent.getMyVolumes()+"/"+ent.getVolumes());
    }
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}
