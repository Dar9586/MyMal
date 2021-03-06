package com.dar.mymal.adapters.listEntries;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dar.mymal.EntryActivity;
import com.dar.mymal.R;
import com.dar.mymal.entries.api.Manga;
import com.dar.mymal.global.EntryList;
import com.dar.mymal.global.Settings;
import com.dar.mymal.utils.MALUtils;
import com.dar.mymal.utils.MalAPI;
import com.dar.mymal.downloader.DownloadImage;

import java.util.List;

/**
 * Created by stopp on 03/07/2017.
 */

public class MangaAdapter extends RecyclerView.Adapter<MangaAdapter.ViewHolder> {
    private int actual;
    Context context;
    List<String>files;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgView;
        public TextView title,id,status,progress1,progress2,score;
        public Button adder1,adder2;
        public LinearLayout master;
        public ViewHolder(View v) {
            super(v);
            imgView=(ImageView)v.findViewById(R.id.image);
            title=(TextView)v.findViewById(R.id.title);
            id=(TextView)v.findViewById(R.id.entry_id);
            status=(TextView)v.findViewById(R.id.status);
            progress1=(TextView)v.findViewById(R.id.progress1);
            progress2=(TextView)v.findViewById(R.id.progress2);
            adder1=(Button)v.findViewById(R.id.adder1);
            adder2=(Button)v.findViewById(R.id.adder2);
            score=(TextView)v.findViewById(R.id.score);
            master=(LinearLayout)v.findViewById(R.id.master_layout);
        }
    }

    public MangaAdapter(Context cont,int actual) {
        this.context=cont;
        this.actual=actual;
        this.files= MALUtils.getCacheFile();
    }

    @Override
    public MangaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_layout_manga, parent, false);
        ViewHolder vh = new ViewHolder(myView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        final Manga ent=(Manga) EntryList.getActualList()[1][actual].get(position);
        if(files.contains("M"+ent.getID()+".jpg")){
            holder.imgView.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache/M"+ent.getID()+".jpg")));
        }
        else if(!Settings.isUsingLessData()){
            new DownloadImage(holder.imgView,Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache","M"+Integer.toString(ent.getID())).execute(ent.getImageURL());
        }
        holder.title.setText(Html.fromHtml(ent.getTitle()).toString());
        holder.id.setText(Integer.toString(ent.getID()));
        holder.score.setText(Integer.toString(ent.getScore()));
        holder.status.setText(ent.getType(false)+" - "+ent.getStatus(false));
        holder.progress1.setText(ent.getMyChapter() + "/" + (ent.getChapter()==0?"-":ent.getChapter()));
        holder.progress2.setText(ent.getMyVolumes() + "/" + (ent.getVolumes()==0?"-":ent.getVolumes()));
        holder.master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.adder1.getVisibility() == View.VISIBLE||holder.adder2.getVisibility() == View.VISIBLE) {
                    holder.adder1.setVisibility(View.GONE);
                    holder.progress1.setVisibility(View.VISIBLE);
                    holder.adder2.setVisibility(View.GONE);
                    holder.progress2.setVisibility(View.VISIBLE);}
                else{
                    Intent i=new Intent(view.getContext(), EntryActivity.class);
                    i.putExtra("ENTRY_ID",ent.getID());
                    i.putExtra("ENTRY_ISANIME",ent.isAnime());
                    i.putExtra("ENTRY_TITLE",ent.getTitle());
                    view.getContext().startActivity(i);
                }
            }
        });
        if(EntryList.isSame()&&(ent.getMyStatus()!=2||ent.getRewatch())) {
            holder.progress1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.adder1.setVisibility(View.VISIBLE);
                    holder.progress1.setVisibility(View.GONE);
                }
            });
            holder.progress2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.adder2.setVisibility(View.VISIBLE);
                    holder.progress2.setVisibility(View.GONE);
                }
            });
            holder.adder1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ent.getChapter()==0||ent.getMyChapter()<ent.getChapter()){
                    ent.setMyChapter(ent.getMyChapter() + 1);
                    MalAPI.update(ent);
                        holder.progress1.setText(ent.getMyChapter() + "/" + (ent.getChapter()==0?"-":ent.getChapter()));
                    }
                }
            });
            holder.adder2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ent.getVolumes()==0||ent.getMyVolumes()<ent.getVolumes()){
                    ent.setMyVolumes(ent.getMyVolumes() + 1);
                    MalAPI.update(ent);
                        holder.progress2.setText(ent.getMyVolumes() + "/" + (ent.getVolumes()==0?"-":ent.getVolumes()));}
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return EntryList.getActualList()[1][actual].size();
    }

}
