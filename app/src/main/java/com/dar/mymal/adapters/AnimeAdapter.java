package com.dar.mymal.adapters;

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
import com.dar.mymal.ListLoader;
import com.dar.mymal.R;
import com.dar.mymal.entries.Anime;
import com.dar.mymal.utils.LoginData;
import com.dar.mymal.utils.MalAPI;
import com.dar.mymal.utils.downloader.DownloadImage;

import java.util.List;

/**
 * Created by stopp on 03/07/2017.
 */

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.ViewHolder> {
    private Anime[] mDataset;
    private boolean useLessData;
    Context context;
    List<String>files;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgView;
        public TextView title,id,status,progress;
        public Button adder;
        public LinearLayout master;
        public ViewHolder(View v) {
            super(v);
            imgView=(ImageView)v.findViewById(R.id.image);
            title=(TextView)v.findViewById(R.id.title);
            id=(TextView)v.findViewById(R.id.entry_id);
            status=(TextView)v.findViewById(R.id.status);
            progress=(TextView)v.findViewById(R.id.progress);
            adder=(Button)v.findViewById(R.id.adder);
            master=(LinearLayout)v.findViewById(R.id.master_layout);
        }
    }

    public AnimeAdapter(Context cont,Anime[] myDataset, boolean useLessData, List<String> files) {
        this.context=cont;
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Anime ent=mDataset[position];
        if(files.contains("A"+ent.getID()+".jpg")){
            holder.imgView.setBackground(new BitmapDrawable(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache/A"+ent.getID()+".jpg")));
        }else if(!useLessData){
            new DownloadImage(holder.imgView,Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache","A"+Integer.toString(ent.getID())).execute(ent.getImageURL());
        }
        holder.title.setText(Html.fromHtml(ent.getTitle()).toString());
        holder.id.setText(Integer.toString(ent.getID()));
        holder.status.setText(ent.getType(true)+" - "+ent.getStatus(true));
        holder.progress.setText(ent.getMyEpisodes()+"/"+ent.getEpisodes());
        if(LoginData.getUsername()== ListLoader.actualUser) {
            holder.progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   holder.adder.setVisibility(View.VISIBLE);
                  holder.progress.setVisibility(View.GONE);
             }
            });
            holder.adder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ent.setMyEpisodes(ent.getMyEpisodes() + 1);
                    MalAPI.update(context,ent);
                    mDataset[position] = ent;
                    holder.progress.setText(ent.getMyEpisodes() + "/" + ent.getEpisodes());
                }
            });
            holder.master.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.adder.getVisibility() == View.VISIBLE) {
                        holder.adder.setVisibility(View.GONE);
                        holder.progress.setVisibility(View.VISIBLE);
                    } else {
                        Intent i=new Intent(view.getContext(), EntryActivity.class);
                        i.putExtra("ENTRY_ID",ent.getID());
                        i.putExtra("ENTRY_ISANIME",ent.isAnime());
                        view.getContext().startActivity(i);
                    }
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}
