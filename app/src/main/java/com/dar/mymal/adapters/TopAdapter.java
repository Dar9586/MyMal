package com.dar.mymal.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dar.mymal.EntryActivity;
import com.dar.mymal.R;
import com.dar.mymal.downloader.DownloadImage;
import com.dar.mymal.entries.top.Top;
import com.dar.mymal.global.Settings;
import com.dar.mymal.utils.MALUtils;

import java.util.List;

/**
 * Created by atopp on 14/08/2017.
 */

public class TopAdapter extends RecyclerView.Adapter<TopAdapter.ViewHolder> {
    private List<Top.TopEntry> mDataset;
    Context context;
    List<String>files;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgView;
        public TextView title,id,score,type,date,member,position;
        public LinearLayout master;
        public ViewHolder(View v) {
            super(v);
            id=(TextView)v.findViewById(R.id.id);
            position=(TextView)v.findViewById(R.id.position);
            imgView=(ImageView)v.findViewById(R.id.image);
            title=(TextView)v.findViewById(R.id.title);
            type=(TextView)v.findViewById(R.id.type);
            date=(TextView)v.findViewById(R.id.date);
            member=(TextView)v.findViewById(R.id.members);
            score=(TextView)v.findViewById(R.id.score);
            master=(LinearLayout)v.findViewById(R.id.master_layout);
        }
    }

    public TopAdapter(Context cont, List<Top.TopEntry> myDataset) {
        this.context=cont;
        this.mDataset = myDataset;
        this.files= MALUtils.getCacheFile();
    }

    @Override
    public TopAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TopAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.top_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(final TopAdapter.ViewHolder holder, final int position) {
        final Top.TopEntry ent=mDataset.get(position);
        if(files.contains((ent.isAnime()?"A":"M")+ent.getId()+".jpg")){
            holder.imgView.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache/"+(ent.isAnime()?"A":"M")+ent.getId()+".jpg")));
        }else if(!Settings.isUsingLessData()){
            new DownloadImage(holder.imgView,Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache","A"+Integer.toString(ent.getId())).execute(ent.getImage());
        }
        holder.title.setText(Html.fromHtml(ent.getTitle()).toString());
        holder.id.setText(Integer.toString(ent.getId()));
        holder.position.setText(Integer.toString(ent.getPos()));
        holder.type.setText(Html.fromHtml(ent.getDesc()[0]).toString());
        holder.date.setText(Html.fromHtml(ent.getDesc()[1]).toString());
        holder.member.setText(Html.fromHtml(ent.getDesc()[2]).toString());
        holder.score.setText(String.format("%2.2f",ent.getScore()));
        holder.master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(view.getContext(), EntryActivity.class);
                i.putExtra("ENTRY_ID",ent.getId());
                i.putExtra("ENTRY_ISANIME",ent.isAnime());
                i.putExtra("ENTRY_TITLE",ent.getTitle());
                view.getContext().startActivity(i);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
